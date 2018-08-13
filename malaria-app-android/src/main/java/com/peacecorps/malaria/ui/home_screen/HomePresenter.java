package com.peacecorps.malaria.ui.home_screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.DbHelper;
import com.peacecorps.malaria.notifications.service.AlarmService;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.home_screen.HomeContract.HomeMvpView;
import com.peacecorps.malaria.utils.CalendarFunction;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Anamika Tripathi on 5/8/18.
 */
public class HomePresenter<V extends HomeMvpView> extends BasePresenter<V> implements HomeContract.HomeMvpPresenter<V> {
    HomePresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }

    /**
     * takes date from calendar, uses SimpleDataFormat to parse date in to current Day & date in specific format
     * calls setCurrentDayAndDelete to set date+day in fragment
     */
    @Override
    public void checkDayDateOfWeek() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        String date = new SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault()).format(d);
        getView().setCurrentDayAndDate(date, dayOfTheWeek);
    }

    /**
     * check if medicine in Store count is less than user's setting for warning?
     * If yes, show warning message
     */
    @Override
    public void checkWarningVisibility() {
        // alert ime set by user in MedicineStoreFragment
        int alertTime = getDataManager().getAlertNumberDaysOrWeeks();
        int medicineCount = getDataManager().getMedicineStoreValue();
        //display warning
        if (alertTime != 1 && medicineCount < alertTime) {
            getView().setWarningText();
        }

    }

    /**
     * decides UI decision by drug history saved in preferences
     * calls appropriate getView function to update UI
     */
    @Override
    public void decideDrugTakenForUI() {
        final long today = new Date().getTime();
        getDataManager().getFirstTimeByTimeStamp(new DbHelper.LoadLongCallback() {
            @Override
            public void onDataLoaded(Long value) {
                long takenWeeklyDate = getDataManager().getLongWeeklyDate();
                long takenDailyDate = getDataManager().getDateDrug();
                boolean isWeekly = getDataManager().isDosesWeekly();
                boolean isWeeklyDrugTaken = getDataManager().isWeeklyDrugTaken();
                if (isWeekly) {
                    long interval;
                    long oneDay = 1000 * 60 * 60 * 24;
                    interval = (today - takenWeeklyDate) / oneDay;

                    if (interval == 0) {
                        if (isWeeklyDrugTaken) {
                            getView().isDrugTakenUI();
                        } else {
                            getView().newDayUI();
                        }
                    } else {
                        if (interval < 7 && interval > 0) {
                            if (isWeeklyDrugTaken) {
                                getView().isDrugTakenUI();
                            } else {
                                getDataManager().setDosesWeekly(0);
                                getView().missedWeekUI();
                                getView().newDayUI();
                            }
                        } else if (interval > 7) {
                            getDataManager().setDrugAcceptedCount(0);
                            getDataManager().setDosesWeekly(0);
                            getView().missedWeekUI();
                            getView().newDayUI();
                        }
                    }
                } else {
                    long interval;
                    long oneDay = 1000 * 60 * 60 * 24;
                    interval = (today - takenDailyDate) / oneDay;
                    if (interval == 0) {
                        if (getDataManager().isDrugTaken()) {
                            getView().isDrugTakenUI();
                        } else {
                            getView().isDrugNotTakenUI();
                        }
                    } else {
                        if (interval > 1) {
                            getDataManager().setDosesDaily(0);
                        }
                        getView().newDayUI();
                    }
                }
            }
        });

    }

    /**
     * increases drug accepted count in preferences
     */
    @Override
    public void increaseDrugAcceptedCount() {
        // get the value
        int value = getDataManager().getDrugAcceptedCount();
        // increase by 1
        getDataManager().setDrugAcceptedCount(value + 1);
    }

    /**
     * used to update in db if medicine is taken or not
     * decrease current medicine count in preferences if taken
     * increase user-points in preferences if taken
     */
    @Override
    public void updateUserMedicineSelection(double adherenceRate, boolean isAcceptButton) {
        // returns true if weekly or false for daily
        boolean isWeekly = getDataManager().isDosesWeekly();
        String drugPicked = getDataManager().getDrugPicked();
        Date date = Calendar.getInstance().getTime();
        if (isAcceptButton) {
            if (isWeekly) {
                decideDrugTakenUIBoolean(true, true);
                getDataManager().setUserMedicineSelection(drugPicked, "weekly", date,
                        "yes", adherenceRate);
                getDataManager().getDosesInaRowWeekly(new DbHelper.LoadIntegerCallback() {
                    @Override
                    public void onDataLoaded(int value) {
                        getDataManager().setDosesWeekly(value);
                    }
                });
            } else {
                decideDrugTakenUIBoolean(false, true);
                getDataManager().setUserMedicineSelection(drugPicked, "daily", date, "yes", adherenceRate);
                getDataManager().getDosesInaRowDaily(new DbHelper.LoadIntegerCallback() {
                    @Override
                    public void onDataLoaded(int value) {
                        getDataManager().setDosesDaily(value);
                    }
                });
            }
            //get user's score
            int score = getDataManager().getUserScore();
            //get medicine in store
            int medicineStore = getDataManager().getMedicineStoreValue();
            //increase score if medicine is taken
            getDataManager().setUserScore(score + 1);
            //decrease medicine store by one if medicine is taken
            getDataManager().setMedicineStoreValue(medicineStore - 1);
            ToastLogSnackBarUtil.showDebugLog("HomePresenter/updateUserMedicineSelection: score updated");
        } else {
            if (isWeekly) {
                decideDrugTakenUIBoolean(true, false);
                getDataManager().setUserMedicineSelection(drugPicked, "weekly", date, "no", adherenceRate);
            } else {
                decideDrugTakenUIBoolean(false, false);
                getDataManager().setUserMedicineSelection(drugPicked, "daily", date, "no", adherenceRate);
            }
        }
    }

    /**
     * determines interval for firstRunTime and calls computerAdherenceInterval to calculate adherence
     */
    @Override
    public void checkDrugIntervalFirstRunTime(final boolean isAcceptButton) {

        final long today = new Date().getTime();
        getDataManager().getFirstTimeByTimeStamp(new DbHelper.LoadLongCallback() {
            @Override
            public void onDataLoaded(Long value) {
                long interval;
                if (value != 0) {
                    ToastLogSnackBarUtil.showDebugLog("First Run Time at FAF->" + value);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(value);
                    cal.add(Calendar.MONTH, 1);

                    Date start = cal.getTime();
                    Date end = Calendar.getInstance().getTime();
                    end.setTime(today);
                    // saving in preferences
                    getDataManager().setFirstRunTime(value);
                    if (getDataManager().isDosesWeekly()) {
                        int dayWeekly = getDataManager().getDayWeekly();
                        interval = CalendarFunction.getIntervalWeekly(start, end, dayWeekly);
                    } else {
                        interval = CalendarFunction.getIntervalDaily(start, end);
                    }
                } else {
                    interval = 1;
                }
                computeAdherenceRate(interval, isAcceptButton);
            }
        });

    }

    @Override
    public void checkDrugIntervalWeeklyDate() {
        final long today = new Date().getTime();
        final long takenDate = getDataManager().getLongWeeklyDate();
        getDataManager().getFirstTimeByTimeStamp(new DbHelper.LoadLongCallback() {
            @Override
            public void onDataLoaded(Long value) {
                if (takenDate == 0) {
                    long oneDay = 1000 * 60 * 60 * 24;
                    long interval;
                    interval = (today - takenDate) / oneDay;
                    if (interval > 1) {
                        changeWeeklyAlarmTime();
                    }
                }
            }
        });
    }

    /**
     * @param interval : interval between drug taken time
     *                 desc : calculates adherence rate & calls updateUserMedicineSelection
     */
    private void computeAdherenceRate(final long interval, final boolean isAcceptButton) {
        getDataManager().getMedicineCountTaken(new DbHelper.LoadIntegerCallback() {
            @Override
            public void onDataLoaded(int value) {
                double adherenceRate = ((double) value / (double) interval) * 100;
                ToastLogSnackBarUtil.showDebugLog("HomePresenter/computerAdherence: " + interval + " " + value + " " + adherenceRate);
                updateUserMedicineSelection(adherenceRate, isAcceptButton);
            }
        });
    }

    /**
     * @param isWeekly : determines if drug is weekly or daily
     * @param isTaken  : determines if it is taken or not/depends on accept or reject button
     */
    @Override
    public void decideDrugTakenUIBoolean(boolean isWeekly, boolean isTaken) {
        if (isWeekly) {
            checkDrugIntervalWeeklyDate();
        }
        saveUserSettings(isTaken, isWeekly);
        if (isTaken) {
            getView().isDrugTakenUI();
        } else {
            getView().isDrugNotTakenUI();
        }
    }

    /**
     * increase drug reject count in preferences
     */
    @Override
    public void increaseDrugRejectCount() {
        int value = getDataManager().getDrugRejectedCount();
        getDataManager().setDrugRejectedCount(value + 1);
    }

    /**
     * set last medicine taken time
     * used in isDrugTakenUI
     */
    @Override
    public void storeMediTimeLastChecked() {
        String lastMedicationCheckedTime = "";
        Calendar c = Calendar.getInstance();
        lastMedicationCheckedTime = new SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault()).format(c.getTime());
        getDataManager().setMedicineLastTakenTime(lastMedicationCheckedTime);
    }

    /**
     * @param isTaken  : determines if it is taken or not/depends on accept or reject button
     * @param isWeekly : determines if drug is weekly or daily
     *                 desc :   checks weekly or daily drug, updates preference data
     */
    private void saveUserSettings(boolean isTaken, boolean isWeekly) {
        if (isWeekly) {
            // updates on last taken date
            getDataManager().setLongWeeklyDate(new Date().getTime());
            // sets true or false if drug is taken or not
            getDataManager().setWeeklyDrugTaken(isTaken);
        } else {
            getDataManager().setDateDrugTaken(new Date().getTime());
            getDataManager().setDrugTaken(isTaken);
        }
        // increases drug accepted count
        int count = getDataManager().getDrugAcceptedCount();
        getDataManager().setDrugAcceptedCount(count + 1);
    }

    private void changeWeeklyAlarmTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE) - 1;

        if (getContext() != null) {
            getContext().startService(new Intent(getContext(), AlarmService.class));
        }
        getView().startAlarmServiceClass();
        getDataManager().updateAlarmTime(hour, minute);
    }

}
