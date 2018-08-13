package com.peacecorps.malaria.notifications.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.DbHelper;
import com.peacecorps.malaria.data.db.entities.AlarmTime;
import com.peacecorps.malaria.notifications.DrugNotificationUtils;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import java.util.Calendar;
import java.util.Date;

public class AlarmHandlerClass extends BroadcastReceiver {
    private static final int INTERVAL_WEEK = 604800000;
    private static final String TAG_START_ALARM = "com.peacecorps.malaria.START_ALARM";
    private static final int START_ALARM_PENDING_INTENT_ID = 200;

    /*
     * On Receiving the call for Alarm
     * sets one on the date and time specified
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, "");

            // set wake lock time
            wakeLock.acquire(10 * 60 * 1000L);
            ToastLogSnackBarUtil.showDebugLog("alarm-handler: on receive method, starting notification");

            // show the notification with taken, snooze and not taken button
            DrugNotificationUtils.startNotificationCheckSnooze(context);
            wakeLock.release();
        }
    }

    public void setAlarm(final Context context) {
        final AppDataManager appDataManager = InjectionClass.provideDataManager(context);
        appDataManager.getAlarmData(new DbHelper.LoadAlarmDataCallback() {
            @Override
            public void onDataLoaded(AlarmTime time) {
                if (time != null) {
                    int hour = time.getHour();
                    int min = time.getMinute();
                    ToastLogSnackBarUtil.showDebugLog("setAlarm " + hour + " "+ min);
                    /*
                     * set alarm date
                     */
                    Calendar scheduleTime = alarmTime(hour, min);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent alarmIntent = new Intent();
                    alarmIntent.setAction(TAG_START_ALARM);
                    PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context,
                            START_ALARM_PENDING_INTENT_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    boolean isWeekly = appDataManager.isDosesWeekly();

                    if (alarmManager != null) {
                        if (isWeekly) {
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                    scheduleTime.getTimeInMillis(), INTERVAL_WEEK,
                                    alarmPendingIntent);
                        } else {
                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                    scheduleTime.getTimeInMillis(),
                                    AlarmManager.INTERVAL_DAY, alarmPendingIntent);
                        }
                    } else {
                        ToastLogSnackBarUtil.showErrorLog("AlarmHandlerClass: alarmManager is null");
                    }
                }
            }
        });
    }

    @NonNull
    private Calendar alarmTime(int hour, int min) {
        Date date = new Date();
        Calendar scheduleTime;
        scheduleTime = Calendar.getInstance();
        scheduleTime.setTime(date);
        Calendar dateNow = Calendar.getInstance();
        dateNow.setTime(date);

        scheduleTime.set(Calendar.HOUR_OF_DAY, hour);
        scheduleTime.set(Calendar.MINUTE, min);

        if (scheduleTime.before(dateNow)) {
            scheduleTime.add(Calendar.DATE, 1);
        }
        return scheduleTime;
    }
}
