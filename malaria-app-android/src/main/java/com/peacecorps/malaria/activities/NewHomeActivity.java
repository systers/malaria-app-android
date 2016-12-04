package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.peacecorps.malaria.R;

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
        homeIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        btnTripIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), TripIndicatorFragmentActivity.class));
                finish();
            }
        });
        infoHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), InfoHubFragmentActivity.class));
                finish();
            }
        });
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                finish();
            }
        });
        //footer ends

        badgeScreenButton.setOnClickListener(badgeScreenOnClickListener());
        mythFactButton.setOnClickListener(mythFactGameOnClickListener());
        rapidFireButton.setOnClickListener(rapidFireButtonOnClickListener());
        medicineStoreButton.setOnClickListener(medicineStoreOnClickListener());
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
                AlertDialog alertDialog = new AlertDialog.Builder(NewHomeActivity.this).create();
                alertDialog.setTitle("Myth Fact Game");
                alertDialog.setMessage("Test your knowledge of Malaria! Drag the facts into the trash if they are a myth, or into the treasure chest if they are a fact. You get one point for each correct answer.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Play!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(NewHomeActivity.this, MythFactGame.class));
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                        }
                        });
                alertDialog.show();


            }
        };
    }

    private View.OnClickListener rapidFireButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(NewHomeActivity.this).create();
                alertDialog.setTitle("Rapid Fire Game");
                alertDialog.setMessage("Test your knowledge of Malaria! You have 5 seconds to choose the correct answer to fill in the blank or complete the sentence about malaria. You get one point for each correct answer.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Play!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(NewHomeActivity.this, RapidFireGame.class));
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        };
    }
}
