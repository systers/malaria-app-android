package com.peacecorps.malaria.ui.user_medicine_setting;

import android.content.Context;
import android.content.Intent;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.entities.AlarmTime;
import com.peacecorps.malaria.notifications.service.AlarmService;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import java.util.Calendar;
import java.util.Date;

import static com.peacecorps.malaria.ui.user_medicine_setting.MedicineSettingContract.*;

public class MedicineSettingPresenter<V extends SettingMvpView> extends BasePresenter<V> implements Presenter<V> {

    MedicineSettingPresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }

    @Override
    public void attachView(V view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


    @Override
    public void checkInitialAppInstall() {
        if (getDataManager().hasUserSetPreferences()) {
            getView().startMainActivity();
        }
    }

    /*
     *What all it sets?
     * drug--------------->INTEGER------------>0-Malarone,1-Doxycycline,2-Melfoquine
     * isWeekly----------->BOOLEAN------------>Tells whether the drug chosen was weekly or not
     * drugPicked ----> String --> Drug name
     * weeklyDate--------->LONG--------------->App registers the weekly date of Melfoquine, now this will be reminded weekly.
     * firstRunTime------->LONG--------------->First time the drug was taken.
     */
    @Override
    public void setUserAndMedicationPreference(int hour, int minute, int mDrugPicked) {
        Calendar calendar = Calendar.getInstance();
        int checkDay = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        ToastLogSnackBarUtil.showDebugLog("day "+ checkDay + "month " + month + "year " + year + " " + hour + " " + minute );
        // inserted the alarm timing and days
        getDataManager().insertAlarmData(new AlarmTime(hour, minute, month, year, checkDay));

        switch (mDrugPicked) {
            case 0:
                getDataManager().setDoseWeekly(false);
                getDataManager().setDrugPicked(getContext().getString(R.string.med_option_one));
                break;
            case 1:
                getDataManager().setDoseWeekly(false);
                getDataManager().setDrugPicked(getContext().getString(R.string.med_option_two));
                break;
            case 2:
                getDataManager().setDoseWeekly(true);
                getDataManager().setDrugPicked(getContext().getString(R.string.med_option_three));
                break;
            default:
                getDataManager().setDoseWeekly(false);
                getDataManager().setDrugPicked(getContext().getString(R.string.med_option_one));
        }
        if (getDataManager().isFirstRun()) {
            getDataManager().setFirstRunTime(new Date().getTime());
            getDataManager().setFirstRun(false);
        }
        getDataManager().setDrugTaken(false);
        getDataManager().setUserPreferences(true);
        // starting alarm service for reminding user for medicine
        getContext().startService(new Intent(getContext(), AlarmService.class));
    }

    /**
     * @param hr : hour selected in @TimePickerFragment (24 hour format)
     * @param mins  : min selected in Fragment
     */
    @Override
    public void convertToTwelveHours(int hr, int mins) {
        ToastLogSnackBarUtil.showDebugLog("" + hr + mins);
        String timeSet;
        int hour = 0;
        if (hr > 12) {
            hour = hr -12;
            timeSet = "PM";
        } else if (hr == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hr == 12) {
            timeSet = "PM";
            hour = hr;
        } else {
            timeSet = "AM";
            hour = hr;
        }

        String minutes;
        if (mins < 10) {
            minutes = getContext().getResources().getString(R.string.add_zero_beginning, mins);
        } else {
            minutes = String.valueOf(mins);
        }
        // Append the time to a stringBuilder
        String theTime = getContext().getResources().getString(R.string.time_picker, hour, minutes, timeSet);

        getView().setSelectedTime(theTime);
        getView().enableDoneButton(hr, mins);
    }
}