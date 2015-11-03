package com.peacecorps.malaria;


import java.util.Calendar;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class MainActivity extends FragmentActivity {


    FragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    Button mInfoButton;
    Button mTripButton;
    String TAGMA="MainActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(this);
        /*Method opens the Info Hub
        *Tiny 'i' symbol in the Setup Screen is Info Hub Button
        */
        mInfoButton = (Button) findViewById(R.id.infoButton);
        mInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), InfoHubFragmentActivity.class));
                finish();

            }
        });
        Log.d(TAGMA, "Info Hub Button initialized");

        /*Method opens the Plan My Trip
        *Tiny 'bus' symbol in the Setup Screen is Plan My Trip Button
        */
        mTripButton = (Button) findViewById(R.id.tripButton);
        mTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(),TripIndicatorFragmentActivity.class));
                finish();
            }
        });

        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        /**Setting the Fragments**/
        mPager = (ViewPager) findViewById(R.id.vPager);
        mPager.setAdapter(mAdapter);
        Log.d(TAGMA, "Adapter Set");
        mIndicator = (CirclePageIndicator) findViewById(R.id.vIndicator);
        mIndicator.setViewPager(mPager);
        mIndicator.setOnPageChangeListener(new OnPageChangeListener() {



            @Override
            public void onPageSelected(int position) {
                /**Setting the Values according to the User Input
                 * Medication Last Time
                 * Doses in A Row
                 * Adherence
                 * **/
                if (position == 1) {

                    Log.d(TAGMA,"Keeping Date");
                    if (FirstAnalyticFragment.checkMediLastTakenTime != null) {
                        FirstAnalyticFragment.checkMediLastTakenTime
                                .setText(SharedPreferenceStore.mPrefsStore
                                        .getString(
                                                "com.peacecorps.malaria.checkMediLastTakenTime",
                                                "").toString());

                        Log.d(TAGMA,"Calculating Doses");
                        int currentDose = 0,dosesInaRow=0;
                        if (SharedPreferenceStore.mPrefsStore.getBoolean(
                                "com.peacecorps.malaria.isWeekly", false)) {
                            dosesInaRow =sqLite.getDosesInaRowWeekly();
                            SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.weeklyDose", dosesInaRow).apply();
                            currentDose = dosesInaRow;
                            Log.d(TAGMA, "Weekly");
                        } else {
                            //currentDose = SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.dailyDose", 0);
                            dosesInaRow=sqLite.getDosesInaRowDaily();
                            SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose",dosesInaRow).apply();
                            currentDose=dosesInaRow;
                            Log.d(TAGMA, "Daily");
                        }
                        FirstAnalyticFragment.doses.setText("" + currentDose);
                        Log.d(TAGMA, "Doses in a Row:" + dosesInaRow);

                        Log.d(TAGMA,"Calculating Adherence");
                        long interval = checkDrugTakenTimeInterval("firstRunTime");
                        int takenCount = sqLite.getCountTaken();
                        double adherenceRate;
                        Log.d(TAGMA,""+ interval);
                        Log.d(TAGMA,""+ takenCount);
                        if(interval!=0)
                            adherenceRate = ((double)takenCount / (double)interval) * 100;
                        else
                            adherenceRate = 100;
                        String ar=String.format("%.1f ",adherenceRate);
                        FirstAnalyticFragment.adherence.setText("" + ar + "%");
                    }

                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }

            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }


        });

    }
    /*Calculating Interval between two time*/
    public long checkDrugTakenTimeInterval(String time) {

        long interval = 0;
        long today = new Date().getTime();
        Date tdy= Calendar.getInstance().getTime();
        tdy.setTime(today);
        DatabaseSQLiteHelper sqLite= new DatabaseSQLiteHelper(this);
        long takenDate= sqLite.getFirstTime();
        if(time.compareTo("firstRunTime")==0) {
            if(takenDate!=0) {
                Log.d(TAGMA, "First Run Time at FAF->" + takenDate);
                Calendar cal=Calendar.getInstance();
                cal.setTimeInMillis(takenDate);
                cal.add(Calendar.MONTH, 1);
                Date start=cal.getTime();
                int weekDay=cal.get(Calendar.DAY_OF_WEEK);
                if(SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",false))
                    interval=sqLite.getIntervalWeekly(start,tdy,weekDay);
                else
                    interval=sqLite.getIntervalDaily(start,tdy);
                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria."
                        + time, takenDate).apply();
                /*long oneDay = 1000 * 60 * 60 * 24;
                interval = (today - takenDate) / oneDay;*/
                return interval;
            }
            else
                return 1;
        }
        else {
            takenDate=SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria."
                    + time, takenDate);
            long oneDay = 1000 * 60 * 60 * 24;
            interval = (today - takenDate) / oneDay;
            return interval;
        }
    }




}