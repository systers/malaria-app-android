package com.peacecorps.malaria.ui.play.medicine_store;

import android.content.Context;
import android.text.TextUtils;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.play.medicine_store.MedicineStoreContract.MedicineMvpPresenter;
import com.peacecorps.malaria.ui.play.medicine_store.MedicineStoreContract.MedicineMvpView;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public class MedicineStorePresenter<V extends MedicineMvpView> extends BasePresenter<V> implements MedicineMvpPresenter<V> {

    MedicineStorePresenter(AppDataManager manager, Context context) {
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

    // check if drug is weekly or daily and calls displayMedicineStoreValues with drugName(by prefernces) and daysLeft
    @Override
    public void checkMedicineStore() {
        int store = getDataManager().getMedicineStoreValue();
        String drugName = getDataManager().getDrugPicked();
        String daysLeft;
        //if drug is weekly
        if (drugName.compareTo(getContext().getResources().getString(R.string.med_option_three)) == 0) {
            if (store >= 0) {
                daysLeft = store + " Weeks";
            } else {
                store = store * -1;
                daysLeft = store + " Weeks Due";
            }
        } else {
            //if drug is daily
            if (store >= 0) {
                daysLeft = store + " Days";
            } else {
                store = store * -1;
                daysLeft = store + " Days Due";
            }
        }
        getView().displayMedicineStoreValues(drugName, daysLeft);
    }

    @Override
    public boolean checkMedicineNumberValidity(String text) {
        if (testIsEmpty(text))
            return false;
        int num = Integer.valueOf(text);
        return num > 0 && num <= 500;

    }

    @Override
    public boolean testIsEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

    // updates total medicine left in store and calls checkMedicineStore for UI update
    @Override
    public void updateMedicineStoreValue(String text) {
        int quantity = Integer.parseInt(text);
        int currValue = getDataManager().getMedicineStoreValue();
        getDataManager().setMedicineStoreValue(currValue + quantity);
        checkMedicineStore();
    }

    @Override
    public int getMedicineStoreValue() {
        return getDataManager().getMedicineStoreValue();
    }

    // creates message body and returns it as a string
    @Override
    public String getMessageBodyForOrder() {
        int storeValue = getMedicineStoreValue();
        String flag;
        if (getDataManager().isDosesWeekly())
            flag = "weeks";
        else flag = "days";

        return "My malaria pills will last for the coming  " + "<b>" + storeValue + "</b>" + " " +
                flag + " only.<br> Send the following immediately: <br>" +
                "Medicine Name:     " + "<b>" + getDataManager().getDrugPicked() + "</b>" + "<br>" +
                "Quantity:          ";
    }

    @Override
    public boolean isDrugWeekly() {
        return getDataManager().isDosesWeekly();
    }

    @Override
    public int getAlertReminderNumber() {
        return getDataManager().getAlertNumberDaysOrWeeks();
    }

    // checking if entered number is valid or not, putting a maximum 30days advance reminder
    @Override
    public boolean checkAlertReminderValidity(String text) {
        if (testIsEmpty(text))
            return false;
        int num = Integer.valueOf(text);
        return num > 0 && num <= 30;
    }

    @Override
    public void updateAlertReminder(int number) {
        getDataManager().setAlertNumberDaysOrWeeks(number);
    }


}
