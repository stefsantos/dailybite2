package com.example.dailybite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    private TextView stepsTextView, timeTextView, mileTextView, kcalTextView, durationTextView, stepCountTargetTextView;
    private Button startStopButton;
    private CircularProgressBar progressBar;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor, accelerometer;
    private int stepCount = 0;
    private boolean isWalking = false;

    private long startTime = 0L;
    private long timePaused = 0L;
    private boolean isPaused = false;
    private Handler handler = new Handler();
    private Runnable runnable;

    private final float stepLengthInMeters = 0.762f; // Approximate step length
    private int stepCountTarget = 5000; // Default target step count

    private static final String SHARED_PREFS = "PedometerPrefs";
    private static final String KEY_STEP_GOAL = "StepGoal";

    // Accelerometer variables for motion detection
    private float accelerationThreshold = 12.0f; // Sensitivity for motion detection
    private float lastX, lastY, lastZ;
    private boolean isFirstShake = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        // Initialize UI components
        stepsTextView = view.findViewById(R.id.steps_text_view);
        timeTextView = view.findViewById(R.id.time_text_view);
        mileTextView = view.findViewById(R.id.mile_text_view);
        kcalTextView = view.findViewById(R.id.kcal_text_view);
        durationTextView = view.findViewById(R.id.duration_text_view);
        stepCountTargetTextView = view.findViewById(R.id.step_target);
        startStopButton = view.findViewById(R.id.start_stop_button);
        progressBar = view.findViewById(R.id.progressBar);

        // Load saved step goal
        SharedPreferences prefs = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        stepCountTarget = prefs.getInt(KEY_STEP_GOAL, stepCountTarget);
        updateStepGoalUI();

        // Initialize SensorManager and sensors
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register sensor listeners
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            stepsTextView.setText("No accelerometer available for motion detection");
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

    private void startWalking() {
        isWalking = true;
        startStopButton.setText("Stop");
        startTime = System.currentTimeMillis() - timePaused; // Continue from paused time if paused
        isPaused = false;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isWalking) {
                    updateStopwatch();
                    updateStats();
                    handler.postDelayed(this, 1000); // Update every second
                }
            }
        };
        handler.post(runnable);
    }

    private void stopWalking() {
        isWalking = false;
        startStopButton.setText("Start");
        timePaused = System.currentTimeMillis() - startTime; // Capture paused time
        isPaused = true;
        handler.removeCallbacks(runnable);
    }

    private void updateStepCount() {
        stepsTextView.setText("Steps: " + stepCount);
        updateProgressBar();
    }

    private void updateProgressBar() {
        // Calculate progress as a percentage of the step count target
        int progressPercentage = (int) ((stepCount / (float) stepCountTarget) * 100);

        // Cap the progress at 100% to avoid overflow if stepCount exceeds stepCountTarget
        if (progressPercentage > 100) {
            progressPercentage = 100;
        }

        // Update the circular progress bar with the calculated percentage
        progressBar.setProgress(progressPercentage);
    }

    private void updateStopwatch() {
        long elapsedTime = isPaused ? timePaused : (System.currentTimeMillis() - startTime);

        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timeTextView.setText(timeFormatted);
    }

    private void updateStats() {
        double miles = stepCount * stepLengthInMeters / 1609.34;
        double kcal = stepCount * 0.04;

        mileTextView.setText(String.format("%.2f Mile", miles));
        kcalTextView.setText(String.format("%.1f Kcal", kcal));

        long elapsedTime = System.currentTimeMillis() - startTime;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        durationTextView.setText(String.format("%dh %dm", hours, minutes));
    }

    private void updateStepGoalUI() {
        stepCountTargetTextView.setText("Step Goal: " + stepCountTarget);
        progressBar.setMaxProgress(stepCountTarget);
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
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];

            // Calculate steps for this session only
            if (stepCount == 0) {
                stepCount = totalSteps; // Initialize step count on first reading
            } else {
                stepCount += (totalSteps - stepCount); // Accumulate step count based on counter sensor
            }
            updateStepCount();
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Shake detection based on accelerometer
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            detectShake(x, y, z);
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

        if (deltaX > accelerationThreshold || deltaY > accelerationThreshold || deltaZ > accelerationThreshold) {
            stepCount++; // Increment step count for each shake detected
            updateStepCount();
        }

        lastX = x;
        lastY = y;
        lastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if necessary
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        handler.removeCallbacks(runnable);
    }
}
