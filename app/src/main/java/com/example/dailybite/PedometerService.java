package com.example.dailybite;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PedometerService extends Service {

    private static final String CHANNEL_ID = "PedometerChannel";
    private static final String ACTION_UPDATE_PEDOMETER = "com.example.dailybite.UPDATE_PEDOMETER";

    private int steps = 0;
    private double distance = 0.0;
    private double calories = 0.0;
    private long startTime = 0L; // Service start time in milliseconds

    private Handler handler;
    private Runnable stopwatchRunnable;
    private BroadcastReceiver pedometerDataReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();
        startTime = System.currentTimeMillis(); // Capture the service start time

        // Register the broadcast receiver dynamically
        pedometerDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ACTION_UPDATE_PEDOMETER.equals(intent.getAction())) {
                    // Update data from fragment broadcast
                    steps = intent.getIntExtra("steps", 0);
                    distance = intent.getDoubleExtra("distance", 0.0);
                    calories = intent.getDoubleExtra("calories", 0.0);
                }
            }
        };

        // Register the receiver
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_PEDOMETER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(pedometerDataReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(pedometerDataReceiver, filter);
        }

        // Create the notification channel and start the service
        createNotificationChannel();
        startForeground(1, createNotification(steps, distance, 0L));

        // Start the stopwatch updates
        startStopwatch();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Keeps the service running until explicitly stopped
    }

    private void startStopwatch() {
        stopwatchRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;

                // Update the notification with elapsed time
                updateNotification(elapsedTime);

                // Schedule the next update
                handler.postDelayed(this, 1000); // Update every second
            }
        };

        handler.post(stopwatchRunnable); // Start the stopwatch
    }

    private Notification createNotification(int steps, double distance, long elapsedTime) {
        String formattedTime = formatElapsedTime(elapsedTime);

        // Create an intent to open the app (MainActivity in this example)
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Create a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android 12+ compatibility
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pedometer Running")
                .setContentText("Steps: " + steps + ", Distance: " + String.format("%.2f", distance) + " km\nTime: " + formattedTime)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
                .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority for background tasks
                .setContentIntent(pendingIntent) // Attach the PendingIntent
                .setAutoCancel(false) // Notification persists
                .build();
    }

    private void updateNotification(long elapsedTime) {
        Notification notification = createNotification(steps, distance, elapsedTime);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(1, notification); // Update the notification
        }
    }

    private String formatElapsedTime(long elapsedTime) {
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pedometer Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the broadcast receiver
        unregisterReceiver(pedometerDataReceiver);

        // Stop the stopwatch updates
        if (handler != null && stopwatchRunnable != null) {
            handler.removeCallbacks(stopwatchRunnable);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // No binding required
    }
}
