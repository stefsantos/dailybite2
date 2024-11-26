package com.example.dailybite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private TextView stepsTextView, timeTextView, mileTextView, kcalTextView, stepCountTargetTextView;
    private Button startStopButton;
    private CircularProgressBar progressBar;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor, accelerometer;

    private int stepCount = 0;
    private int sessionSteps = 0; // Track steps only for this session
    private boolean isWalking = false;
    private boolean isShakeStepDetected = false;

    private long startTime = 0L;
    private long timePaused = 0L;
    private Handler handler = new Handler();
    private Runnable runnable;

    private final float stepLengthInMeters = 0.762f; // Approximate step length
    private int stepCountTarget = 5000; // Default step goal

    private static final String SHARED_PREFS = "PedometerPrefs";
    private static final String KEY_STEP_GOAL = "StepGoal";

    // Accelerometer variables for motion detection
    private float lastX, lastY, lastZ;
    private boolean isFirstShake = true;
    private float accelerationThreshold = 12.0f; // Sensitivity for motion detection

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        // Initialize UI components
        stepsTextView = view.findViewById(R.id.steps_text_view);
        timeTextView = view.findViewById(R.id.time_text_view);
        mileTextView = view.findViewById(R.id.mile_text_view);
        kcalTextView = view.findViewById(R.id.kcal_text_view);
        stepCountTargetTextView = view.findViewById(R.id.step_target);
        startStopButton = view.findViewById(R.id.start_stop_button);
        progressBar = view.findViewById(R.id.progressBar);

        // Load saved step goal
        SharedPreferences prefs = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        stepCountTarget = prefs.getInt(KEY_STEP_GOAL, stepCountTarget);
        updateStepGoalUI();

        // Initialize SensorManager
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Start/Stop button logic
        startStopButton.setOnClickListener(v -> {
            if (isWalking) {
                stopWalking();
            } else {
                startWalking();
            }
        });

        // Step goal click listener
        stepCountTargetTextView.setOnClickListener(v -> showStepGoalDialog());

        return view;
    }

    private void startWalking() {
        isWalking = true;
        startStopButton.setText("Stop");
        startTime = System.currentTimeMillis() - timePaused;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isWalking) {
                    updateUI();
                    sendStepDataToService();
                    handler.postDelayed(this, 1000); // Update every second
                }
            }
        };
        handler.post(runnable);

        // Register accelerometer for shake detection
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        // Start the pedometer service
        Intent serviceIntent = new Intent(requireContext(), PedometerService.class);
        requireContext().startForegroundService(serviceIntent);
    }

    private void stopWalking() {
        isWalking = false;
        startStopButton.setText("Start");
        timePaused = System.currentTimeMillis() - startTime;
        handler.removeCallbacks(runnable);

        // Unregister accelerometer
        sensorManager.unregisterListener(this, accelerometer);

        // Stop the pedometer service
        Intent serviceIntent = new Intent(requireContext(), PedometerService.class);
        requireContext().stopService(serviceIntent);
    }

    private void sendStepDataToService() {
        Intent intent = new Intent("com.example.dailybite.UPDATE_PEDOMETER");
        intent.putExtra("steps", sessionSteps);
        intent.putExtra("distance", calculateDistance(sessionSteps));
        intent.putExtra("calories", calculateCalories(sessionSteps));
        intent.putExtra("elapsedTime", System.currentTimeMillis() - startTime);
        requireActivity().sendBroadcast(intent);
    }

    private void updateUI() {
        double distance = calculateDistance(sessionSteps);
        double kcal = calculateCalories(sessionSteps);

        stepsTextView.setText(String.valueOf(sessionSteps));
        mileTextView.setText(String.format("%.2f Mile", distance));
        kcalTextView.setText(String.format("%.1f Kcal", kcal));

        long elapsedTime = System.currentTimeMillis() - startTime;
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        timeTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

        updateProgressBar();
    }

    private void updateProgressBar() {
        int progressPercentage = (int) ((sessionSteps / (float) stepCountTarget) * 100);
        progressBar.setProgress(Math.min(progressPercentage, 100));
    }

    private void updateStepGoalUI() {
        stepCountTargetTextView.setText("Step Goal: " + stepCountTarget);
        progressBar.setMaxProgress(stepCountTarget);
    }

    private double calculateDistance(int steps) {
        return steps * stepLengthInMeters / 1000; // Distance in km
    }

    private double calculateCalories(int steps) {
        return steps * 0.04; // Calories burned
    }

    private void showStepGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Step Goal");

        // Input field
        final EditText input = new EditText(requireContext());
        input.setHint("Enter new step goal");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Dialog buttons
        builder.setPositiveButton("Set", (dialog, which) -> {
            String newGoalText = input.getText().toString();
            if (!newGoalText.isEmpty()) {
                stepCountTarget = Integer.parseInt(newGoalText);
                saveStepGoal();
                updateStepGoalUI();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveStepGoal() {
        SharedPreferences prefs = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_STEP_GOAL, stepCountTarget);
        editor.apply();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER && isWalking) {
            if (stepCount == 0) {
                stepCount = (int) event.values[0]; // Initialize step count
            }
            sessionSteps = (int) event.values[0] - stepCount; // Calculate steps for this session
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isWalking) {
            detectShake(event.values[0], event.values[1], event.values[2]);
        }
    }

    private void detectShake(float x, float y, float z) {
        if (isFirstShake) {
            lastX = x;
            lastY = y;
            lastZ = z;
            isFirstShake = false;
            return;
        }

        float deltaX = Math.abs(x - lastX);
        float deltaY = Math.abs(y - lastY);
        float deltaZ = Math.abs(z - lastZ);

        if ((deltaX > accelerationThreshold || deltaY > accelerationThreshold || deltaZ > accelerationThreshold) && !isShakeStepDetected) {
            isShakeStepDetected = true;
            sessionSteps++;
            updateUI();
        } else if (deltaX < accelerationThreshold && deltaY < accelerationThreshold && deltaZ < accelerationThreshold) {
            isShakeStepDetected = false;
        }

        lastX = x;
        lastY = y;
        lastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        if (isWalking && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }
}
