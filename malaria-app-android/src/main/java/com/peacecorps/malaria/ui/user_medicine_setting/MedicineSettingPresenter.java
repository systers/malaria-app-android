package com.peacecorps.malaria.ui.user_medicine_setting;

import android.content.Context;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.entities.AlarmTime;
import com.peacecorps.malaria.ui.base.BasePresenter;

import java.util.Calendar;
import java.util.Date;

import static com.peacecorps.malaria.ui.user_medicine_setting.MedicineSettingContract.*;

public class MedicineSettingPresenter<V extends View> extends BasePresenter<V> implements Presenter<V> {

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

        // inserted the alarm timing and days
        getDataManager().insertAlarmData(new AlarmTime(hour, minute, month, year, checkDay));

        switch (mDrugPicked) {
            case 0:
                getDataManager().setDoesWeekly(false);
                getDataManager().setDrugPicked(getContext().getString(R.string.med_option_one));
                break;
            case 1:
                getDataManager().setDoesWeekly(false);
                getDataManager().setDrugPicked(getContext().getString(R.string.med_option_two));
                break;
            case 2:
                getDataManager().setDoesWeekly(true);
                getDataManager().setDrugPicked(getContext().getString(R.string.med_option_three));
                break;
            default:
                getDataManager().setDoesWeekly(false);
                getDataManager().setDrugPicked(getContext().getString(R.string.med_option_one));
        }
        if (getDataManager().isFirstRun()) {
            getDataManager().setFirstRunTime(new Date().getTime());
            getDataManager().setFirstRun(false);
        }
        getDataManager().setDrugTaken(false);
        getDataManager().setUserPreferences(true);
        //Todo IMP start a alarm service
//        mFragmentContext.startService(new Intent(mFragmentContext,
//                AlarmService.class));
    }
}