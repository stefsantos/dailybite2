package com.example.dailybite;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private Sensor stepCounterSensor, accelerometer;
    private int stepCount = 0;
    private boolean isWalking = false;

    private long startTime = 0L;
    private long timePaused = 0L;
    private boolean isPaused = false;
    private Handler handler = new Handler();
    private Runnable runnable;

    private final float stepLengthInMeters = 0.762f; // Approximate step length
    private final int stepCountTarget = 5000; // Target step count

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

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

        // Display target step count
        stepCountTargetTextView.setText("Step Goal: " + stepCountTarget);

        // Set maximum progress for the circular progress bar
        progressBar.setMaxProgress(stepCountTarget);

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

        // Check permissions
        checkAndRequestPermissions();

        // Start/Stop button listener
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

    private void checkAndRequestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean hasPermissions() {
        boolean hasNotificationPermission = android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;

        boolean hasLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        return hasNotificationPermission && hasLocationPermission;
    }

    private void startPedometerService() {
        Intent serviceIntent = new Intent(requireActivity(), PedometerService.class);
        requireActivity().startForegroundService(serviceIntent);
    }

    private void stopPedometerService() {
        Intent serviceIntent = new Intent(requireActivity(), PedometerService.class);
        requireActivity().stopService(serviceIntent);
    }

    private void startWalking() {
        isWalking = true;
        startStopButton.setText("Stop");
        startTime = System.currentTimeMillis() - timePaused;
        isPaused = false;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isWalking) {
                    updateStopwatch();
                    updateStats();
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
    }

    private void updateStepCount() {
        stepsTextView.setText("Steps: " + stepCount);
        updateProgressBar();
    }

    private void updateProgressBar() {
        progressBar.setProgress(stepCount);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];

            if (stepCount == 0) {
                stepCount = totalSteps;
            } else {
                stepCount += (totalSteps - stepCount);
            }
            updateStepCount();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE || requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Cannot start the pedometer.", Toast.LENGTH_SHORT).show();
            }
        }
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
