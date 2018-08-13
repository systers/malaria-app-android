package com.peacecorps.malaria.notifications.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.code.reciever.DrugReminderCallerReceiver;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.DbHelper;
import com.peacecorps.malaria.notifications.service.AlarmService;
import com.peacecorps.malaria.notifications.DrugNotificationUtils;
import com.peacecorps.malaria.utils.CalendarFunction;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Anamika Tripathi on 9/8/18.
 */
public class DrugNotificationReceiver extends BroadcastReceiver {
    private int flag;
    private AppDataManager dataManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            dataManager = InjectionClass.provideDataManager(context);
            String action = intent.getAction();
            ToastLogSnackBarUtil.showDebugLog(action);
            if (action != null) {
                switch (action) {
                    case DrugNotificationUtils.ACTION_REJECT_MEDICINE:
                        //medicine not taken action
                        medicineNotTaken(context);
                        break;
                    case DrugNotificationUtils.ACTION_SNOOZE_MEDICINE:
                        //snooze
                        if (flag == 0 || flag == 2) {
                            snoozeAlarmNotification(context);
                        } else {
                            Toast.makeText(context, "You have already taken medicine, no need to snooze.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case DrugNotificationUtils.ACTION_ACCEPTED_MEDICINE:
                        // medicine taken action
                        medicineTaken(context);
                        break;
                    default: ToastLogSnackBarUtil.showErrorLog("unknown action selected");
                }
            }

        }
        DrugNotificationUtils.clearAllNotification(context);
        playBlackGroundSound(context);
    }

    private void medicineTaken(final Context context) {

        final boolean isWeekly = dataManager.isDosesWeekly();
        final String drugPicked = dataManager.getDrugPicked();

        Calendar cal = Calendar.getInstance();
        int d = cal.get(Calendar.DATE);
        int m = cal.get(Calendar.MONTH);
        int y = cal.get(Calendar.YEAR);

        dataManager.getDailyStatus(d, m, y, new DbHelper.LoadStringCallback() {
            @Override
            public void onDataLoaded(String status) {
                if ("yes".equalsIgnoreCase(status)) {
                    flag = 1;
                } else if ("no".equalsIgnoreCase(status)) {
                    flag = 2;
                } else {
                    flag = 0;
                }

                if (flag == 0) {
                    if (isWeekly) {
                        saveUserSettings(true, true);
                        // Marked as Not Taken. No reminders now,it ll be for next time now
                        checkWeeklyInterval(drugPicked, "yes");
                        changeWeeklyAlarmTime(context);
                    } else {
                        checkDailyInterval(true, drugPicked, "yes");
                    }
                } else {
                    if (flag == 1) {
                        ToastLogSnackBarUtil.showToast(context, "You have already taken medicine");
                    } else {
                        ToastLogSnackBarUtil.showToast(context, "You have not taken medicine. " +
                                "Modify later, if you have taken");
                        snoozeAlarmNotification(context);
                    }
                }

            }
        });
    }

    private void medicineNotTaken(final Context context) {
        final boolean isWeekly = dataManager.isDosesWeekly();
        final String drugPicked = dataManager.getDrugPicked();

        Calendar cal = Calendar.getInstance();
        int d = cal.get(Calendar.DATE);
        int m = cal.get(Calendar.MONTH);
        int y = cal.get(Calendar.YEAR);

        dataManager.getDailyStatus(d, m, y, new DbHelper.LoadStringCallback() {
            @Override
            public void onDataLoaded(String status) {
                if ("yes".equalsIgnoreCase(status)) {
                    flag = 1;
                } else if ("no".equalsIgnoreCase(status)) {
                    flag = 2;
                } else {
                    flag = 0;
                }

                if (flag == 0) {
                    if (isWeekly) {
                        saveUserSettings(false, true);
                        // Marked as Not Taken. No reminders now,it ll be for next time now
                        checkWeeklyInterval(drugPicked, "no");
                        changeWeeklyAlarmTime(context);
                    } else {
                        checkDailyInterval(false, drugPicked, "no");
                    }
                } else {
                    if (flag == 1) {
                        ToastLogSnackBarUtil.showToast(context, "You have taken the medicine. " +
                                "Modify it later, if you have not taken it.");
                    } else {
                        ToastLogSnackBarUtil.showToast(context, "You have not taken the medicine.");
                        snoozeAlarmNotification(context);
                    }
                }
            }
        });
    }

    /**
     * snoozing the alarm
     */
    public void snoozeAlarmNotification(Context context) {
        Intent intent = new Intent(context,
                DrugReminderCallerReceiver.class);
        PendingIntent pendingSnooze = PendingIntent.getBroadcast(
                context.getApplicationContext(), DrugNotificationUtils.ACTION_SNOOZE_PENDING_INTENT_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Calendar calender = Calendar.getInstance();
        Date date = new Date();
        calender.setTime(date);
        calender.add(Calendar.MINUTE, 10);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calender.getTimeInMillis(), pendingSnooze);
        }
    }

    public void saveUserSettings(Boolean state, Boolean isWeekly) {
        if (isWeekly) {
            dataManager.setLongWeeklyDate(new Date().getTime());
            dataManager.setWeeklyDrugTaken(state);
        } else {
            dataManager.setDateDrugTaken(new Date().getTime());
            dataManager.setDailyDrugTaken(state);
        }
    }

    private void checkWeeklyInterval(final String drugPicked, final String status) {
        // Calculating the Interval
        final long today = new Date().getTime();
        final Date tdy = Calendar.getInstance().getTime();
        tdy.setTime(today);

        dataManager.getFirstTimeByTimeStamp(new DbHelper.LoadLongCallback() {
            @Override
            public void onDataLoaded(Long takenDate) {
                final long interval;
                if (takenDate != 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(takenDate);
                    cal.add(Calendar.MONTH, 1);
                    Date start = cal.getTime();
                    int weekDay = cal.get(Calendar.DAY_OF_WEEK);
                    interval = CalendarFunction.getIntervalWeekly(start, tdy, weekDay);
                    dataManager.setFirstRunTime(takenDate);
                } else {
                    interval = 1;
                }
                // calculating Adherence Rate & save user details
                calculateAdherenceAndSetUserSelection(interval, drugPicked, "weekly", status);
            }
        });
    }

    private void checkDailyInterval(boolean state, String drugPicked, final String status) {
        long takenDate = dataManager.getDateDrug();
        long oneDay = 1000 * 60 * 60 * 24;
        long today = new Date().getTime();
        long interval = (today - takenDate) / oneDay;
        if (interval > 0) {
            saveUserSettings(state, false);
            calculateAdherenceAndSetUserSelection(interval, drugPicked, "daily", status);
        }
    }

    private void calculateAdherenceAndSetUserSelection(final double interval, final String drugPicked, final String choice,
                                                       final String status) {
        dataManager.getMedicineCountTaken(new DbHelper.LoadIntegerCallback() {
            @Override
            public void onDataLoaded(int takenCount) {
                double adherenceRate = ((double) takenCount / interval) * 100;
                dataManager.setUserMedicineSelection(drugPicked, choice, Calendar.getInstance().getTime(),
                        status, adherenceRate);
            }
        });
    }

    private void playBlackGroundSound(Context context) {
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.soundsmedication);
        final MediaPlayer mp = MediaPlayer.create(context, sound);
        mp.start();
    }

    /**
     * Function to set the alarm for next week
     */
    private void changeWeeklyAlarmTime(Context context) {
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE) - 1;
        context.startService(
                new Intent(context, AlarmService.class));
        dataManager.updateAlarmTime(hour, minute);
    }
}
