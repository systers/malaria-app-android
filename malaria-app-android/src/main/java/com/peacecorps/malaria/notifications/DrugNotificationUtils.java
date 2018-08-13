package com.peacecorps.malaria.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.DbHelper;
import com.peacecorps.malaria.notifications.receiver.DrugNotificationReceiver;
import com.peacecorps.malaria.ui.main.MainActivity;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import java.util.Calendar;

public class DrugNotificationUtils {
    private static final int START_ACTIVITY_PENDING_INTENT_ID = 245;
    private static final int ACTION_ACCEPT_PENDING_INTENT_ID = 502;
    private static final int ACTION_REJECT_PENDING_INTENT_ID = 504;
    public static final int ACTION_SNOOZE_PENDING_INTENT_ID = 506;
    private static final String NOTIFICATION_CHANNEL_ID = "drug_reminder_channel_id";
    private static final int NOTIFICATION_MANAGER_ID = 568;

    public static final String ACTION_ACCEPTED_MEDICINE = "ACTION_ACCEPTED_MEDICINE";
    public static final String ACTION_REJECT_MEDICINE = "ACTION_REJECT_MEDICINE";
    public static final String ACTION_SNOOZE_MEDICINE = "ACTION_SNOOZE_MEDICINE";

    // function to create and start notification for drug reminder
    private static void startNotificationForDrugs(boolean snooze, Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.drug_reminder_notification_title),
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.lightest_brown))
                .setContentTitle(context.getString(R.string.drug_reminder_notification_title))
                .setContentText(context.getString(R.string.drug_reminder_notification_message))
                .setContentIntent(contentIntent(context))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.app_icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.drug_reminder_notification_message)))
                .addAction(acceptMedicineAction(context))
                .addAction(rejectMedicineAction(context))
                .setWhen(0)
                .setAutoCancel(true);

        if(snooze) {
            notificationBuilder.addAction(snoozeAction(context));
        }

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.soundsmedication);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        }
        notificationBuilder.setSound(sound);
        if (manager != null) {
            manager.notify(NOTIFICATION_MANAGER_ID, notificationBuilder.build());
        }
    }

    // returns a pending intent for main activity
    private static PendingIntent contentIntent(Context context) {
        Intent activityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, START_ACTIVITY_PENDING_INTENT_ID, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * @param context : android's context - used for creating intent
     * @return : returns accept notification action which will be used with setAction method
     */
    private static NotificationCompat.Action acceptMedicineAction (Context context) {
        // create an intent to launch Drug reminder receiver
        Intent acceptIntent = new Intent(context, DrugNotificationReceiver.class);
        // set the action to designate that medicine is accepted
        acceptIntent.setAction(ACTION_ACCEPTED_MEDICINE);
        // create pending intent to launch service class
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(context,
                ACTION_ACCEPT_PENDING_INTENT_ID, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // create and return notification action
        return new NotificationCompat.Action(R.drawable.ic_done_black_18dp,
                context.getString(R.string.drug_reminder_notification_action_taken)
                , acceptPendingIntent);
    }

    /**
     * @param context : android's context - used for creating intent
     * @return : returns reject notification action which will be used with setAction method
     */
    private static NotificationCompat.Action rejectMedicineAction (Context context) {
        // create an intent to launch Drug reminder receiver
        Intent rejectIntent = new Intent(context, DrugNotificationReceiver.class);
        // set the action to designate that medicine is accepted
        rejectIntent.setAction(ACTION_REJECT_MEDICINE);
        // create pending intent to launch service class
        PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(context,
                ACTION_REJECT_PENDING_INTENT_ID, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // create and return notification action
        return new NotificationCompat.Action(R.drawable.ic_cancel_black_18dp,
                context.getString(R.string.drug_reminder_notification_action_not_taken)
                , rejectPendingIntent);
    }

    private static NotificationCompat.Action snoozeAction (Context context) {
        Intent intentSnooze = new Intent(context, DrugNotificationReceiver.class);
        intentSnooze.setAction(ACTION_SNOOZE_MEDICINE);
        PendingIntent pendingIntentSnooze = PendingIntent.getBroadcast(context,
                ACTION_SNOOZE_PENDING_INTENT_ID, intentSnooze, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action(R.drawable.ic_snooze_black_18dp,
                context.getString(R.string.drug_reminder_notification_action_snooze)
                , pendingIntentSnooze);
    }

    public static void clearAllNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }
    }

    public static void startNotificationCheckSnooze(final Context context) {
        final Calendar c = Calendar.getInstance();
        int d = c.get(Calendar.DATE);
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        AppDataManager manager = InjectionClass.provideDataManager(context);
        manager.getDailyStatus(d, m, y, new DbHelper.LoadStringCallback() {
            @Override
            public void onDataLoaded(String status) {
                if("yes".equalsIgnoreCase(status)) {
                    startNotificationForDrugs(false, context);
                } else {
                    startNotificationForDrugs(true, context);
                }
                ToastLogSnackBarUtil.showDebugLog("DrugNotificationUtil: starting notification");
            }
        });
    }


}
