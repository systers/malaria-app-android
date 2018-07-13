package com.peacecorps.malaria.ui.play.medicine_store;

import com.peacecorps.malaria.ui.base.MvpPresenter;
import com.peacecorps.malaria.ui.base.MvpView;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public interface MedicineStoreContract {
    interface MedicineMvpView extends MvpView {
        void displayMedicineStoreValues(String drugName, String daysLeft);
    }

    interface MedicineMvpPresenter<V extends MedicineMvpView> extends MvpPresenter<V> {
        void checkMedicineStore();
        boolean checkMedicineNumberValidity(String text);
        boolean testIsEmpty(String text);
        void updateMedicineStoreValue(String text);
        int getMedicineStoreValue();
        String getMessageBodyForOrder();
        boolean isDrugWeekly();
        int getAlertReminderNumber();
        boolean checkAlertReminderValidity(String text);
        void updateAlertReminder(int number);
    }
}
