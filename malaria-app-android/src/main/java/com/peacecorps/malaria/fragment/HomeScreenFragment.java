package com.peacecorps.malaria.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.activities.RemainderToneActivity;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.activities.UserMedicineSettingsFragmentActivity;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;
import com.peacecorps.malaria.services.AlarmService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeScreenFragment extends Fragment {

    static final private int INIT_HOUR = 5;
    static final private int INIT_MINUTE = 30;

    private Button mAcceptMedicationButton;
    private Button mRejectMedicationButton;
    private Button mSettingsButton;
    private Button mRemainderToneButton;
    private TextView mCurrentDateLabel;
    private TextView mCurrentDayOfweekLabel;
    private LinearLayout warningView;
    private static CharSequence mGetCurrentDate;
    private static int mDrugAcceptedCount;
    private static int drugRejectedCount;
    private Calendar mCalendar;
    private String[] mPossibledays = {"Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};
    private static View rootView;
    private static String TAGHSF = "HomeScreenFragment";
    private SharedPreferences sharedPreferences;

    int checkDay = -1;

    static SharedPreferenceStore mSharedPreferenceStore;
    private Dialog dialog = null;


    @Override
    public void onResume() {

        super.onResume();
        updateUI();

    }

    public void getSharedPreferences() {

        mSharedPreferenceStore.mPrefsStore = getActivity()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_screen, null);

        updateUI();
        return rootView;

    }

    public double computeAdherenceRate() {
        long interval = checkDrugTakenTimeInterval("firstRunTime");
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(this.getActivity());
        long takenCount = sqLite.getCountTaken();
        double adherenceRate = ((double)takenCount / (double)interval) * 100;
        Log.d(TAGHSF, "adherence:" + adherenceRate);
        return adherenceRate;
    }

    public void addButtonListeners() {
        mSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addDialog();

            }
        });

        mRemainderToneButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(getActivity(), RemainderToneActivity.class);
                startActivity(myIntent);

            }
        });
        mAcceptMedicationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MediaPlayer.create(getActivity(), R.raw.accept_button_sound)
                        .start();
                mDrugAcceptedCount += 1;
                int value = SharedPreferenceStore.mPrefsStore.getInt(
                        "com.peacecorps.malaria.AcceptedCount", 0) + 1;
                SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AcceptedCount",
                        value).commit();
                if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                        "com.peacecorps.malaria.isWeekly", false)) {

                    decideDrugTakenUIBoolean(true, true);
                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(getActivity());
                    databaseSQLiteHelper.getUserMedicationSelection(getActivity(), "weekly", Calendar.getInstance().getTime(), "yes", computeAdherenceRate());

                    int currentDose = databaseSQLiteHelper.getDosesInaRowWeekly();
                    mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.weeklyDose", currentDose).commit();

                } else {
                    decideDrugTakenUIBoolean(false, true);

                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(getActivity());
                    databaseSQLiteHelper.getUserMedicationSelection(getActivity(), "daily", Calendar.getInstance().getTime(), "yes", computeAdherenceRate());

                    int currentDose = databaseSQLiteHelper.getDosesInaRowDaily();
                    mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose", currentDose).commit();
                }
                //get user's score
                int score=sharedPreferences.getInt("userScore", 0);
                //get medicine in store
                int medicineStore=sharedPreferences.getInt("medicineStore",0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                //increase score if medicine is taken
                editor.putInt("userScore",score+1);
                //decrease medicine store by one if medicine is taken
                editor.putInt("medicineStore",medicineStore-1);
                editor.commit();
                Log.d("check","score updated");
            }
        });

        mRejectMedicationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MediaPlayer.create(getActivity(), R.raw.reject_button_sound)
                        .start();
                drugRejectedCount += 1;
                if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                        "com.peacecorps.malaria.isWeekly", false)) {
                    decideDrugTakenUIBoolean(true, false);
                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(getActivity());
                    databaseSQLiteHelper.getUserMedicationSelection(getActivity(), "weekly", Calendar.getInstance().getTime(), "no", computeAdherenceRate());

                } else {
                    decideDrugTakenUIBoolean(false, false);
                    DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(getActivity());
                    databaseSQLiteHelper.getUserMedicationSelection(getActivity(), "daily", Calendar.getInstance().getTime(), "no", computeAdherenceRate());

                }

            }
        });
    }

    public void createView() {

        mAcceptMedicationButton = (Button) rootView
                .findViewById(R.id.fragment_home_screen_accept_medication_button);
        mRejectMedicationButton = (Button) rootView
                .findViewById(R.id.fragment_home_screen__reject_medication_button);
        mSettingsButton = (Button) rootView
                .findViewById(R.id.fragment_home_screen_settings_button);
        mRemainderToneButton=(Button) rootView
                .findViewById(R.id.fragment_home_screen_set_tone_button);
        mCurrentDateLabel = (TextView) rootView
                .findViewById(R.id.fragment_home_screen_current_date);
        mCurrentDayOfweekLabel = (TextView) rootView
                .findViewById(R.id.fragment_home_screen_current_day_of_week);
        //yatna
        warningView=(LinearLayout)rootView
                .findViewById(R.id.warningView);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        //get the limit set by user from shared preference
        int alertTime=sharedPreferences.getInt("alertTime",-1);
        //display warning if medicine in store is less than the limit set
        if(alertTime!=-1 && sharedPreferences.getInt("medicineStore",0)<alertTime){
            warningView.setVisibility(View.VISIBLE);
        }

        mCurrentDateLabel.setTextColor(Color.rgb(89, 43, 21));
        mCurrentDayOfweekLabel.setTextColor(Color.rgb(89, 43, 21));
        mCurrentDateLabel.setText(mGetCurrentDate);
        mCurrentDayOfweekLabel
                .setText(decideDayofWeek(checkDay, mPossibledays));

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/garreg.ttf");
        mCurrentDayOfweekLabel.setTypeface(custom_font);
        mCurrentDateLabel.setTypeface(custom_font);
        mCurrentDayOfweekLabel.setTextSize(50);
        mCurrentDateLabel.setTextSize(20);
        //checkMediLastTakenTime = (TextView) rootView.findViewById(R.id.checkMediLastTakenTime);

    }

    public void updateUI() {
        getSharedPreferences();

        mCalendar = Calendar.getInstance();

        checkDay = mCalendar.get(Calendar.DAY_OF_WEEK);


        getSettings();
        createView();
        addButtonListeners();
        getSettings();
        decideisDrugTakenUI();
       // updateMediLastTime();
    }

    public void saveUsersettings(Boolean state, Boolean isWeekly) {
        if (isWeekly) {
            mSharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.weeklyDate",
                    new Date().getTime()).commit();
            mSharedPreferenceStore.mEditor.putBoolean(
                    "com.peacecorps.malaria.isWeeklyDrugTaken", state).commit();
        } else {
            mSharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.dateDrugTaken",
                    new Date().getTime()).commit();
            mSharedPreferenceStore.mEditor.putBoolean("com.peacecorps.malaria.isDrugTaken",
                    state).commit();
        }
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugRejectedCount",
                drugRejectedCount).commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugAcceptedCount",
                mDrugAcceptedCount).commit();

    }

    public void getSettings() {
        checkDay = mCalendar.get(Calendar.DAY_OF_WEEK);
        mGetCurrentDate = new SimpleDateFormat("MM/dd/yyyy",
                Locale.getDefault()).format(mCalendar.getTime());
        mDrugAcceptedCount = mSharedPreferenceStore.mPrefsStore.getInt(
                "com.peacecorps.malaria.drugAcceptedCount", 0);
        drugRejectedCount = mSharedPreferenceStore.mPrefsStore.getInt(
                "com.peacecorps.malaria.drugRejectedCount", 0);
        decideDayofWeek(checkDay, mPossibledays);
    }

    public void missedWeekUI() {
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.weeklyDose", 0).commit();
        mCurrentDateLabel.setTextColor(Color.RED);
        mCurrentDayOfweekLabel.setTextColor(Color.RED);
    }

    public void decideisDrugTakenUI() {
        //if drug is taken weekly//
        if (mSharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",
                false)) {
            if (checkDrugTakenTimeInterval("weeklyDate") == 0) {
                if ((mSharedPreferenceStore.mPrefsStore.getBoolean(
                        "com.peacecorps.malaria.isWeeklyDrugTaken", false))) {
                    isDrugTakenUI();
                } else {
                    newDayUI();
                }
            } else {
                if (checkDrugTakenTimeInterval("weeklyDate") < 7
                        && checkDrugTakenTimeInterval("weeklyDate") > 0) {
                    if ((mSharedPreferenceStore.mPrefsStore.getBoolean(
                            "com.peacecorps.malaria.isWeeklyDrugTaken", false))) {
                        isDrugTakenUI();
                    } else {
                        missedWeekUI();
                        newDayUI();
                    }
                } else if (checkDrugTakenTimeInterval("weeklyDate") > 7) {
                    SharedPreferenceStore.mEditor.putInt(
                            "com.peacecorps.malaria.AcceptedCount", 0).commit();
                    missedWeekUI();
                    newDayUI();
                }
            }
        } else { //if drug is taken daily//
            if (checkDrugTakenTimeInterval("dateDrugTaken") == 0) {
                if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                        "com.peacecorps.malaria.isDrugTaken", false)) {
                    isDrugTakenUI();
                } else {

                    isDrugNotTakenUI();
                }

            } else {

                if (checkDrugTakenTimeInterval("dateDrugTaken") > 1) {
                    mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose", 0).apply();
                }

                newDayUI();
            }
        }
    }

    public long checkDrugTakenTimeInterval(String time) {

        long interval = 0;
        long today = new Date().getTime();
        DatabaseSQLiteHelper sqLite= new DatabaseSQLiteHelper(getActivity());
        long takenDate= sqLite.getFirstTime();
        if(time.compareTo("firstRunTime")==0) {
            if(takenDate!=0) {
                Log.d(TAGHSF, "First Run Time at FAF->" + takenDate);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(takenDate);
                cal.add(Calendar.MONTH, 1);
                Log.d(TAGHSF,"CURRENT : "+cal.get(Calendar.MONTH));
                Date start= cal.getTime();
                Date end= Calendar.getInstance().getTime();
                end.setTime(today);
                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria."
                        + time, takenDate).apply();
                if(SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",false)) {
                    interval = sqLite.getIntervalWeekly(start,end,SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.weeklyDay",1));
                }
                else
                {
                    interval = sqLite.getIntervalDaily(start,end);
                }

                /*long oneDay = 1000 * 60 * 60 * 24;
                interval = (today - takenDate) / oneDay;*/
                return interval;
            }
            else
                return 1;
        }
        else {
            takenDate=SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria."
                    + time, takenDate);
            long oneDay = 1000 * 60 * 60 * 24;
            interval = (today - takenDate) / oneDay;
            return interval;
        }
    }

    public void newDayUI() {
        mAcceptMedicationButton
                .setBackgroundResource(R.drawable.accept_medi_checked_);
        mRejectMedicationButton
                .setBackgroundResource(R.drawable.reject_medi_checked);
        setButtonState(true);

    }

    public void isDrugNotTakenUI() {
        mAcceptMedicationButton
                .setBackgroundResource(R.drawable.accept_medi_grayscale);
        mRejectMedicationButton
                .setBackgroundResource(R.drawable.reject_medi_checked);
        setButtonState(false);
    }

    public void isDrugTakenUI() {
        mCurrentDateLabel.setTextColor(Color.rgb(89, 43, 21));
        mCurrentDayOfweekLabel.setTextColor(Color.rgb(89, 43, 21));
        mAcceptMedicationButton
                .setBackgroundResource(R.drawable.accept_medi_checked_);
        mRejectMedicationButton
                .setBackgroundResource(R.drawable.reject_medi_grayscale);
        setButtonState(false);
        storeMediTimeLastChecked();

    }

    public void setButtonState(boolean state) {
        mAcceptMedicationButton.setEnabled(state);
        mRejectMedicationButton.setEnabled(state);
    }

    public void decideDrugTakenUIBoolean(Boolean isWeekly, Boolean isTaken) {
        if (isWeekly) {
            if (checkDrugTakenTimeInterval("weeklyDate") > 1) {
                changeWeeklyAlarmTime();
            }
        }
        saveUsersettings(isTaken, isWeekly);
        if (isTaken) {
            isDrugTakenUI();
        } else {
            isDrugNotTakenUI();
        }
    }

    public void storeMediTimeLastChecked() {
        CharSequence lastMedicationCheckedTime = "";
        Calendar c = Calendar.getInstance();
        lastMedicationCheckedTime = new SimpleDateFormat("dd/MM",
                Locale.getDefault()).format(c.getTime());

        mSharedPreferenceStore.mEditor.putString(
                "com.peacecorps.malaria.checkMediLastTakenTime",
                lastMedicationCheckedTime.toString()).commit();
    }

    public void changeWeeklyAlarmTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE) - 1;
        getActivity().startService(
                new Intent(getActivity(), AlarmService.class));
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AlarmHour", hour)
                .commit();
        mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.AlarmMinute", minute)
                .commit();
    }


    public String decideDayofWeek(int checkDay, String possibleDays[]) {
        String currentDayOfWeek = null;
        switch (checkDay) {
            case 1:
                currentDayOfWeek = possibleDays[0];
                break;
            case 2:
                currentDayOfWeek = possibleDays[1];
                break;
            case 3:
                currentDayOfWeek = possibleDays[2];
                break;
            case 4:
                currentDayOfWeek = possibleDays[3];
                break;
            case 5:
                currentDayOfWeek = possibleDays[4];
                break;
            case 6:
                currentDayOfWeek = possibleDays[5];
                break;
            case 7:
                currentDayOfWeek = possibleDays[6];
                break;

        }
        return currentDayOfWeek;
    }


    public void missedDayRecord(int day, int month, int year){

        DatabaseSQLiteHelper sqLH = new DatabaseSQLiteHelper(getActivity());
        sqLH.insertOrUpdateMissedMedicationEntry(day,month,year,computeAdherenceRate());

    }

    public void addDialog()
    {
        dialog = new Dialog(this.getActivity(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setContentView(R.layout.resetdata_dialog);
        dialog.setTitle("Reset Data");

        //final RadioGroup btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReset);
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOKReset);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          /*      // get selected radio button from radioGroup
                int selectedId = btnRadGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton btnRadButton = (RadioButton) dialog.findViewById(selectedId);

                String ch = btnRadButton.getText().toString();

                if(ch.equalsIgnoreCase("yes"))
                {
                    DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
                    sqLite.resetDatabase();
                    mSharedPreferenceStore.mEditor.clear().commit();
                    SharedPreferenceStore.mEditor.clear().commit();
                    startActivity(new Intent(getActivity(),
                            UserMedicineSettingsFragmentActivity.class));
                    getActivity().finish();
                }
                else
                {
                    dialog.dismiss();
                }*/
                DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
                sqLite.resetDatabase();
                mSharedPreferenceStore.mEditor.clear().commit();
                SharedPreferenceStore.mEditor.clear().commit();
                startActivity(new Intent(getActivity(),
                        UserMedicineSettingsFragmentActivity.class));
                getActivity().finish();

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

    /*public void updateMediLastTime() {
        if (FirstAnalyticFragment.checkMediLastTakenTime != null) {
            FirstAnalyticFragment.checkMediLastTakenTime.setText(mSharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.checkMediLastTakenTime", "").toString());
        }
    }*/

}