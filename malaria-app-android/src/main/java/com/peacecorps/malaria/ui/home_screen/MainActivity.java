package com.peacecorps.malaria.ui.home_screen;


import android.app.Dialog;
import android.os.Bundle;

import com.peacecorps.malaria.R;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.peacecorps.malaria.code.fragment.HomeScreenFragment;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BaseActivity;
import com.peacecorps.malaria.ui.home_screen.HomeContract.IHomeView;
import com.peacecorps.malaria.ui.play.PlayFragment.OnPlayFragmentListener;
import com.peacecorps.malaria.ui.play.PlayFragment;
import com.peacecorps.malaria.ui.play.medicine_store.MedicineStoreFragment;
import com.peacecorps.malaria.ui.play.myth_vs_fact.MythFactFragment;
import com.peacecorps.malaria.ui.play.rapid_fire.RapidFireFragment;
import com.peacecorps.malaria.ui.play.rapid_fire.RapidFireFragment.OnRapidFragmentListener;
import com.peacecorps.malaria.ui.user_profile.UserProfileFragment;
import com.peacecorps.malaria.ui.user_profile.UserProfileFragment.OnUserFragmentListener;
import com.peacecorps.malaria.utils.BottomNavigationViewHelper;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogUtil;

import static com.peacecorps.malaria.ui.play.myth_vs_fact.MythFactFragment.*;

public class MainActivity extends BaseActivity implements IHomeView, OnUserFragmentListener,
                                            OnPlayFragmentListener, OnMythFragmentListener, OnRapidFragmentListener {

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        }

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
        Fragment fragment = new HomeScreenFragment();
        loadFragment(fragment);

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
                        fragment = new PlayFragment();
                        loadFragment(fragment);
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


    // reset button to create a dialog & if clicked okay, delete all data & start User medicine settings activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_menu_reset) {
            final Dialog dialog;
            dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
            dialog.setContentView(R.layout.resetdata_dialog);
            dialog.setTitle("Reset Data");

            Button btnOK = dialog.findViewById(R.id.btn_dialog_reset_okay);

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //Todo   reset database here
                }
            });

            Button btnCancel = dialog.findViewById(R.id.btn_dialog_reset_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void startHomeFragment() {
        bottomNavigationView.setSelectedItemId(R.id.home_screen_fragment);
        loadFragment(new HomeScreenFragment());
    }

    @Override
    public void replacePlayFragment(int id) {
        switch (id) {
            case R.id.btn_badge_screen: break;
            case R.id.btn_myth_vs_fact:loadFragment(new MythFactFragment());
                                            break;
            case R.id.btn_medicine_store:loadFragment(new MedicineStoreFragment());
                                            break;

            case R.id.btn_rapid_fire:loadFragment(new RapidFireFragment());
                                            break;
            default:
                ToastLogUtil.showToast(this, "Wrong button ");
        }
    }

    @Override
    public void goBackToPlayFragment() {
        loadFragment(new PlayFragment());
    }
}
