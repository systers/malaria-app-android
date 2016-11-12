package com.peacecorps.malaria.services;

/**
 * Created by Ankita on 8/8/2015.
 */
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.activities.TripAlarmActivity;

public class TripAlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public TripAlarmService() {
        super("TripAlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        SharedPreferences preferences = getSharedPreferences("WidgetReminder", Context.MODE_PRIVATE);
        sendNotification("Get Ready to Pack your Bags!" + "\n" + preferences.getString("view_upcoming_reminder",""), intent);

    }

    private void sendNotification(String msg,Intent intent) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activIntent = new Intent(this,TripAlarmActivity.class);
        activIntent.putExtra("AlarmID",intent.getIntExtra("AlarmID",0));
        //activIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.d("TAS","aLARMid: "+intent.getIntExtra("AlarmID",0));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                activIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Upcoming Trip Reminder").setSmallIcon(R.drawable.appicon_themed)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .addAction(R.drawable.checked, "Trip Checklist", contentIntent)
                .setOngoing(true);


        //alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("TripAlarmService", "Notification sent.");
    }
}