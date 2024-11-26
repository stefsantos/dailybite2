package com.example.dailybite;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";
    private static final String CHANNEL_ID = "MyReceiverChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check the action and handle accordingly
        if ("com.example.dailybite.MY_ACTION".equals(intent.getAction())) {
            Log.d(TAG, "Received broadcast: " + intent.getAction());

            // Example of extracting data from the intent (if any)
            String data = intent.getStringExtra("extra_data");
            if (data != null) {
                Log.d(TAG, "Received data: " + data);
                // You can perform actions based on the received data
            }

            // Perform a task like starting a service or showing a notification
            startSomeService(context);
            showNotification(context, "Broadcast Received", "Action: " + intent.getAction());
        }
    }

    // Example: Start a background service (e.g., to update step count, check data, etc.)
    private void startSomeService(Context context) {
        Intent serviceIntent = new Intent(context, PedometerService.class);
        context.startService(serviceIntent); // Start your service
    }

    // Example: Send a notification to the user
    private void showNotification(Context context, String title, String message) {
        // Create notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Receiver Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        // Show the notification
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }
}
