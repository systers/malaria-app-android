package com.peacecorps.malaria;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmHandlerClass extends BroadcastReceiver {

    /**Class is for Handling the Alarm**/

    public static Calendar mAlarmScheduleTime;
    final int INTERVAL_WEEK = 604800000;
    public static AlarmManager mAlarmManager;

    static SharedPreferenceStore mSharedPreferenceStore;

    @Override
    public void onReceive(Context context, Intent intent) {

        /**On Receiving the call for Alarm, it sets one on the date and time specified. **/
        PowerManager powerManager = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        /** Opening the Alert Dialog Window On Clicking Notification*/

        intent = new Intent(context, AlertCallerFragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        wakeLock.release();
    }

    public void getSharedPreferences(Context context) {
        /**Initializing the Shared Preferences for Storing Details**/
        mSharedPreferenceStore.mPrefsStore = context.getSharedPreferences(
                "com.peacecorps.malaria.storeTimePicked", Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }

    public void setAlarm(Context context) {

        getSharedPreferences(context);
        /**Getting the Time**/
        int hour = mSharedPreferenceStore.mPrefsStore.getInt(
                "com.peacecorps.malaria.AlarmHour", -1);
        int minute = mSharedPreferenceStore.mPrefsStore.getInt(
                "com.peacecorps.malaria.AlarmMinute", -1);
        if ((hour != -1) && (minute != -1)) {
            AlarmTime(context, hour, minute);
            /**Setting Alarm**/
            mAlarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(
                    "com.peacecorps.malaria.START_ALARM");
            PendingIntent pendingAlarm = PendingIntent.getBroadcast(context, 0,
                    alarmIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                    "com.peacecorps.malaria.isWeekly", false)) {
                /**Weekly Alarm**/
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        mAlarmScheduleTime.getTimeInMillis(), INTERVAL_WEEK,
                        pendingAlarm);
            } else {
                /**Daily Alarm**/
                mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        mAlarmScheduleTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingAlarm);
            }
        }

    }

    public void AlarmTime(Context context, int hour, int minute) {
        /**Setting The Alarm Time **/
        Date date = new Date();
        mAlarmScheduleTime = Calendar.getInstance();
        mAlarmScheduleTime.setTime(date);

        Calendar dateNow = Calendar.getInstance();
        dateNow.setTime(date);

        mAlarmScheduleTime.set(Calendar.HOUR_OF_DAY, hour);
        mAlarmScheduleTime.set(Calendar.MINUTE, minute);

        if (mAlarmScheduleTime.before(dateNow)) {
            mAlarmScheduleTime.add(Calendar.DATE, 1);
        }

    }
}