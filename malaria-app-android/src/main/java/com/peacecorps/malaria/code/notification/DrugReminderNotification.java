package com.peacecorps.malaria.code.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.home_screen.MainActivity;
import com.peacecorps.malaria.code.reciever.DrugReminderReceiver;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;

import java.util.Calendar;

public class DrugReminderNotification {
    Context context;

    public DrugReminderNotification(Context context) {
        this.context = context;
        buildNotification();
    }

    private void buildNotification() {
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

       /* Add Actions in Notification
        /*Action Not Taken*/
        Intent intentNotTaken = new Intent(context, DrugReminderReceiver.class);
        intentNotTaken.setAction(context.getString(R.string.notification_action_code_not_taken));
        PendingIntent pendingIntentNotTaken = PendingIntent.getBroadcast(context, 0, intentNotTaken, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action actionNotTaken;
        actionNotTaken = new NotificationCompat.Action(R.drawable.ic_cancel_black_18dp, context.getString(R.string.drug_reminder_notification_action_not_taken)
                , pendingIntentNotTaken);

         /*Action  Taken*/
        Intent intentTaken = new Intent(context, DrugReminderReceiver.class);
        intentTaken.setAction(context.getString(R.string.notification_action_code_taken));
        PendingIntent pendingIntentTaken = PendingIntent.getBroadcast(context, 0, intentTaken, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action actionTaken;
        actionTaken = new NotificationCompat.Action(R.drawable.ic_done_black_18dp, context.getString(R.string.drug_reminder_notification_action_taken)
                , pendingIntentTaken);

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.soundsmedication);
        /**Building Notifications**/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context).setContentTitle(context.getString(R.string.drug_reminder_notification_title))
                .setSmallIcon(R.drawable.appicon_themed)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.drug_reminder_notification_message)))
                .setContentText(context.getString(R.string.drug_reminder_notification_message))
                .setAutoCancel(false)
                .setOngoing(true)
                .addAction(actionNotTaken)
                .addAction(actionTaken);

        if (addSnoozeButton()) {
              /*Action Snooze*/
            Intent intentSnooze = new Intent(context, DrugReminderReceiver.class);
            intentSnooze.setAction(context.getString(R.string.notification_action_code_snooze));
            PendingIntent pendingIntentSnooze = PendingIntent.getBroadcast(context, 0, intentSnooze, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action actionSnooze;
            actionSnooze = new NotificationCompat.Action(R.drawable.ic_snooze_black_18dp, context.getString(R.string.drug_reminder_notification_action_snooze)
                    , pendingIntentSnooze);

            builder.addAction(actionSnooze);
        }
        builder.setSound(sound);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(12345, builder.build());
        Log.d("DrugReminderNotif", "Notification sent.");
    }

    private boolean addSnoozeButton() {
        Calendar c = Calendar.getInstance();
        int d = c.get(Calendar.DATE);
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(context);
        if (sqLite.getStatus(d, m, y).equalsIgnoreCase("yes") == true) {
            return false;
        } else if (sqLite.getStatus(d, m, y).equalsIgnoreCase("no") == true) {
            return true;
        } else
            return true;
    }
}
