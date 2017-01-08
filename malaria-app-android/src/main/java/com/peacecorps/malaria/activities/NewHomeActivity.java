package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                final Dialog alertDialog = new Dialog(NewHomeActivity.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                alertDialog.setContentView(R.layout.game_info_dialog);
                alertDialog.setCancelable(false);

                TextView dialogTitle = (TextView) alertDialog.findViewById(R.id.gameTitle);
                TextView dialogDescription = (TextView) alertDialog.findViewById(R.id.gameInfoDescription);
                dialogTitle.setText(R.string.myth_fact_game);
                dialogDescription.setText(R.string.myth_fact_description);
                Button cancelButton = (Button) alertDialog.findViewById(R.id.noGameDialogButton);
                Button okButton = (Button) alertDialog.findViewById(R.id.continueGameDialogButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(NewHomeActivity.this, MythFactGame.class));
                        alertDialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
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
                final Dialog alertDialog = new Dialog(NewHomeActivity.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                alertDialog.setContentView(R.layout.game_info_dialog);
                alertDialog.setCancelable(false);

                TextView dialogTitle = (TextView) alertDialog.findViewById(R.id.gameTitle);
                TextView dialogDescription = (TextView) alertDialog.findViewById(R.id.gameInfoDescription);
                dialogTitle.setText(R.string.rapid_fire_game);
                dialogDescription.setText(R.string.rapid_fire_description);
                Button cancelButton = (Button) alertDialog.findViewById(R.id.noGameDialogButton);
                Button okButton = (Button) alertDialog.findViewById(R.id.continueGameDialogButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(NewHomeActivity.this, RapidFireGame.class));
                        alertDialog.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        };
    }
}
