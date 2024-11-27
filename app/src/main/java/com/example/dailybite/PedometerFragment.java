package com.example.dailybite;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "PedometerFragment";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private TextView stepsTextView, timeTextView, mileTextView, kcalTextView, stepCountTargetTextView;
    private Button startStopButton;
    private CircularProgressBar progressBar;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor, accelerometer;
    private int stepCount = 0; // Initial total step count from the step counter sensor
    private int sessionSteps = 0; // Steps counted during the current session
    private boolean isWalking = false;

    private long startTime = 0L; // Start time of the session
    private long timePaused = 0L; // Time paused during the session
    private boolean isPaused = false; // Tracks whether the session is paused
    private Handler handler = new Handler();
    private Runnable runnable;

    private final float stepLengthInMeters = 0.762f; // Approximate step length
    private int stepCountTarget = 5000; // Default target step count

    private static final String SHARED_PREFS = "PedometerPrefs";
    private static final String KEY_STEP_GOAL = "StepGoal";

    // Shake detection variables
    private float lastX, lastY, lastZ;
    private boolean isFirstShake = true;
    private float accelerationThreshold = 12.0f; // Sensitivity for shake detection

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
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Check for permission to access activity recognition
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, initialize sensors
            initializeSensors();
        }

        // Start/Stop button listener
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

    private void initializeSensors() {
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    private void startWalking() {
        if (sensorManager == null || stepCounterSensor == null) {
            Log.e(TAG, "Step counter sensor not available.");
            stepsTextView.setText("Step sensor not available");
            return;
        }

        isWalking = true;
        startStopButton.setText("Stop");
        startTime = System.currentTimeMillis() - timePaused; // Adjust for paused time
        isPaused = false;

        // Runnable for updating UI and sending step data to service
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isWalking) {
                    updateStopwatch();
                    updateStats();
                    sendStepDataToService();
                    handler.postDelayed(this, 1000); // Update every second
                }
            }
        };
        handler.post(runnable);

        // Start the pedometer service
        Intent serviceIntent = new Intent(requireContext(), PedometerService.class);
        requireContext().startForegroundService(serviceIntent);
    }

    private void stopWalking() {
        isWalking = false;
        startStopButton.setText("Start");
        timePaused = System.currentTimeMillis() - startTime; // Capture paused time
        isPaused = true;
        handler.removeCallbacks(runnable);

        // Stop the pedometer service
        Intent serviceIntent = new Intent(requireContext(), PedometerService.class);
        requireContext().stopService(serviceIntent);
    }

    private void updateStopwatch() {
        long elapsedTime = isPaused ? timePaused : (System.currentTimeMillis() - startTime);
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);
        timeTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void updateStats() {
        double distance = calculateDistance(sessionSteps);
        double calories = calculateCalories(sessionSteps);

        stepsTextView.setText(String.valueOf(sessionSteps));
        mileTextView.setText(String.format("%.2f Mile", distance));
        kcalTextView.setText(String.format("%.1f Kcal", calories));

        updateProgressBar();
    }

    private void updateStepGoalUI() {
        stepCountTargetTextView.setText("Step Goal: " + stepCountTarget);
        progressBar.setMaxProgress(stepCountTarget);
    }

    private void updateProgressBar() {
        int progress = (int) ((sessionSteps / (float) stepCountTarget) * 100);
        progressBar.setProgress(Math.min(progress, 100));
    }

    private double calculateDistance(int steps) {
        return steps * stepLengthInMeters / 1000; // Distance in km
    }

    private double calculateCalories(int steps) {
        return steps * 0.04; // Calories burned per step
    }

    private void sendStepDataToService() {
        Intent intent = new Intent("com.example.dailybite.UPDATE_PEDOMETER");
        intent.putExtra("steps", sessionSteps);
        intent.putExtra("distance", calculateDistance(sessionSteps));
        intent.putExtra("calories", calculateCalories(sessionSteps));
        intent.putExtra("elapsedTime", System.currentTimeMillis() - startTime);
        requireActivity().sendBroadcast(intent); // Send broadcast to service
    }

    private void showStepGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Step Goal");

        // Input field for the new step goal
        final EditText input = new EditText(requireContext());
        input.setHint("Enter new step goal");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Set", (dialog, which) -> {
            String newGoal = input.getText().toString();
            if (!newGoal.isEmpty()) {
                stepCountTarget = Integer.parseInt(newGoal);
                SharedPreferences prefs = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(KEY_STEP_GOAL, stepCountTarget);
                editor.apply();
                updateStepGoalUI();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
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

        if (deltaX > accelerationThreshold || deltaY > accelerationThreshold || deltaZ > accelerationThreshold) {
            sessionSteps++; // Increment steps on shake detection
            updateStats();
        }

        lastX = x;
        lastY = y;
        lastZ = z;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (stepCount == 0) {
                stepCount = (int) event.values[0];
            }
            sessionSteps = (int) event.values[0] - stepCount;
            updateStats();
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectShake(event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No specific action needed
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize sensors
                initializeSensors();
            } else {
                Toast.makeText(requireContext(), "Permission Denied! Cannot access step counter.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
