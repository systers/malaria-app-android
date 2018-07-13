package com.peacecorps.malaria.ui.info_hub;

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

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public class InfoHubFragment extends BaseFragment implements InfoHubContract.InfoHubMvpView, View.OnClickListener {

    private Context context;


    // returns a new instance of the Fragment
    public static InfoHubFragment newInstance() {
        return new InfoHubFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.info_hub_screen, container, false);
        context = getContext();
        // butterknife binding
        ButterKnife.bind(this, view);
        // instantiating presenter, passing datamanger, view & attaching view to the presenter
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    protected int getContentResource() {
        return R.layout.info_hub_screen;
    }

    @Override
    protected void init() {
        // no need of it init() now, necessary to override
    }

    // still needs to be worked upon
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_peace_corps_policy:
                break;
            case R.id.btn_percent_side_effects:
                break;
            case R.id.btn_side_effects_pcv:
                break;
            case R.id.btn_side_effects_non_pcv:
                break;
            case R.id.btn_volunteer_adherence:
                break;
            case R.id.btn_effectiveness:
                break;
            default:
                ToastLogUtil.showToast(context, "Wrong button click");
        }
    }
}
