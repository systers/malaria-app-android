package com.peacecorps.malaria.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.code.activities.NewHomeActivity;

/**
 * Created by yatna on 17/8/16.
 */
public class NewHomeActivityTest extends ActivityInstrumentationTestCase2 {

    private NewHomeActivity mActivity;
    private Button mBadgeButton;
    private Button mMythButton;
    private Button mQuizButton;
    private Button mOrderButton;

    public NewHomeActivityTest() {
        super("com.peacecorps.malaria.activities",NewHomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity = (NewHomeActivity) getActivity();
        mBadgeButton= (Button)mActivity.findViewById(R.id.btn_badge_screen);
        mQuizButton=(Button)mActivity.findViewById(R.id.btn_rapid_fire);
        mMythButton=(Button)mActivity.findViewById(R.id.btn_myth_vs_fact);
        mOrderButton=(Button)mActivity.findViewById(R.id.btn_medicine_store);

    }
    public void testBadgeRoomButtonsExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, mBadgeButton);
    }
    public void testMythVsFactButtonsExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, mMythButton);
    }
    public void testRapidFireQuizButtonsExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, mQuizButton);
    }
    public void testMedicineStoreButtonsExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, mOrderButton);
    }
}