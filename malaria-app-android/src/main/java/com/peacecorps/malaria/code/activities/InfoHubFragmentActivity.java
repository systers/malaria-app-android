package com.peacecorps.malaria.code.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peacecorps.malaria.R;

/**
 * Created by Chimdi on 7/18/14.
 */
public class InfoHubFragmentActivity extends FragmentActivity {

    Button btnPeaceCorpsPolicy, btnPercentSideEffects, btnSideEffectsPCV,
            btnSideEffectsNPCV, btnVolunteerAdherence, btnEffectiveness;
    TextView ihLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_hub_screen);

        btnPeaceCorpsPolicy = (Button) findViewById(R.id.btn_peace_corps_policy);
        btnPercentSideEffects = (Button) findViewById(R.id.btn_percent_side_effects);
        btnSideEffectsPCV = (Button) findViewById(R.id.btn_side_effects_pcv);
        btnSideEffectsNPCV = (Button) findViewById(R.id.btn_side_effects_non_pcv);
        btnVolunteerAdherence = (Button) findViewById(R.id.btn_volunteer_adherence);
        btnEffectiveness = (Button) findViewById(R.id.btn_effectiveness);


        /**Setting fonts**/
        Typeface cf = Typeface.createFromAsset(getAssets(), "fonts/garreg.ttf");
        ihLabel.setTypeface(cf);

        addListeners();

    }

    public void addListeners() {

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
    }
}
