package com.peacecorps.malaria.ui.home_screen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.notifications.service.AlarmService;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.utils.InjectionClass;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anamika Tripathi on 5/8/18.
 */
public class HomeScreenFragment extends BaseFragment implements HomeContract.HomeMvpView {


    private Context context;
    private HomePresenter<HomeScreenFragment> presenter;
    @BindView(R.id.btn_accept_medication)
    Button mAcceptMedicationButton;
    @BindView(R.id.btn_reject_medication)
    Button mRejectMedicationButton;
    @BindView(R.id.tv_current_date)
    TextView mCurrentDateLabel;
    @BindView(R.id.tv_day_of_week)
    TextView mCurrentDayOfweekLabel;
    @BindView(R.id.warningView)
    TextView warningView;
    @BindView(R.id.warningButton)
    Button warningButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        context = getContext();
        ButterKnife.bind(this, view);
        presenter = new HomePresenter<>(InjectionClass.provideDataManager(context), context);
        presenter.attachView(this);
        return view;
    }

    @Override
    protected int getContentResource() {
        return R.layout.fragment_home_screen;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    // get drug accepted + rejected count from preferences
    @Override
    protected void init() {
        // setting font for day & date labels
        setFont();
        // checking current date and day
        presenter.checkDayDateOfWeek();
        // checking visibility of warning text using preferences
        presenter.checkWarningVisibility();
        // decide drug taken for UI
        presenter.decideDrugTakenForUI();
    }

    // set current date and day
    @Override
    public void setCurrentDayAndDate(String date, String day) {
        mCurrentDateLabel.setText(date);
        mCurrentDayOfweekLabel.setText(day);
    }

    // make warning text visible
    @Override
    public void setWarningText() {
        warningView.setVisibility(View.VISIBLE);
        warningButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void isDrugTakenUI() {
        mCurrentDateLabel.setTextColor(Color.rgb(89, 43, 21));
        mCurrentDayOfweekLabel.setTextColor(Color.rgb(89, 43, 21));
        mAcceptMedicationButton
                .setBackgroundResource(R.drawable.accept_medi_checked_);
        mRejectMedicationButton
                .setBackgroundResource(R.drawable.reject_medi_grayscale);
        setButtonState(false);
        presenter.storeMediTimeLastChecked();
    }

    @Override
    public void isDrugNotTakenUI() {
        mAcceptMedicationButton
                .setBackgroundResource(R.drawable.accept_medi_grayscale);
        mRejectMedicationButton
                .setBackgroundResource(R.drawable.reject_medi_checked);
        setButtonState(false);
    }

    @Override
    public void newDayUI() {
        mAcceptMedicationButton
                .setBackgroundResource(R.drawable.accept_medi_checked_);
        mRejectMedicationButton
                .setBackgroundResource(R.drawable.reject_medi_checked);
        setButtonState(true);
    }

    @Override
    public void missedWeekUI() {
        mCurrentDateLabel.setTextColor(Color.RED);
        mCurrentDayOfweekLabel.setTextColor(Color.RED);
    }

    @Override
    public void startAlarmServiceClass() {
        if (getActivity() != null) {
            getActivity().startService(
                    new Intent(getActivity(), AlarmService.class));
        }
    }

    public void setButtonState(boolean state) {
        mAcceptMedicationButton.setEnabled(state);
        mRejectMedicationButton.setEnabled(state);
    }

    // setting fonts from assets file
    private void setFont() {
        if (getActivity() != null) {
            Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/garreg.ttf");
            mCurrentDayOfweekLabel.setTypeface(custom_font);
            mCurrentDateLabel.setTypeface(custom_font);
        }
    }

    @OnClick(R.id.fragment_home_screen_set_tone_button)
    public void reminderButtonListener() {
        Intent myIntent = new Intent(getActivity(), ReminderToneActivity.class);
        startActivity(myIntent);
    }

    @OnClick(R.id.btn_accept_medication)
    public void acceptButtonListener() {
        // create media player for sound
        MediaPlayer.create(getActivity(), R.raw.accept_button_sound)
                .start();
        // increase drug accepted count by 1
        presenter.increaseDrugAcceptedCount();

        presenter.checkDrugIntervalFirstRunTime(true);
    }

    @OnClick(R.id.btn_reject_medication)
    public void rejectButtonListener() {

        MediaPlayer.create(getActivity(), R.raw.reject_button_sound)
                .start();
        presenter.increaseDrugRejectCount();

        presenter.checkDrugIntervalFirstRunTime(false);
    }

//    @OnClick(R.id.fragment_home_screen_settings_button)
//    public void settingListener() {
//        DrugNotificationUtils.startNotificationCheckSnooze(context);
//    }
}
