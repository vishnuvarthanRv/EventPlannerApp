package com.example.eventplannerapp_jf_ict_25_23;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderScheduler {

    // Schedule Event Reminder
    public static void scheduleReminder(
            Context context,
            event e) {

        long reminderTime =
                e.getDateTime()
                        - (e.getReminderMinutes() * 60L * 1000L);

        // Don't schedule past reminders
        if (reminderTime <= System.currentTimeMillis()) {
            return;
        }

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        Intent intent =
                new Intent(context, ReminderReceiver.class);

        intent.putExtra(
                "eventName",
                e.getName()
        );

        intent.putExtra(
                "eventDescription",
                e.getDescription()
        );

        intent.putExtra(
                "eventId",
                e.getId().hashCode()
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        e.getId().hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.M) {

                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                );

            } else {

                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                );

            }

        }

    }

    // Cancel Event Reminder
    public static void cancelReminder(
            Context context,
            event e) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        Intent intent =
                new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        e.getId().hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        if (alarmManager != null) {

            alarmManager.cancel(pendingIntent);

        }

        pendingIntent.cancel();

    }

}