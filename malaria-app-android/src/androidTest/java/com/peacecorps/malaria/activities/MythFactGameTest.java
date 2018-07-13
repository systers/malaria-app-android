package com.peacecorps.malaria.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.TextView;

import com.peacecorps.malaria.R;

/**
 * Created by yatna on 18/8/16.
 */
public class MythFactGameTest extends ActivityInstrumentationTestCase2 {

    private MythFactGame mActivity;
    private TextView tvQuestion;
    private TextView tvUserCoins;
    private TextView trash;
    private TextView chest;
    public MythFactGameTest() {
        super(MythFactGame.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity=(MythFactGame)getActivity();
        tvUserCoins=(TextView)mActivity.findViewById(R.id.tv_total_score);
        tvQuestion=(TextView)mActivity.findViewById(R.id.tv_question);
        trash=(TextView)mActivity.findViewById(R.id.trash);
        chest=(TextView)mActivity.findViewById(R.id.chest);
    }
    public void testTrashTvExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, trash);
    }
    public void testChestTvExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, chest);
    }
    public void testQuestionTvExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, tvQuestion);
    }
    public void testUserCoinTvExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, tvUserCoins);
    }
}