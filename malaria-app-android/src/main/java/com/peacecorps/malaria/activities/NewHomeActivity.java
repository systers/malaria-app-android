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
                if (sharedPreferences.getBoolean(getString(R.string.shared_prefs_myth_fact_game), true))
                    showHelpDialog(getResources().getInteger(R.integer.MythFactGame));
                else startActivity(new Intent(NewHomeActivity.this, MythFactGame.class));
            }
        };
    }

    private View.OnClickListener rapidFireButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean(getString(R.string.shared_prefs_rapid_fire_game), true))
                    showHelpDialog(getResources().getInteger(R.integer.RapidFireGame));
                else startActivity(new Intent(NewHomeActivity.this, RapidFireGame.class));
            }
        };
    }

    /**
     * This method is used to show the help dialog for a game.
     * To reduce code redundancy, the different alerts for different games were combined,
     * and can be used with the below parameter.
     * @param gameID Enter the gameID (R.integer.MythFactGame for MythFactGame and R.integer.RapidFireGame for RapidFireGame) to show the dialog.
     */
    public void showHelpDialog(final int gameID) {

        String strHowToPlay = "", strGameInfo = "";
        if (gameID == getResources().getInteger(R.integer.RapidFireGame)) {
            strHowToPlay = getString(R.string.help_rapid_fire_how_to_play);
            strGameInfo = getString(R.string.help_rapid_fire_info);
        } else if (gameID == getResources().getInteger(R.integer.MythFactGame)) {
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
                    if (gameID == getResources().getInteger(R.integer.RapidFireGame))
                        editor.putBoolean(getString(R.string.shared_prefs_rapid_fire_game), true);
                    else if (gameID == getResources().getInteger(R.integer.MythFactGame))
                        editor.putBoolean(getString(R.string.shared_prefs_myth_fact_game), true);
                } else {
                    if (gameID == getResources().getInteger(R.integer.RapidFireGame))
                        editor.putBoolean(getString(R.string.shared_prefs_rapid_fire_game), false);
                    else if (gameID == getResources().getInteger(R.integer.MythFactGame))
                        editor.putBoolean(getString(R.string.shared_prefs_myth_fact_game), false);
                }
                editor.apply();
                if (gameID == getResources().getInteger(R.integer.RapidFireGame))
                    startActivity(new Intent(NewHomeActivity.this, RapidFireGame.class));
                else if (gameID == getResources().getInteger(R.integer.MythFactGame))
                    startActivity(new Intent(NewHomeActivity.this, MythFactGame.class));
                helpDialog.dismiss();
            }
        });
        helpDialog.show();
    }
}
