package com.peacecorps.malaria.ui.user_profile;

import android.content.Context;
import android.text.TextUtils;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.user_profile.UserProfileContract.UserProfileMvpPresenter;
import com.peacecorps.malaria.ui.user_profile.UserProfileContract.UserProfileMvpView;

/**
 * Created by Anamika Tripathi on 13/7/18.
 */
public class UserProfilePresenter<V extends UserProfileMvpView> extends BasePresenter<V> implements UserProfileMvpPresenter<V> {

    UserProfilePresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }

    @Override
    public void setPreviousDetails() {
        getView().setInitialValuesIfAvailable(
                getDataManager().getUserName(),
                getDataManager().getUserEmail(),
                getDataManager().getUserAge(),
                getDataManager().getDrugPicked()
        );
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
    public void setNewDetails(String name, String email, int age) {
        getDataManager().setUserName(name);
        getDataManager().setUserEmail(email);
        getDataManager().setUserAge(age);
    }

    @Override
    public boolean testIsEmpty(String text) {
        return TextUtils.isEmpty(text);
    }

    @Override
    public boolean isAgeValid() {
        if(testIsEmpty(getView().getUserAge()))
            return false;
        String text = getView().getUserAge();
        return text.length()<= 2;
    }

    @Override
    public boolean isEmailValid() {
        return !testIsEmpty(getView().getUserEmail()) && android.util.Patterns.EMAIL_ADDRESS.matcher(getView().getUserEmail()).matches();
    }

    @Override
    public boolean checkError() {
        return getView().checkAgeError() && getView().checkNameError() && getView().checkEmailError();
    }
}
