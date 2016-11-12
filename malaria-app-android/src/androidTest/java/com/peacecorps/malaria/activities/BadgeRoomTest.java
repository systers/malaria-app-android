package com.peacecorps.malaria.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.peacecorps.malaria.R;

import junit.framework.TestCase;

/**
 * Created by yatna on 17/8/16.
 */
public class BadgeRoomTest extends ActivityInstrumentationTestCase2 {

    private BadgeRoom mActivity;
    private Button shareButton;
    private LinearLayout achievementCategory1;
    private LinearLayout achievementCategory2;
    private LinearLayout achievementCategory3;
    public BadgeRoomTest() {
        super("com.peacecorps.malaria.activities",BadgeRoom.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity = (BadgeRoom) getActivity();
        shareButton=(Button)mActivity.findViewById(R.id.shareButton);
        achievementCategory1=(LinearLayout)mActivity.findViewById(R.id.cat1_layout);
        achievementCategory2=(LinearLayout)mActivity.findViewById(R.id.cat2_layout);
        achievementCategory3=(LinearLayout)mActivity.findViewById(R.id.cat3_layout);
    }
    public void testShareButtonsExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, shareButton);
    }
    public void testAchievementCat1Exists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, achievementCategory1);
    }
    public void testAchievementCat2Exists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, achievementCategory2);
    }
    public void testAchievementCat3Exists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, achievementCategory3);
    }
}