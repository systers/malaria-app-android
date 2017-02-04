package com.peacecorps.malaria.activities;

/**
 * Created by Chimdi on 6/2/2014.
 * Edited by Ankita
 */

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.adapter.DrugArrayAdapter;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.services.AlarmService;

import java.util.Calendar;
import java.util.Date;


public class UserMedicineSettingsFragmentActivity extends FragmentActivity
        implements AdapterView.OnItemSelectedListener {

    private static Button mDoneButton;

    private static TextView timePickButton;
    private TextView mSetupLabel;
    private TextView mDrugTakeLabel;
    private TextView mTimePickLabel;
    private TextView mIfForgetLabel;
    private Spinner mDrugSelectSpinner;
    private static boolean isTimeSet = false;
    private static String mDrugPicked;
    private static int mHour;
    private static int mMinute;
    private final static Calendar mCalendar = Calendar.getInstance();
    private String TAGUMSFA = "UserMedicineSettingsFragmentActivity";
    static SharedPreferenceStore mSharedPreferenceStore;
    private static View v;
    private static TimePicker tp;

    public static Context mFragmentContext;

    /*User Medicine Settings Fragment Activity is for the Setup Screen of the Malaria App*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_user_medicine_settings);
        this.setTitle(R.string.user_medicine_settings_fragment_activity_title);

        mSharedPreferenceStore = new SharedPreferenceStore();

        mFragmentContext = UserMedicineSettingsFragmentActivity.this
                .getApplicationContext();
        mDoneButton = (Button) findViewById(R.id.user_medicine_settings_activity_done_button);
        mDoneButton.setOnClickListener(mDoneButtonClickListener);

        boolean isDoneButtonChecked = false;

        timePickButton = (TextView) findViewById(R.id.user_medicine_settings_activity_time_pick_button);
        mDrugTakeLabel = (TextView) findViewById(R.id.user_medicine_settings_activity_drug_take_label);
        mSetupLabel = (TextView) findViewById(R.id.user_medicine_settings_activity_setup_label);
        mTimePickLabel = (TextView) findViewById(R.id.user_medicine_settings_activity_time_pick_label);
        mIfForgetLabel = (TextView) findViewById(R.id.user_medicine_settings_activity_if_forget_label);
        mDrugSelectSpinner = (Spinner) findViewById(R.id.user_medicine_settings_activity_drug_select_spinner);


        mSharedPreferenceStore.getSharedPreferences(this);

        Typeface cb = Typeface.createFromAsset(getAssets(), "fonts/garbold.ttf");
        mSetupLabel.setTypeface(cb);

        Typeface cf = Typeface.createFromAsset(getAssets(), "fonts/garreg.ttf");
        timePickButton.setTypeface(cf);
        mIfForgetLabel.setTypeface(cb);
        mTimePickLabel.setTypeface(cb);
        mDrugTakeLabel.setTypeface(cb);

        checkInitialAppInstall();

        createDrugSelectionSpinner();

        checkIfTimeSet(isDoneButtonChecked);

        addTimePickButtonClickListener();

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (tp != null) {
            savedInstanceState.putInt("hour", tp.getCurrentHour());
            savedInstanceState.putInt("minute", tp.getCurrentMinute());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (isTimeSet) {
            updateTime(savedInstanceState.getInt("hour"), savedInstanceState.getInt("minute"));
        }
    }

    /*Method for checking the Initial Application Install.
             *It checks two parameters:-
             * hasUserSetPreference----> BOOLEAN ----> whether user had done the settings in Initial Setup Screen or not.
             * isFirstRun--------------> BOOLEAN ----> whether this is the first Time App is run or not.
             */
    private void checkInitialAppInstall() {


        if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                "com.peacecorps.malaria.hasUserSetPreference", false)) {

            mSharedPreferenceStore.mEditor.putBoolean(
                    "com.peacecorps.malaria.hasUserSetPreference", true).commit();
            mSharedPreferenceStore.mEditor
                    .putBoolean("com.peacecorps.malaria.isFirstRun", true).commit();

            startActivity(new Intent(UserMedicineSettingsFragmentActivity.this,
                    MainActivity.class));
            finish();
        }

    }

    /*Method is for working with the Spinner to make selection of drugs work
     *It allows selection between three of the drugs-
     * Malarone- Daily
     * Doxycycline- Daily
     * Melofquine- Weekly
     */
    private void createDrugSelectionSpinner() {

       /* ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.user_medicine_settings_activity_drug_array,
                android.R.layout.simple_spinner_item);*/
        String[] listContent = {"Malarone", "Doxycycline", "Mefloquine"};
        Integer[] imageID = {R.drawable.mal, R.drawable.doxy, R.drawable.mef};
        String[] descriptions = {
                getString(R.string.mal_description),
                getString(R.string.doxy_description),
                getString(R.string.mef_description),
        };
        DrugArrayAdapter adapter = new DrugArrayAdapter(this, listContent, imageID, descriptions);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDrugSelectSpinner.setAdapter(adapter);
        mDrugSelectSpinner.setOnItemSelectedListener(this);
    }

    /*Method is for picking time for the Alarm Notifications
     *If it's missed the current time will be saved as the alarm time. ==>NOT IMPLEMENTED
     */
    public void addTimePickButtonClickListener() {

        timePickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "Time Picker");
            }

        });
    }

    /*Class to manage the Time Picker Widget*/

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = mCalendar.get(Calendar.MINUTE);
            int month = mCalendar.get(Calendar.MONTH);
            Log.d("Month", Integer.toString(month));

            TimePickerDialog view = new TimePickerDialog(getActivity(), R.style.MyTimePicker, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

            LayoutInflater inflater = getActivity().getLayoutInflater();
            v = inflater.inflate(R.layout.time_picker_style_setting, null);

            view.setView(v);
            tp = (TimePicker) v.findViewById(R.id.tpUser);

            return view;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {

            boolean isDoneButtonChecked = true;

            mHour = hourOfDay;
            mMinute = minutes;

            mHour = tp.getCurrentHour();
            mMinute = tp.getCurrentMinute();

            //updateTime(hourOfDay, minutes);
            updateTime(mHour, mMinute);
            checkIfTimeSet(isDoneButtonChecked);

        }

    }

    /*Method to enable the done Button
     *Done button is enabled if the user have setup a time
     */

    public static void checkIfTimeSet(boolean isDoneButtonChecked) {
        mDoneButton.setEnabled(isDoneButtonChecked);
        if (isDoneButtonChecked){
            mDoneButton.setText(mFragmentContext.getString(R.string.user_medicine_settings_activity_done_button));
            mDoneButton.setBackgroundColor(mFragmentContext.getResources().getColor(R.color.golden_brown));
        }
    }

    /*Method for saving all the settings of the User\
      What all it saves? Let's see-
      AlarmHour-------->INTEGER---->The Hour at which Notification will pop-up in Mobile Screen
      AlarmMinute------>INTEGER---->The Minute at which Notification will pop-up in Mobile Screen
      dayTakingDrug---->INTEGER---->The day at which drug was taken
      drugPicked------->STRING----->Which drug was picked up out of three.
      isDrugTaken------>BOOLEAN---->Whether the drug was taken or not.
      After setting related parameters it calls the Alarm Service to make notifications!
     */

    public static void saveUserTimeAndMedicationPrefs() {

        int checkDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);

        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AlarmHour", mHour)
                .commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AlarmMinute", mMinute)
                .commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.SetupMonth", month)
                .commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.SetupYear", year)
                .commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dayTakingDrug", checkDay);
        mSharedPreferenceStore.mEditor.putString("com.peacecorps.malaria.drugPicked",
                mDrugPicked);
        mSharedPreferenceStore.mEditor.putBoolean("com.peacecorps.malaria.isDrugTaken", false)
                .commit();
        mSharedPreferenceStore.mEditor.commit();

        mSharedPreferenceStore.mEditor.putBoolean(
                "com.peacecorps.malaria.hasUserSetPreference", true).commit();

        mFragmentContext.startService(new Intent(mFragmentContext,
                AlarmService.class));

        int check = mSharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.SetupYear", -1);

        Log.d("check year", Integer.toString(check));

    }

    // converts 24hr format to 12hr format with AM/PM values
    /*Method is used for setting the selected time in the text field of Spinner.
     */
    private static void updateTime(int hours, int mins) {
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

        // Set the timePickButton as the converted time
        timePickButton.setText(theTime);
        isTimeSet = true;

    }

    // Listeners
    /*OnClicking the done button what all 'll happen?
     * All the user settings will be saved.
     * Start the Main Activity which shows the Home Screen
     */
    private View.OnClickListener mDoneButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            saveUserTimeAndMedicationPrefs();
            //yatna
            createVariables();

            startActivity(new Intent(UserMedicineSettingsFragmentActivity.this,
                    MainActivity.class));
            finish();

        }
    };


    /*Overrided Method called by the create Drug Selection Spinner to check which drug was chosen
     *What all it sets?
     * drug--------------->INTEGER------------>0-Malarone,1-Doxycycline,2-Melfoquine
     * isWeekly----------->BOOLEAN------------>Tells whether the drug chosen was weekly or not
     * weeklyDate--------->LONG--------------->App registers the weekly date of Melfoquine, now this will be reminded weekly.
     * firstRunTime------->LONG--------------->First time the drug was taken.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {

        mDrugPicked = parent.getItemAtPosition(position).toString();

        parent.setSelection(parent.getSelectedItemPosition());
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drug", position).commit();
        if (position == 2) {
            mSharedPreferenceStore.mEditor.putBoolean("com.peacecorps.malaria.isWeekly", true);
            mSharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.weeklyDate",
                    new Date().getTime()).commit();
        } else {
            mSharedPreferenceStore.mEditor.putBoolean("com.peacecorps.malaria.isWeekly", false);
        }
        if (mSharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isFirstRun", true)) {
            long firstRunTime = new Date().getTime();
            mSharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.firstRunTime", firstRunTime);
            Log.d(TAGUMSFA, "First Run Time:" + mSharedPreferenceStore.mPrefsStore.getLong(
                    "com.peacecorps.malaria.firstRunTime", 0));
        }
        mSharedPreferenceStore.mEditor.putBoolean("com.peacecorps.malaria.isFirstRun", false);

    }

    //yatna
    public void createVariables() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UserMedicineSettingsFragmentActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //stores the score scored by taking medicines on time
        editor.putInt("userScore", 0);
        //stores the score earned by playing the games
        editor.putInt("gameScore", 0);
        //stores number of medicines left in store
        editor.putInt("medicineStore", 0);
        //store the limit set by user for medicine purchase reminder
        editor.putInt("alertTime", -1);
        //for user profile details
        editor.putString("user_name", "");
        editor.putString("user_email", "");
        editor.putInt("user_age", 0);
        editor.commit();
        Log.d("check", "user score and medicineStore initialized");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
