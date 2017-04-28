package com.peacecorps.malaria.reciever;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.services.AlarmService;

import java.util.Calendar;
import java.util.Date;

public class DrugReminderReceiver extends BroadcastReceiver {
    static SharedPreferenceStore mSharedPreferenceStore;
    private static int mDrugAcceptedCount;
    private NotificationManager alarmNotificationManager;
    private static int mDrugRejectedCount;
    private int flag;
    String TAG = getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        switch (intent.getAction()) {
            case "0":
                //not taken
                notTaken(context);
                break;
            case "1":
                //snooze
                if (flag == 0 || flag == 2) {
                    snooze(context);
                } else {
                    Toast.makeText(context, "You have already taken medicine, no need to snooze.", Toast.LENGTH_SHORT).show();
                }
                break;
            case "2":
                //taken
                taken(context);
                break;
        }
        alarmNotificationManager.cancel(12345);
        playBlackGroundSound(context);
    }

    private void playBlackGroundSound(Context context) {
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.soundsmedication);
        final MediaPlayer mp = MediaPlayer.create(context,sound);
        mp.start();
    }

    private void notTaken(Context context) {
        getSharedPreferences(context);
        getSettings(context);
        if (flag == 0) {
            if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                    "com.peacecorps.malaria.isWeekly", false)) {
                saveUsersettings(context,true, false);
                /**Marked as Not Taken. No reminders now,it ll be for next time now.**/
                DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(context);
                databaseSQLiteHelper.getUserMedicationSelection(context, "weekly", Calendar.getInstance().getTime(), "no", computeAdherenceRate(context));
                changeWeeklyAlarmTime(context);

            } else {
                if (checkDrugTakenTimeInterval("dateDrugTaken",context) > 0) {
                    saveUsersettings(context,false, false);
                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(context);
                    databaseSQLiteHelper.getUserMedicationSelection(context, "daily", Calendar.getInstance().getTime(), "no", computeAdherenceRate(context));

                }
            }
        } else {
            if (flag == 1) {
                Toast.makeText(context, "You have taken the medicine. Modify it later, if you have not taken it.", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "You have not taken the medicine.", Toast.LENGTH_SHORT).show();
                snooze(context);
            }
        }
    }

    public void getSharedPreferences(Context context) {
        mSharedPreferenceStore.mPrefsStore = context
                .getSharedPreferences(context.getString(R.string.shared_preferences_store_time_picked),
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }


    public void getSettings(Context context) {
        /**Setting the reminder according to setting**/
        mDrugAcceptedCount = mSharedPreferenceStore.mPrefsStore.getInt(
                context.getString(R.string.shared_preferences_drug_accepted_count), 0);
        mDrugRejectedCount = mSharedPreferenceStore.mPrefsStore.getInt(
                context.getString(R.string.shared_preferences_drug_rejected_count), 0);

        Calendar c = Calendar.getInstance();
        int d = c.get(Calendar.DATE);
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(context);
        if (sqLite.getStatus(d, m, y).equalsIgnoreCase("yes") == true) {
            flag = 1;
        } else if (sqLite.getStatus(d, m, y).equalsIgnoreCase("no") == true) {
            flag = 2;
        } else
            flag = 0;

    }

    public void changeWeeklyAlarmTime(Context context) {
        /**Function to set the alarm for next week**/
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE) - 1;
        context.startService(
                new Intent(context, AlarmService.class));
        mSharedPreferenceStore.mEditor
                .putInt(context.getString(R.string.shared_preferences_alarm_hour), hour)
                .commit();
        mSharedPreferenceStore.mEditor
                .putInt(context.getString(R.string.shared_preferences_alarm_minute), minute)
                .commit();
    }

    public void saveUsersettings(Context context, Boolean state, Boolean isWeekly) {
        if (isWeekly) {
            /**Storing the Dates**/
            mSharedPreferenceStore.mEditor
                    .putLong(context.getString(R.string.shared_preferences_weekly_date),
                    new Date().getTime()).commit();
            mSharedPreferenceStore.mEditor.putBoolean(
                    context.getString(R.string.shared_preferences_is_weekly_drug_taken), state)
                    .commit();
        } else {
            mSharedPreferenceStore.mEditor
                    .putLong(context.getString(R.string.shared_preferences_date_drug_taken),
                    new Date().getTime()).commit();
            mSharedPreferenceStore.mEditor
                    .putBoolean(context.getString(R.string.shared_preferences_is_drug_taken),
                    state).commit();
        }
        mSharedPreferenceStore.mEditor
                .putInt(context.getString(R.string.shared_preferences_drug_rejected_count),
                mDrugRejectedCount).commit();
        mSharedPreferenceStore.mEditor
                .putInt(context.getString(R.string.shared_preferences_drug_accepted_count),
                mDrugAcceptedCount).commit();

    }

    public void snooze(Context context) {
        /**Snoozing The Alarm**/
        getSharedPreferences(context);
        Intent intent = new Intent(context,
                DrugReminderCallerReceiver.class);
        PendingIntent pendingSnooze = PendingIntent.getBroadcast(
                context.getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(context.ALARM_SERVICE);
        Calendar calender = Calendar.getInstance();
        Date date = new Date();
        calender.setTime(date);
        calender.add(Calendar.MINUTE, 10);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                calender.getTimeInMillis(), pendingSnooze);


    }


    private void taken(Context context) {
        getSharedPreferences(context);
        getSettings(context);
        if (flag == 0) {
            if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                    "com.peacecorps.malaria.isWeekly", false)) {
                /**Updates the date when weekly drug was taken and set the alarm for nex weekly Date**/
                saveUsersettings(context,true, true);
                DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(context);
                databaseSQLiteHelper.getUserMedicationSelection(context, "weekly", Calendar.getInstance().getTime(), "yes", computeAdherenceRate(context));
                changeWeeklyAlarmTime(context);

            } else {
                /**Updating the Daily Alarm and Cancelling today's notification because Drug is already Taken**/
                if (checkDrugTakenTimeInterval("dateDrugTaken", context) > 0) {
                    saveUsersettings(context,true, false);
                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(context);
                    databaseSQLiteHelper.getUserMedicationSelection(context, "daily", Calendar.getInstance().getTime(), "yes", computeAdherenceRate(context));

                }
            }
        } else {
            if (flag == 1) {
                Toast.makeText(context, "You have already taken medicine", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "You have not taken medicine. Modify later, if you have taken.", Toast.LENGTH_SHORT).show();
                snooze(context);
            }
        }
    }

    public long checkDrugTakenTimeInterval(String time, Context context) {
        long interval = 0;

        /**Calculating the Interval**/
        long today = new Date().getTime();
        Date tdy = Calendar.getInstance().getTime();
        tdy.setTime(today);
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(context);
        long takenDate = sqLite.getFirstTime();
        if (time.compareTo("firstRunTime") == 0) {
            if (takenDate != 0) {
                Log.d(TAG, "First Run Time at ADF->" + takenDate);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(takenDate);
                cal.add(Calendar.MONTH, 1);
                Date start = cal.getTime();
                int weekDay = cal.get(Calendar.DAY_OF_WEEK);
                if (SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly", false))
                    interval = sqLite.getIntervalWeekly(start, tdy, weekDay);
                else
                    interval = sqLite.getIntervalDaily(start, tdy);
                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria."
                        + time, takenDate).apply();

                return interval;
            } else
                return 1;
        } else {
            takenDate = SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria."
                    + time, takenDate);
            long oneDay = 1000 * 60 * 60 * 24;
            interval = (today - takenDate) / oneDay;
            return interval;
        }
    }

    public double computeAdherenceRate(Context context) {
        /**calculating Adherence Rate**/
        long interval = checkDrugTakenTimeInterval("firstRunTime",context);
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(context);
        long takenCount = sqLite.getCountTaken();
        double adherenceRate = ((double) takenCount / (double) interval) * 100;
        Log.d(TAG, "adherence:" + adherenceRate);
        return adherenceRate;
    }
}
