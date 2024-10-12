package com.example.e2243003_todolist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "Alarm triggered!");

        // Reset the AlarmSet value in SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("AlarmSet", false);
        editor.apply();

        if (context.checkSelfPermission("android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
            createNotificationChannel(context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyTask")
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Task Alert")
                    .setContentText("You have a task due soon! Time: " + System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());
        } else {
            // Handle the case where the permission is not granted
            Toast.makeText(context, "Permission to post notifications is not granted. Please enable it in the app settings.", Toast.LENGTH_LONG).show();
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TaskReminderChannel";
            String description = "Channel for Task Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyTask", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
