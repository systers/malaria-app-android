package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_home_activity);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
                if (sharedPreferences.getBoolean("showHelpMythFactGame", true))
                    showHelpDialog(2);
                else startActivity(new Intent(NewHomeActivity.this, MythFactGame.class));
            }
        };
    }

    private View.OnClickListener rapidFireButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("showHelpRapidFireGame", true))
                    showHelpDialog(1);
                else startActivity(new Intent(NewHomeActivity.this, RapidFireGame.class));
            }
        };
    }

    /**
     * This method is used to show the help dialog for a game.
     * To reduce code redundancy, the different alerts for different games were combined,
     * and can be used with the below parameter.
     * @param gameID Enter the gameID (1 for RapidFire, 2 for MythFact) to show the dialog
     */
    public void showHelpDialog(final int gameID) {

        String strHowToPlay = "", strGameInfo = "";
        if (gameID == 1) {
            strHowToPlay = getString(R.string.help_rapid_fire_how_to_play);
            strGameInfo = getString(R.string.help_rapid_fire_info);
        } else if (gameID == 2) {
            strHowToPlay = getString(R.string.help_myth_fact_how_to_play);
            strGameInfo = getString(R.string.help_myth_fact_info);
        }

        final Dialog helpDialog = new Dialog(NewHomeActivity.this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        helpDialog.setContentView(R.layout.game_help_dialog);
        
        TextView howToPlay = (TextView) helpDialog.findViewById(R.id.helpGameDialogHowToPlay);
        TextView gameInfo = (TextView) helpDialog.findViewById(R.id.helpGameDialogInfo);
        final CheckBox showNextTime = (CheckBox) helpDialog.findViewById(R.id.helpGameDialogShowNextTime);
        Button start = (Button) helpDialog.findViewById(R.id.helpGameDialogButtonStart);

        howToPlay.setText(strHowToPlay);
        gameInfo.setText(strGameInfo);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (showNextTime.isChecked()) {
                    if (gameID == 1)
                        editor.putBoolean("showHelpRapidFireGame", true);
                    else if (gameID == 2)
                        editor.putBoolean("showHelpMythFactGame", true);
                } else {
                    if (gameID == 1)
                        editor.putBoolean("showHelpRapidFireGame", false);
                    else if (gameID == 2)
                        editor.putBoolean("showHelpMythFactGame", false);
                }
                editor.commit();
                if (gameID == 1)
                    startActivity(new Intent(NewHomeActivity.this, RapidFireGame.class));
                else if (gameID == 2)
                    startActivity(new Intent(NewHomeActivity.this, MythFactGame.class));
                helpDialog.dismiss();
            }
        });
        helpDialog.show();
    }
}
