package com.peacecorps.malaria.ui.user_profile;

import com.peacecorps.malaria.ui.base.MvpPresenter;
import com.peacecorps.malaria.ui.base.MvpView;

/**
 * Created by Anamika Tripathi on 13/7/18.
 */

public interface UserProfileContract {
    interface UserProfileMvpView extends MvpView {
        void setInitialValuesIfAvailable(String name, String email, int age, String medicine);
        String getUserName();
        String getUserEmail();
        String getUserAge();
        boolean checkAgeError();
        boolean checkNameError();
        boolean checkEmailError();
    }

    interface UserProfileMvpPresenter<V extends UserProfileMvpView> extends MvpPresenter<V> {
        void setPreviousDetails();
        void setNewDetails(String name, String email, int age);
        boolean testIsEmpty(String text);
        boolean isAgeValid();
        boolean isEmailValid();
        boolean checkError();
    }
}
