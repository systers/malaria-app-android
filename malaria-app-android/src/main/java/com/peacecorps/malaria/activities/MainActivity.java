package com.peacecorps.malaria.activities;


import java.util.Calendar;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.adapter.FragmentAdapter;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;
import com.peacecorps.malaria.fragment.FirstAnalyticFragment;



public class MainActivity extends FragmentActivity {


    FragmentAdapter mAdapter;
    ViewPager mPager;
    Button mInfoButton;
    Button mTripButton;
    Button tempButton;
    Button userProfile;
    String TAGMA="MainActivity";
    private LinearLayout indicatingDotsContainer;
    private TextView[] dots;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addDots(0);
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

        //play symbol on the screen to open games and achievements
        tempButton =(Button)findViewById(R.id.tempButton);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewHomeActivity.class));
                finish();
            }
        });

        //user symbol on the screen to enter and push user's details
        userProfile =(Button)findViewById(R.id.userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),UserProfile.class));
                finish();
            }
        });


        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        /**Setting the Fragments**/
        mPager = (ViewPager) findViewById(R.id.vPager);
        mPager.setAdapter(mAdapter);
        Log.d(TAGMA, "Adapter Set");
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /**Setting the Values according to the User Input
                 * Medication Last Time
                 * Doses in A Row
                 * Adherence
                 * **/
                addDots(position);

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
            public void onPageScrollStateChanged(int state) {

            }

        });


    }

    public void addDots(int position){
        indicatingDotsContainer = (LinearLayout) findViewById(R.id.layoutDots);
        dots = new TextView[3];
        int dot_colorActive = getResources().getColor(R.color.selected_dot);
        int dot_colorInActive = getResources().getColor(R.color.dot_dark_screen1);
        indicatingDotsContainer.removeAllViews();

        //number of dots added to container equals number of slides
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(MainActivity.this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(dot_colorInActive);
            indicatingDotsContainer.addView(dots[i]);
        }

        //dot corresponding to current slide is given active color i.e white color
        if (dots.length > 0) {
            dots[position].setTextColor(dot_colorActive);
        }

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

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1
        if(requestCode==1)
        {
            ViewPager vp=(ViewPager)MainActivity.this.findViewById(R.id.vPager);
            vp.getAdapter().notifyDataSetChanged();
        }

    }


}