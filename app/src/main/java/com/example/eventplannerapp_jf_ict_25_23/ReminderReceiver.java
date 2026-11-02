package com.example.eventplannerapp_jf_ict_25_23;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID =
            "event_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        String eventName =
                intent.getStringExtra("eventName");

        String eventDescription =
                intent.getStringExtra("eventDescription");

        int eventId =
                intent.getIntExtra("eventId", 0);

        if (eventName == null || eventName.isEmpty()) {
            eventName = "Upcoming Event";
        }

        if (eventDescription == null ||
                eventDescription.isEmpty()) {

            eventDescription = "You have an upcoming event!";
        }

        // Notification Manager

        NotificationManager notificationManager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        // Create Notification Channel (Android 8+)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Event Reminders",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "Notifications for upcoming events"
            );

            notificationManager.createNotificationChannel(
                    channel
            );

        }

        // Open app when notification is clicked

        Intent openAppIntent =
                new Intent(context, MainActivity.class);

        openAppIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        eventId,
                        openAppIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        // Build Notification

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder = new Notification.Builder(
                    context,
                    CHANNEL_ID
            );

        } else {

            builder = new Notification.Builder(context);

        }

        builder.setSmallIcon(
                android.R.drawable.ic_dialog_info
        );

        builder.setContentTitle(
                "Event Reminder: " + eventName
        );

        builder.setContentText(
                eventDescription
        );

        builder.setStyle(
                new Notification.BigTextStyle()
                        .bigText(eventDescription)
        );

        builder.setPriority(
                Notification.PRIORITY_HIGH
        );

        builder.setAutoCancel(true);

        builder.setContentIntent(
                pendingIntent
        );

        // Display Notification

        Notification notification =
                builder.build();

        notificationManager.notify(
                eventId,
                notification
        );

    }

}