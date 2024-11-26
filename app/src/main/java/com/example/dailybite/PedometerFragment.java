package com.example.dailybite;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private TextView stepsTextView, timeTextView, mileTextView, kcalTextView, durationTextView, stepCountTargetTextView;
    private Button startStopButton;
    private CircularProgressBar progressBar;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int stepCount = 0;
    private boolean isWalking = false;

    private long startTime = 0L;
    private long timePaused = 0L;
    private boolean isPaused = false;
    private Handler handler = new Handler();
    private Runnable runnable;

    private final float stepLengthInMeters = 0.762f; // Approximate step length
    private final int stepCountTarget = 5000; // Target step count

    private static final String SHARED_PREFS_NAME = "PedometerPrefs";
    private static final String KEY_STEP_COUNT = "stepCount";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_IS_WALKING = "isWalking";

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

        stepCountTargetTextView.setText("Step Goal: " + stepCountTarget);
        progressBar.setMaxProgress(stepCountTarget);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        restoreState();

        startStopButton.setOnClickListener(v -> {
            if (isWalking) {
                stopWalking();
                stopPedometerService();
            } else {
                if (hasPermissions()) {
                    startWalking();
                    startPedometerService();
                } else {
                    Toast.makeText(requireContext(), "Permissions are required to start the pedometer.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void startWalking() {
        isWalking = true;
        startStopButton.setText("Stop");
        if (startTime == 0L) {
            startTime = System.currentTimeMillis() - timePaused;
        }
        isPaused = false;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isWalking) {
                    updateStopwatch();
                    updateStats();
                    saveState(); // Save the current state
                    sendStepDataToService(); // Send updated data to service
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }

    private void stopWalking() {
        isWalking = false;
        startStopButton.setText("Start");
        timePaused = System.currentTimeMillis() - startTime;
        isPaused = true;
        handler.removeCallbacks(runnable);
        saveState(); // Save the state when stopped
    }

    private void sendStepDataToService() {
        Intent intent = new Intent("com.example.dailybite.UPDATE_PEDOMETER");
        intent.putExtra("steps", stepCount);
        intent.putExtra("distance", calculateDistance(stepCount));
        intent.putExtra("calories", calculateCalories(stepCount));
        intent.putExtra("time", System.currentTimeMillis() - startTime); // Elapsed time
        requireActivity().sendBroadcast(intent);
    }

    private void startPedometerService() {
        Intent serviceIntent = new Intent(requireActivity(), PedometerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent);
        } else {
            requireActivity().startService(serviceIntent);
        }
    }

    private void stopPedometerService() {
        Intent serviceIntent = new Intent(requireActivity(), PedometerService.class);
        requireActivity().stopService(serviceIntent);
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
        double miles = calculateDistance(stepCount);
        double kcal = calculateCalories(stepCount);

        mileTextView.setText(String.format("%.2f Mile", miles));
        kcalTextView.setText(String.format("%.1f Kcal", kcal));
    }

    private double calculateDistance(int steps) {
        return steps * stepLengthInMeters / 1000;
    }

    private double calculateCalories(int steps) {
        return steps * 0.04;
    }

    private void saveState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_STEP_COUNT, stepCount);
        editor.putLong(KEY_START_TIME, startTime);
        editor.putBoolean(KEY_IS_WALKING, isWalking);
        editor.apply();
    }

    private void restoreState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        stepCount = prefs.getInt(KEY_STEP_COUNT, 0);
        startTime = prefs.getLong(KEY_START_TIME, 0L);
        isWalking = prefs.getBoolean(KEY_IS_WALKING, false);

        stepsTextView.setText(String.valueOf(stepCount));
        updateStopwatch();
        updateStats();

        if (isWalking) {
            startWalking();
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];

            if (stepCount == 0) {
                stepCount = totalSteps;
            } else {
                stepCount += (totalSteps - stepCount);
            }
            updateStats();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
