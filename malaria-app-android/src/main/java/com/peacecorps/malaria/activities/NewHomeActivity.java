package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.utils.TouchFeedBack;

/**
 * Created by yatna on 14/6/16.
 */
public class NewHomeActivity extends Activity{
    private Button badgeScreenButton;
    private Button mythFactButton;
    private Button rapidFireButton;
    private Button medicineStoreButton;
    private Button homeIconButton;
    private Button btnTripIndicator;
    private Button infoHub;
    private Button userProfile;
    private Button tempButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_home_activity);
        badgeScreenButton=(Button)findViewById(R.id.badgeScreen);
        rapidFireButton=(Button)findViewById(R.id.rapidFire);
        mythFactButton=(Button)findViewById(R.id.mythFact);
        medicineStoreButton=(Button)findViewById(R.id.medicineStore);
        //footer buttons
        homeIconButton = (Button) findViewById(R.id.homeButton);
        btnTripIndicator = (Button) findViewById(R.id.tripButton);
        infoHub = (Button) findViewById(R.id.infoButton);
        userProfile =(Button)findViewById(R.id.userProfile);
        tempButton = (Button)findViewById(R.id.tempButton);
        homeIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        homeIconButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
        btnTripIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), TripIndicatorFragmentActivity.class));
                finish();
            }
        });
        btnTripIndicator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
        infoHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), InfoHubFragmentActivity.class));
                finish();
            }
        });
        infoHub.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                finish();
            }
        });
        userProfile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
        tempButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
        //footer ends

        badgeScreenButton.setOnClickListener(badgeScreenOnClickListener());
        badgeScreenButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
        mythFactButton.setOnClickListener(mythFactGameOnClickListener());
        mythFactButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
        rapidFireButton.setOnClickListener(rapidFireButtonOnClickListener());
        rapidFireButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
        medicineStoreButton.setOnClickListener(medicineStoreOnClickListener());
        medicineStoreButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TouchFeedBack.touchFeedBack(v, event);
                return false;
            }
        });
    }

    private View.OnClickListener medicineStoreOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewHomeActivity.this,MedicineStore.class));
            }
        };
    }

    private View.OnClickListener badgeScreenOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewHomeActivity.this, BadgeRoom.class));
            }
        };
    }

    private View.OnClickListener mythFactGameOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewHomeActivity.this, MythFactGame.class));
            }
        };
    }

    private View.OnClickListener rapidFireButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewHomeActivity.this, RapidFireGame.class));
            }
        };
    }
}
