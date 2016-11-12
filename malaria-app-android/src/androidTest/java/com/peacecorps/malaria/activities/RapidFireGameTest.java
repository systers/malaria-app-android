package com.peacecorps.malaria.activities;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peacecorps.malaria.R;

import junit.framework.TestCase;

/**
 * Created by yatna on 17/8/16.
 */
public class RapidFireGameTest extends ActivityInstrumentationTestCase2 {

    private RapidFireGame mActivity;
    private Button opt1;
    private Button opt2;
    private Button opt3;
    private TextView questionTv;
    private  TextView scoreTv;

    public RapidFireGameTest() {
        super(RapidFireGame.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mActivity=(RapidFireGame)getActivity();
        opt1 = (Button)mActivity.findViewById(R.id.button1);
        opt2 = (Button)mActivity.findViewById(R.id.button2);
        opt3 = (Button)mActivity.findViewById(R.id.button3);
        questionTv= (TextView)mActivity.findViewById(R.id.txtQuestion);
        scoreTv=(TextView)mActivity.findViewById(R.id.score);
    }
    public void testOpt1ButtonExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, opt1);
    }
    public void testOpt2ButtonExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, opt2);
    }
    public void testOpt3ButtonExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, opt3);
    }
    public void testQuestionTvExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, questionTv);
    }
    public void testScoreTvExists() {
        View mainActivityDecorView = mActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(mainActivityDecorView, scoreTv);
    }
}