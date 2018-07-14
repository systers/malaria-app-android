package com.peacecorps.malaria.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.home_screen.MainActivity;

public class NotificationUtils {
    private static final int PENDING_INTENT_ID = 245;
    private static final String NOTICATION_CHANNEL_ID = "drug_reminder_channel_id";
    private static final int NOTICATION_MANAGER_ID = 568;

    // function to create and start notification for drug reminder
    public void startNotificationForDrugs(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTICATION_CHANNEL_ID,
                    context.getString(R.string.drug_reminder_notification_title),
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

//        //Todo can still add large icon, small icon & style
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.lightest_brown))
                .setContentTitle(context.getString(R.string.drug_reminder_notification_title))
                .setContentText(context.getString(R.string.drug_reminder_notification_message))
                .setContentIntent(contentIntent(context))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        if (manager != null) {
            manager.notify(NOTICATION_MANAGER_ID, notificationBuilder.build());
        }


    }

    // returns a pending intent for main activity
    private PendingIntent contentIntent(Context context) {
        Intent activityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, PENDING_INTENT_ID, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
