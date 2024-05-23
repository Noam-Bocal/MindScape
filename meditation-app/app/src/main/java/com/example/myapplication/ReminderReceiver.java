package com.example.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.common.api.internal.PendingResultFacade;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the meditation name and link from the intent extras
        String meditationName = intent.getStringExtra("MEDITATION_NAME");
        String meditationLink = intent.getStringExtra("MEDITATION_LINK");

        // Create an intent to open the Reminder_screen when the notification is tapped
        Intent reminderIntent = new Intent(context, Reminder_screen.class);
        reminderIntent.putExtra("FROM_NOTIFICATION", true);  // This indicates it's from the notification
        reminderIntent.putExtra("MEDITATION_NAME", meditationName);
        reminderIntent.putExtra("MEDITATION_LINK", meditationLink);
        // Create a pending intent for the reminder intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "YOUR_CHANNEL_ID")
                .setSmallIcon(R.drawable.notification_icon)  // Set the small icon for the notification
                .setContentTitle("Meditation Reminder")  // Set the title of the notification
                .setContentText(meditationName)  // Set the content text of the notification (meditation name)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Set the priority of the notification
                .setContentIntent(pendingIntent)  // Set the pending intent to open Reminder_screen
                .setAutoCancel(true); // Auto-cancel the notification when the user taps on it

        // Get the NotificationManagerCompat instance and notify the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());  // Notify the notification with ID 1
    }
}
