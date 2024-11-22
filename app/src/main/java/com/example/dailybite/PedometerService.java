package com.example.dailybite;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PedometerService extends Service {
    private static final String CHANNEL_ID = "PedometerChannel";
    private int steps = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // Check location permission
        if (!hasLocationPermission()) {
            stopSelf();
            return;
        }

        createNotificationChannel();
        // Start the service with an initial notification
        startForeground(1, createNotification(steps, 0));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Simulate step counting in a background thread
        new Thread(() -> {
            try {
                while (true) {
                    steps++;
                    Notification notification = createNotification(steps, steps * 0.0008); // Example conversion
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    if (manager != null) {
                        manager.notify(1, notification); // Update notification
                    }
                    SystemClock.sleep(1000); // Update every second
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log exceptions for debugging
                stopSelf(); // Stop the service if an error occurs
            }
        }).start();

        return START_STICKY;
    }

    private boolean hasLocationPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || // Pre-Marshmallow doesn't require runtime permissions
                (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pedometer Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notification for Pedometer Service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification(int steps, double distance) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pedometer Running")
                .setContentText("Steps: " + steps + ", Distance: " + String.format("%.2f", distance) + " km")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with a valid icon
                .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority for background tasks
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // No binding is required
    }
}
