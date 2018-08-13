package com.peacecorps.malaria.ui.play.rapid_fire;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseActivity;

/**
 * Created by Anamika Tripathi on 13/8/18.
 */
public class RapidFireActivity  extends BaseActivity implements RapidFireFragment.OnRapidFragmentListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapid_fire);
        init();
    }

    @Override
    protected void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Rapid Fire");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Fragment fragment = new RapidFireFragment();
        loadFragment(fragment);
    }

    /**
     * @param fragment : replaces frame layout with parameter received in main activity
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rapid_fire_container, fragment, "");
        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // close activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void goBackToPlayFragment() {
        finish();
    }
}
