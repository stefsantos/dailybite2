package com.example.dailybite;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PedometerFragment extends Fragment {

    private TextView stepsTextView, timeTextView;
    private Button startStopButton;
    private boolean isWalking = false;
    private int stepCount = 0;
    private long startTime = 0L;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        // Initialize UI components
        stepsTextView = view.findViewById(R.id.steps_text_view);
        timeTextView = view.findViewById(R.id.time_text_view);
        startStopButton = view.findViewById(R.id.start_stop_button);

        // Set initial values
        stepsTextView.setText("Steps: 0");
        timeTextView.setText("00:00:00");

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
                    updateStopwatch();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable); // Stop updating the UI if fragment is destroyed
    }
}
