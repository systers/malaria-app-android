package com.peacecorps.malaria.ui.play;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.utils.ToastLogUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public class PlayFragment extends BaseFragment {

    private OnPlayFragmentListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        // butterknife binding
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected int getContentResource() {
        return R.layout.fragment_play;
    }

    // listener for badge button in play fragment
    @OnClick(R.id.btn_badge_screen)
    public void badgeScreenListener(View view) {
        //Todo implement it in next PR
        ToastLogUtil.showToast(getContext(), "badge screen clicked");
    }

    // listener for badge button in play fragment
    @OnClick(R.id.btn_myth_vs_fact)
    public void mythVsFactListener(View view) {
        // calls interface method which is implemented by mainActivity - replaces play fragment with MythVsFactFragment
        listener.replacePlayFragment(R.id.btn_myth_vs_fact);
    }

    // listener for rapid fire button in play fragment
    @OnClick(R.id.btn_rapid_fire)
    public void rapidFireListener(View view) {
        // replaces play fragment with RapidFireFragment
        listener.replacePlayFragment(R.id.btn_rapid_fire);
    }

    @OnClick(R.id.btn_medicine_store)
    public void medicineStoreListener(View view) {
        // replaces play fragment with MedicineStore Fragment
        listener.replacePlayFragment(R.id.btn_medicine_store);
    }

    @Override
    protected void init() {

    }

    // it needs to be implemented by main activity
    public interface OnPlayFragmentListener {
        void replacePlayFragment(int id);
    }

    // Container Activity must implement this interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof OnPlayFragmentListener) {
            listener = (OnPlayFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayFragmentListener");
        }
    }

    // setting null values to the listener, presenter
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
