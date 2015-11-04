package com.peacecorps.malaria;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.MainActivity;

/**
 * Created by Chimdi on 7/18/14.
 */
public class InfoHubFragmentActivity extends FragmentActivity {

    Button homeIconButton, btnPeaceCorpsPolicy, btnPercentSideEffects, btnSideEffectsPCV,
            btnSideEffectsNPCV, btnVolunteerAdherence, btnEffectiveness,btnTripIndicator,btnSettings;
    TextView ihLabel;

        private Dialog dialog = null;
    static SharedPreferenceStore mSharedPreferenceStore;

    //TextView internetIsConnected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_hub_screen);

        /*internetIsConnected = (TextView)findViewById(R.id.internetIsConnected);

        // check if you are connected or not
        if(isConnected()){
            internetIsConnected.setBackgroundColor(0xFF00CC00);
            internetIsConnected.setText("Internet Connected");
        }
        else{
            internetIsConnected.setText("You are NOT connected");
        }*/

        /**Declaring Views**/
        ihLabel= (TextView)findViewById(R.id.ih);
        homeIconButton = (Button) findViewById(R.id.homeButton);
        btnTripIndicator = (Button) findViewById(R.id.tripButton);
        btnPeaceCorpsPolicy = (Button) findViewById(R.id.btnPeaceCorpsPolicy);
        btnPercentSideEffects = (Button) findViewById(R.id.btnPercentSideEffects);
        btnSideEffectsPCV = (Button) findViewById(R.id.btnSideEffectsPCV);
        btnSideEffectsNPCV = (Button) findViewById(R.id.btnSideEffectsNPCV);
        btnVolunteerAdherence = (Button) findViewById(R.id.btnVolunteerAdherence);
        btnEffectiveness = (Button) findViewById(R.id.btnEffectiveness);
        btnSettings = (Button)findViewById(R.id.info_hub_settings_button);

        /**Setting fonts**/
        Typeface cf = Typeface.createFromAsset(getAssets(),"fonts/garreg.ttf");
        ihLabel.setTypeface(cf);

        addListeners();

    }


    public void addListeners() {

        /**Calling Each of the Posts by 6 different buttons**/
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

        btnPeaceCorpsPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), PeaceCorpsPolicyFragmentActivity.class));
            }
        });

        btnPercentSideEffects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), PercentSideEffectsFragmentActivity.class));
            }
        });

        btnSideEffectsPCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), SideEffectsPCVFragmentActivity.class));
            }
        });

        btnSideEffectsNPCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), SideEffectsNPCVFragmentActivity.class));
            }
        });

        btnVolunteerAdherence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), VolunteerAdherenceFragmentActivity.class));
            }
        });

        btnEffectiveness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), EffectivenessFragmentActivity.class));
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addDialog();
            }
        });

    }


    /*public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }*/

    public void addDialog()
    {
        /**Reset Dialog**/
        dialog = new Dialog(InfoHubFragmentActivity.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setContentView(R.layout.resetdata_dialog);
        dialog.setTitle("Reset Data");

       // final RadioGroup btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReset);
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOKReset);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           /*     // get selected radio button from radioGroup
                int selectedId = btnRadGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton btnRadButton = (RadioButton) dialog.findViewById(selectedId);

                String ch = btnRadButton.getText().toString();

                if (ch.equalsIgnoreCase("yes")) {
                    DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getApplicationContext());
                    sqLite.resetDatabase();
                    mSharedPreferenceStore.mEditor.clear().commit();
                    startActivity(new Intent(getApplication().getApplicationContext(),
                            UserMedicineSettingsFragmentActivity.class));

                } else {
                    dialog.dismiss();
                }*/
                DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getApplicationContext());
                sqLite.resetDatabase();
                mSharedPreferenceStore.mEditor.clear().commit();
                startActivity(new Intent(getApplication().getApplicationContext(),
                        UserMedicineSettingsFragmentActivity.class));
            }
        });

        Button btnCancel = (Button) dialog.findViewById(R.id.dialogButtonCancelReset);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}

