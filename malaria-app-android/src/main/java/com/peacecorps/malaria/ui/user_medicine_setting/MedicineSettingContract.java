package com.peacecorps.malaria.ui.user_medicine_setting;

import com.peacecorps.malaria.ui.base.MvpPresenter;
import com.peacecorps.malaria.ui.base.MvpView;

public interface MedicineSettingContract {
    interface SettingMvpView extends MvpView {
        void startMainActivity();
        void setSelectedTime(String theTime);
        void enableDoneButton(int h, int min);
    }

    interface Presenter<V extends SettingMvpView> extends MvpPresenter<V> {
        void checkInitialAppInstall();
        void setUserAndMedicationPreference(int hour, int minute, int drugPickedNo);
        void convertToTwelveHours(int hours, int mins);
    }
}
