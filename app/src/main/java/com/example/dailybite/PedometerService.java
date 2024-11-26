package com.example.dailybite;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PedometerService extends Service {

    private static final String CHANNEL_ID = "PedometerChannel";

    private BroadcastReceiver pedometerDataReceiver;
    private int stepCount = 0;
    private double distance = 0.0;
    private double calories = 0.0;
    private long elapsedTime = 0L;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create and register the BroadcastReceiver
        pedometerDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.dailybite.UPDATE_PEDOMETER".equals(intent.getAction())) {
                    stepCount = intent.getIntExtra("steps", 0);
                    distance = intent.getDoubleExtra("distance", 0.0);
                    calories = intent.getDoubleExtra("calories", 0.0);
                    elapsedTime = intent.getLongExtra("elapsedTime", 0L);

                    updateNotification();
                }
            }
        };

        IntentFilter filter = new IntentFilter("com.example.dailybite.UPDATE_PEDOMETER");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(pedometerDataReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(pedometerDataReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }

        createNotificationChannel();

        // Start the service as a foreground service
        startForeground(1, createNotification());
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

    private void updateNotification() {
        Notification notification = createNotification();
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(1, notification);
        }
    }

    private Notification createNotification() {
        String formattedTime = formatElapsedTime(elapsedTime);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pedometer")
                .setContentText(
                        "Steps: " + stepCount +
                                " | Distance: " + String.format("%.2f", distance) + " km" +
                                "\nCalories: " + String.format("%.1f", calories) + " kcal | Time: " + formattedTime
                )
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private String formatElapsedTime(long elapsedTime) {
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pedometerDataReceiver != null) {
            unregisterReceiver(pedometerDataReceiver);
        }
    }
}
