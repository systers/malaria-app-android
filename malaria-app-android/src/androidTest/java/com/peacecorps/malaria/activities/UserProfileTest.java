package com.peacecorps.malaria.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.peacecorps.malaria.R;

import junit.framework.TestCase;

/**
 * Created by yatna on 17/8/16.
 */
public class UserProfileTest extends ActivityInstrumentationTestCase2 {

    private UserProfile mActivity;
    private EditText userNameEt;
    private EditText userEmailEt;
    private EditText userAgeEt;
    private EditText userMedicineTypeEt;
    private Button saveData;

    public UserProfileTest() {
        super(UserProfile.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity=(UserProfile)getActivity();
        userNameEt=(EditText)mActivity.findViewById(R.id.user_name);
        userEmailEt=(EditText)mActivity.findViewById(R.id.user_email);
        userAgeEt=(EditText)mActivity.findViewById(R.id.user_age);
        userMedicineTypeEt=(EditText)mActivity.findViewById(R.id.user_medicine_type);
        saveData=(Button)mActivity.findViewById(R.id.user_profile_save);

    }
    public void testUserNameEtExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, userNameEt);
    }
    public void testUserEmailEtExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, userEmailEt);
    }
    public void testUserAgeEtExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, userAgeEt);
    }
    public void testUserMedicineEtExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, userMedicineTypeEt);
    }
    public void testSaveDataButtonExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, saveData);
    }
}