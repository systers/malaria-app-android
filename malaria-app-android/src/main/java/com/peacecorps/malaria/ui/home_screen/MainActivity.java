package com.peacecorps.malaria.ui.home_screen;


import android.os.Bundle;

import com.peacecorps.malaria.R;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;

import com.peacecorps.malaria.code.fragment.HomeScreenFragment;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BaseActivity;
import com.peacecorps.malaria.ui.user_profile.UserProfileFragment;
import com.peacecorps.malaria.utils.BottomNavigationViewHelper;
import com.peacecorps.malaria.utils.InjectionClass;

public class MainActivity extends BaseActivity implements HomeContract.IHomeView, UserProfileFragment.OnUserFragmentListener {

    private HomePresenter<MainActivity> presenter;
    private BottomNavigationView bottomNavigationView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void init() {
        // setting up presenter and attaching views
        AppDataManager dataManager = InjectionClass.provideDataManager(this);
        presenter = new HomePresenter<>(dataManager, this);
        presenter.attachView(this);

        //setting up bottom navigation
        setBottomNavigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }

    private void setBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.home_screen_fragment);
        bottomNavigationView.getMenu().findItem(R.id.bnv_home).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.bnv_home:
                        fragment = new HomeScreenFragment();
                        loadFragment(fragment);
                        break;

                    case R.id.bnv_trip_button:
                        Toast.makeText(MainActivity.this, "trip button", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.bnv_info_button:
                        Toast.makeText(MainActivity.this, "info button", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.bnv_play_button:
                        Toast.makeText(MainActivity.this, "play button", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.bnv_user_profile:
                        fragment = new UserProfileFragment();
                        loadFragment(fragment);
                        break;
                    default:
                        fragment = new HomeScreenFragment();
                        loadFragment(fragment);
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void startHomeFragment() {
        bottomNavigationView.setSelectedItemId(R.id.home_screen_fragment);
        loadFragment(new HomeScreenFragment());
    }

    //    /*Calculating Interval between two time*/
//    public long checkDrugTakenTimeInterval(String time) {
//
//        long interval = 0;
//        long today = new Date().getTime();
//        Date tdy= Calendar.getInstance().getTime();
//        tdy.setTime(today);
//        DatabaseSQLiteHelper sqLite= new DatabaseSQLiteHelper(this);
//        long takenDate= sqLite.getFirstTime();
//        if(time.compareTo("firstRunTime")==0) {
//            if(takenDate!=0) {
//                Log.d(TAGMA, "First Run Time at FAF->" + takenDate);
//                Calendar cal=Calendar.getInstance();
//                cal.setTimeInMillis(takenDate);
//                cal.add(Calendar.MONTH, 1);
//                Date start=cal.getTime();
//                int weekDay=cal.get(Calendar.DAY_OF_WEEK);
//                if(SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",false))
//                    interval= CalendarFunction.getIntervalWeekly(start,tdy,weekDay);
//                else
//                    interval=CalendarFunction.getIntervalDaily(start,tdy);
//                // Todo add it to preferences later, not able to decide pref_key now
//                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria."
//                        + time, takenDate).apply();
//                /*long oneDay = 1000 * 60 * 60 * 24;
//                interval = (today - takenDate) / oneDay;*/
//                return interval;
//            }
//            else
//                return 1;
//        }
//        else {
//            takenDate=SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria."
//                    + time, takenDate);
//            long oneDay = 1000 * 60 * 60 * 24;
//            interval = (today - takenDate) / oneDay;
//            return interval;
//        }
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//        // check if the request code is same as what is passed  here it is 1
//        if(requestCode==1)
//        {
//            ViewPager vp=(ViewPager)MainActivity.this.findViewById(R.id.vPager);
//            vp.getAdapter().notifyDataSetChanged();
//        }

//        mAdapter = new FragmentAdapter(getSupportFragmentManager());
//        /**Setting the Fragments**/
//        mPager = (ViewPager) findViewById(R.id.vPager);
//        mPager.setAdapter(mAdapter);
//        Log.d(TAGMA, "Adapter Set");
//        mIndicator = (CirclePageIndicator) findViewById(R.id.vIndicator);
//        mIndicator.setViewPager(mPager);
//        mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
//
//
//
//            @Override
//            public void onPageSelected(int position) {
//                /**Setting the Values according to the User Input
//                 * Medication Last Time
//                 * Doses in A Row
//                 * Adherence
//                 * **/
//                if (position == 1) {
//
//                    Log.d(TAGMA,"Keeping Date");
//                    if (FirstAnalyticFragment.checkMediLastTakenTime != null) {
//                        FirstAnalyticFragment.checkMediLastTakenTime
//                                .setText(SharedPreferenceStore.mPrefsStore
//                                        .getString(
//                                                "com.peacecorps.malaria.checkMediLastTakenTime",
//                                                "").toString());
//
//                        Log.d(TAGMA,"Calculating Doses");
//                        int currentDose = 0,dosesInaRow=0;
//                        if (SharedPreferenceStore.mPrefsStore.getBoolean(
//                                "com.peacecorps.malaria.isWeekly", false)) {
//                            dosesInaRow =sqLite.getDosesInaRowWeekly();
//                            SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.weeklyDose", dosesInaRow).apply();
//                            currentDose = dosesInaRow;
//                            Log.d(TAGMA, "Weekly");
//                        } else {
//                            //currentDose = SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.dailyDose", 0);
//                            dosesInaRow=sqLite.getDosesInaRowDaily();
//                            SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose",dosesInaRow).apply();
//                            currentDose=dosesInaRow;
//                            Log.d(TAGMA, "Daily");
//                        }
//                        FirstAnalyticFragment.doses.setText("" + currentDose);
//                        Log.d(TAGMA, "Doses in a Row:" + dosesInaRow);
//
//                        Log.d(TAGMA,"Calculating Adherence");
//                        long interval = checkDrugTakenTimeInterval("firstRunTime");
//                        int takenCount = sqLite.getCountTaken();
//                        double adherenceRate;
//                        Log.d(TAGMA,""+ interval);
//                        Log.d(TAGMA,""+ takenCount);
//                        if(interval!=0)
//                            adherenceRate = ((double)takenCount / (double)interval) * 100;
//                        else
//                            adherenceRate = 100;
//                        String ar=String.format("%.1f ",adherenceRate);
//                        FirstAnalyticFragment.adherence.setText("" + ar + "%");
//                    }
//
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//
//            @Override
//            public void onPageScrolled(int i, float v, int i2) {
//
//            }
//
//
//        });

}