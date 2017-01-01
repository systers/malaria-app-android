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

    public void mythDescription(View view){
        final Dialog alertDialog = new Dialog(NewHomeActivity.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        alertDialog.setContentView(R.layout.exit_game_dialog);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        alertDialog.setTitle("Game Over!!");
        TextView head=(TextView)alertDialog.findViewById(R.id.dialogHead);
        TextView description=(TextView)alertDialog.findViewById(R.id.dialogDescription);
        Button ok=(Button)alertDialog.findViewById(R.id.exitGameDialogButtonOK);
        Button cancel=(Button)alertDialog.findViewById(R.id.exitGameDialogButtonCancel);

        head.setText("Myth vs Fact description");
        description.setText("This game makes you decipher whether or not a given statement is true, or if it is a myth. You will be rewarded with points for each correct answer.");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewHomeActivity.this, MythFactGame.class));
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void RFDescription(View view){
        final Dialog alertDialog = new Dialog(NewHomeActivity.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        alertDialog.setContentView(R.layout.exit_game_dialog);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        alertDialog.setTitle("Game Over!!");
        TextView head=(TextView)alertDialog.findViewById(R.id.dialogHead);
        TextView description=(TextView)alertDialog.findViewById(R.id.dialogDescription);
        Button ok=(Button)alertDialog.findViewById(R.id.exitGameDialogButtonOK);
        Button cancel=(Button)alertDialog.findViewById(R.id.exitGameDialogButtonCancel);

        head.setText("Rapid Fire description");
        description.setText("This is a multiple choice game that tests your vast knowledge of malaria.");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewHomeActivity.this, RapidFireGame.class));
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
