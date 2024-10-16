package com.example.dailybite;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dailybite.CircularProgressBar;


public class PedometerFragment extends Fragment {

    private TextView stepsTextView, timeTextView, mileTextView, kcalTextView, durationTextView;
    private Button startStopButton;
    private CircularProgressBar progressBar; // Custom progress bar

    private boolean isWalking = false;
    private int stepCount = 0;
    private long startTime = 0L;
    private Handler handler = new Handler();
    private Runnable runnable;

    private final int STEP_GOAL = 50;

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
        startStopButton = view.findViewById(R.id.start_stop_button);
        progressBar = view.findViewById(R.id.progressBar); // Custom CircularProgressBar

        // Set max progress for the custom circular progress bar
        progressBar.setMaxProgress(STEP_GOAL);

        // Set initial values
        stepsTextView.setText("Steps: 0");
        timeTextView.setText("00:00:00");
        mileTextView.setText("0.00 Mile");
        kcalTextView.setText("0.0 Kcal");
        durationTextView.setText("0h 0m");

        // Start/Stop button listener
        startStopButton.setOnClickListener(v -> {
            if (isWalking) {
                stopWalking();
            } else {
                startWalking();
            }
        });

        return view;
    }

    private void startWalking() {
        isWalking = true;
        startStopButton.setText("Stop");

        // Reset step count and start time
        stepCount = 0;
        startTime = System.currentTimeMillis();

        // Update the step count and stopwatch every second
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isWalking) {
                    stepCount++;
                    updateStepCount();
                    updateProgressBar(); // Update the custom CircularProgressBar
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
        handler.removeCallbacks(runnable);
    }

    private void updateStepCount() {
        stepsTextView.setText("Steps: " + stepCount);
    }

    private void updateProgressBar() {
        progressBar.setProgress(stepCount); // Update custom progress bar based on step count
    }

    private void updateStopwatch() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        // Convert milliseconds to hours, minutes, seconds
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timeTextView.setText(timeFormatted);
    }

    private void updateStats() {
        double miles = stepCount * 0.0005; // Example: 1 step = 0.0005 miles
        double kcal = stepCount * 0.04;    // Example: 1 step = 0.04 kcal

        mileTextView.setText(String.format("%.2f Mile", miles));
        kcalTextView.setText(String.format("%.1f Kcal", kcal));

        long elapsedTime = System.currentTimeMillis() - startTime;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        durationTextView.setText(String.format("%dh %dm", hours, minutes));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}
