package com.peacecorps.malaria.ui.play;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.ui.play.myth_vs_fact.MythFactActivity;
import com.peacecorps.malaria.ui.play.rapid_fire.RapidFireActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public class PlayFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        // butter knife binding
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected int getContentResource() {
        return R.layout.fragment_play;
    }

    // listener for badge button in play fragment
    @OnClick(R.id.btn_myth_vs_fact)
    public void mythVsFactListener(View view) {
        // start MythFactActivity
        startActivity(new Intent(getContext(), MythFactActivity.class));
    }

    // listener for rapid fire button in play fragment
    @OnClick(R.id.btn_rapid_fire)
    public void rapidFireListener(View view) {
        // start Rapid fire activity
        startActivity(new Intent(getContext(), RapidFireActivity.class));
    }

    @Override
    protected void init() {

    }

    // setting null values to the listener, presenter
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
