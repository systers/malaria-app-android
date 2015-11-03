package com.peacecorps.malaria;
/*
Edited By Ankita
 */
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;


public class AlertDialogFragment extends DialogFragment {

    private static int mDrugAcceptedCount;
    private static int mDrugRejectedCount;
    static SharedPreferenceStore mSharedPreferenceStore;
    private NotificationManager alarmNotificationManager;
    private int flag;
    String TAGADF="";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /**
         * Turn Screen On and Unlock the keypad when this alert dialog is
         * displayed
         */
        getActivity().getWindow().addFlags(
                LayoutParams.FLAG_TURN_SCREEN_ON
                        | LayoutParams.FLAG_DISMISS_KEYGUARD);

        showNotification("Take Your Medicine!");

        /** Creating a alert dialog builder */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyAlertDialog);

        /** Creating Layout Inflater **/

        LayoutInflater inflater = getActivity().getLayoutInflater();

        /*Inflate and set the layout for the dialog
         Pass null as the parent view because its going in the dialog layout*/
        builder.setView(inflater.inflate(R.layout.alert_drug_reminder, null));

        /** Defining an OK button event listener */
        builder.setPositiveButton("Taken", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        getSharedPreferences();
                        getSettings();
                        if (flag == 0) {
                            if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                                    "com.peacecorps.malaria.isWeekly", false)) {
                                /**Updates the date when weekly drug was taken and set the alarm for nex weekly Date**/
                                saveUsersettings(true, true);
                                DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(getActivity());
                                databaseSQLiteHelper.getUserMedicationSelection(getActivity(), "weekly", Calendar.getInstance().getTime(), "yes", computeAdherenceRate());
                                changeWeeklyAlarmTime();
                                alarmNotificationManager.cancelAll();
                            } else {
                                /**Updating the Daily Alarm and Cancelling today's notification because Drug is already Taken**/
                                if (checkDrugTakenTimeInterval("dateDrugTaken") > 0) {
                                    saveUsersettings(true, false);
                                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(getActivity());
                                    databaseSQLiteHelper.getUserMedicationSelection(getActivity(), "daily", Calendar.getInstance().getTime(), "yes", computeAdherenceRate());
                                    alarmNotificationManager.cancelAll();
                                }
                            }
                            getActivity().finish();
                        }
                        else {
                            if (flag == 1) {
                                Toast.makeText(getActivity(), "You have already taken medicine", Toast.LENGTH_SHORT).show();
                                alarmNotificationManager.cancelAll();
                            } else {
                                Toast.makeText(getActivity(), "You have not taken medicine. Modify later, if you have taken.", Toast.LENGTH_SHORT).show();
                                snooze();
                            }
                        }
                    }
                });

            }
        }).setNegativeButton("Not Taken", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        getSharedPreferences();
                        getSettings();
                        if (flag == 0) {
                            if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                                    "com.peacecorps.malaria.isWeekly", false)) {
                                saveUsersettings(true, false);
                                /**Marked as Not Taken. No reminders now,it ll be for next time now.**/
                                DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(getActivity());
                                databaseSQLiteHelper.getUserMedicationSelection(getActivity(), "weekly", Calendar.getInstance().getTime(), "no", computeAdherenceRate());
                                changeWeeklyAlarmTime();
                                alarmNotificationManager.cancelAll();
                            } else {
                                if (checkDrugTakenTimeInterval("dateDrugTaken") > 0) {
                                    saveUsersettings(false, false);
                                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(getActivity());
                                    databaseSQLiteHelper.getUserMedicationSelection(getActivity(), "daily", Calendar.getInstance().getTime(), "no", computeAdherenceRate());
                                    alarmNotificationManager.cancelAll();
                                }
                            }
                        } else {
                            if (flag == 1) {
                                Toast.makeText(getActivity(), "You have taken the medicine. Modify it later, if you have not taken it.", Toast.LENGTH_SHORT).show();
                                alarmNotificationManager.cancelAll();
                            } else {
                                Toast.makeText(getActivity(), "You have not taken the medicine.", Toast.LENGTH_SHORT).show();
                                snooze();
                            }
                        }
                        getActivity().finish();
                    }
                });

            }
        }).setNeutralButton("Snooze", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (flag == 0 || flag == 2) {
                    snooze();
                } else {
                    Toast.makeText(getActivity(),"You have already taken medicine, no need to snooze.",Toast.LENGTH_SHORT).show();
                    alarmNotificationManager.cancelAll();
                }
            }
        }).setCancelable(false);

        /** Creating the alert dialog window */
        return builder.create();
    }

    public long checkDrugTakenTimeInterval(String time) {
        long interval = 0;

        /**Calculating the Interval**/
        long today = new Date().getTime();
        Date tdy= Calendar.getInstance().getTime();
        tdy.setTime(today);
        DatabaseSQLiteHelper sqLite= new DatabaseSQLiteHelper(getActivity());
        long takenDate= sqLite.getFirstTime();
        if(time.compareTo("firstRunTime")==0) {
            if(takenDate!=0) {
                Log.d(TAGADF, "First Run Time at ADF->" + takenDate);
                Calendar cal=Calendar.getInstance();
                cal.setTimeInMillis(takenDate);
                cal.add(Calendar.MONTH, 1);
                Date start=cal.getTime();
                int weekDay=cal.get(Calendar.DAY_OF_WEEK);
                if(SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",false))
                    interval=sqLite.getIntervalWeekly(start,tdy,weekDay);
                else
                    interval=sqLite.getIntervalDaily(start,tdy);
                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria."
                        + time, takenDate).apply();

                return interval;
            }
            else
                return 1;
        }
        else {
            takenDate=SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria."
                    + time, takenDate);
            long oneDay = 1000 * 60 * 60 * 24;
            interval = (today - takenDate) / oneDay;
            return interval;
        }
    }

    public void snooze() {
        /**Snoozing The Alarm**/
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getSharedPreferences();
                Intent intent = new Intent(getActivity(),
                        AlertCallerFragmentActivity.class);
                PendingIntent pendingSnooze = PendingIntent.getActivity(
                        getActivity().getApplicationContext(), 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getActivity()
                        .getSystemService(getActivity().ALARM_SERVICE);
                Calendar calender = Calendar.getInstance();
                Date date = new Date();
                calender.setTime(date);
                calender.add(Calendar.MINUTE, 10);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        calender.getTimeInMillis(), pendingSnooze);
            }
        });
    }

    public void changeWeeklyAlarmTime() {
        /**Function to set the alarm for next week**/
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE) - 1;
        getActivity().startService(
                new Intent(getActivity(), AlarmService.class));
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AlarmHour", hour)
                .commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AlarmMinute", minute)
                .commit();
    }

    public void saveUsersettings(Boolean state, Boolean isWeekly) {
        if (isWeekly) {
            /**Storing the Dates**/
            mSharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.weeklyDate",
                    new Date().getTime()).commit();
            mSharedPreferenceStore.mEditor.putBoolean(
                    "com.peacecorps.malaria.isWeeklyDrugTaken", state).commit();
        } else {
            mSharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.dateDrugTaken",
                    new Date().getTime()).commit();
            mSharedPreferenceStore.mEditor.putBoolean("com.peacecorps.malaria.isDrugTaken",
                    state).commit();
        }
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugRejectedCount",
                mDrugRejectedCount).commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugAcceptedCount",
                mDrugAcceptedCount).commit();

    }

    public void getSettings() {

        /**Setting the reminder according to setting**/
        mDrugAcceptedCount = mSharedPreferenceStore.mPrefsStore.getInt(
                "com.peacecorps.malaria.drugAcceptedCount", 0);
        mDrugRejectedCount = mSharedPreferenceStore.mPrefsStore.getInt(
                "com.peacecorps.malaria.drugRejectedCount", 0);

        Calendar c = Calendar.getInstance();
        int d = c.get(Calendar.DATE);
        int m = c.get(Calendar.MONTH);
        int y = c.get(Calendar.YEAR);
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
        if(sqLite.getStatus(d,m,y).equalsIgnoreCase("yes")==true)
        {
            flag=1;
        }
        else if (sqLite.getStatus(d,m,y).equalsIgnoreCase("no")==true) {
            flag = 2;
        }
        else
             flag=0;

    }

    public void getSharedPreferences() {

        mSharedPreferenceStore.mPrefsStore = getActivity()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }

    /**
     * The application exits, if the user presses the back button
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }


    public void showNotification(String msg)
    {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);

        /**Setting Up Alarm**/
        alarmNotificationManager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0,
                new Intent(getActivity(), AlertCallerFragmentActivity.class), 0);

        Uri sound = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.soundsmedication);
        /**Building Notifications**/
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                getActivity()).setContentTitle("Malaria Prevention").setSmallIcon(R.drawable.appicon_themed)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        alamNotificationBuilder.setSound(sound);

        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }

    public double computeAdherenceRate() {
        /**calculating Adherence Rate**/
        long interval = checkDrugTakenTimeInterval("firstRunTime");
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
        long takenCount = sqLite.getCountTaken();
        double adherenceRate = ((double)takenCount / (double)interval) * 100;
        Log.d(TAGADF, "adherence:" + adherenceRate);
        return adherenceRate;
    }
}