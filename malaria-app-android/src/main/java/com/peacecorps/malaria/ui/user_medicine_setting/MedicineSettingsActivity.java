package com.peacecorps.malaria.ui.user_medicine_setting;

/*
 * Created by Anamika on 12/7/2018.
 */

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.home_screen.MainActivity;
import com.peacecorps.malaria.code.adapter.DrugArrayAdapter;
import com.peacecorps.malaria.ui.base.BaseActivity;
import com.peacecorps.malaria.utils.Constants;
import com.peacecorps.malaria.utils.InjectionClass;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MedicineSettingsActivity extends BaseActivity
        implements AdapterView.OnItemSelectedListener, MedicineSettingContract.View {

    @BindView(R.id.setup_label)
    TextView mSetupLabel;
    @BindView(R.id.drug_take_label)
    TextView mDrugTakeLabel;
    @BindView(R.id.time_pick_label)
    TextView mTimePickLabel;
    @BindView(R.id.if_forget_label)
    TextView mIfForgetLabel;

    // need to make it static as TimePickerFragment class is static & it's using both buttons
    private static Button mDoneButton;
    private static TextView timePickButton;

    @BindView(R.id.drug_select_spinner)
    Spinner mDrugSelectSpinner;

    private static int drugPickedNo;
    private static int mHour;
    private static int mMinute;

    private MedicineSettingPresenter<MedicineSettingsActivity> presenter;

    /*User Medicine Settings Fragment Activity is for the Setup Screen of the Malaria App*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_user_medicine_settings);

        // set up title of activity
        this.setTitle(R.string.user_medicine_settings_fragment_activity_title);
        ButterKnife.bind(this);

        // setting up presenter and views
        presenter = new MedicineSettingPresenter<>(InjectionClass.provideDataManager(this), this);
        presenter.attachView(this);
        init();
    }

    /*OnClicking the done button what all 'll happen?
     * All the user settings will be saved.
     * Start the Main Activity which shows the Home Screen
     */
    @OnClick(R.id.done_button)
    public void doneButtonListener(View view) {
        if(mDoneButton.isEnabled()) {
            // saving alarm timings, setting user preference to true and saving drug picked in preferences
            presenter.setUserAndMedicationPreference(mHour, mMinute, drugPickedNo);

            startMainActivity();
        }
        else {
            Toast.makeText(MedicineSettingsActivity.this, "Select time first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void init() {
        // setting different fonts on labels & buttons
        Typeface cb = Typeface.createFromAsset(getAssets(), "fonts/garbold.ttf");
        mSetupLabel.setTypeface(cb);

        mDoneButton = findViewById(R.id.done_button);
        timePickButton = findViewById(R.id.time_pick_button);

        Typeface cf = Typeface.createFromAsset(getAssets(), "fonts/garreg.ttf");
        timePickButton.setTypeface(cf);
        mIfForgetLabel.setTypeface(cb);
        mTimePickLabel.setTypeface(cb);
        mDrugTakeLabel.setTypeface(cb);

        // check if user has set preferences already
        presenter.checkInitialAppInstall();

        // set up spinner for selecting medicine
        createDrugSelectionSpinner();
    }

    /*Method is for working with the Spinner to make selection of drugs work
     *It allows selection between three of the drugs-
     * Malarone- Daily
     * Doxycycline- Daily
     * Melofquine- Weekly
     */
    private void createDrugSelectionSpinner() {

        DrugArrayAdapter adapter = new DrugArrayAdapter(this, getResources().getStringArray(R.array.array_drugs),
                Constants.imageID, getResources().getStringArray(R.array.array_medicine_description));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDrugSelectSpinner.setAdapter(adapter);
        mDrugSelectSpinner.setOnItemSelectedListener(this);
    }

    /*Method is for picking time for the Alarm Notifications*/
    @OnClick(R.id.time_pick_button)
    public void timePickButtonListener(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "User Medicine Setting Activity");
    }

    @Override
    public void startMainActivity() {
        startActivity(new Intent(MedicineSettingsActivity.this,
                MainActivity.class));
        finish();
    }


    public static void setSelectedTime(String time) {
        timePickButton.setText(time);
    }

    /*Method to enable the done Button
     *Done button is enabled if the user have setup a time
     */
    public static void enableDoneButton() {
        mDoneButton.setEnabled(true);
    }

    /*Overrided Method called by the create Drug Selection Spinner to check which drug was chosen */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        drugPickedNo = position;
        parent.setSelection(parent.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /*Class to manage the Time Picker Widget*/

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog view = new TimePickerDialog(getActivity(), R.style.MyTimePicker, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

            View v = getActivity().getLayoutInflater().inflate(R.layout.time_picker_style_setting, null);

            view.setView(v);
            return view;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {

            mHour = hourOfDay;
            mMinute = minutes;
            //updateTime(hourOfDay, minutes);
            //updateTime(mHour, mMinute);
            convertToTwelveHours(hourOfDay, minutes);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }

    public static void convertToTwelveHours(int hours, int mins) {
        String timeSet;
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes;
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append the time to a stringBuilder
        String theTime = String.valueOf(hours) + ':' + minutes + " " + timeSet;
        setSelectedTime(theTime);
        enableDoneButton();
    }

}