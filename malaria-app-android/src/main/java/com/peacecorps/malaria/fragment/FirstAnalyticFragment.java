package com.peacecorps.malaria.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.activities.UserMedicineSettingsFragmentActivity;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;

import java.util.Calendar;
import java.util.Date;


public class FirstAnalyticFragment extends Fragment {


    static SharedPreferenceStore mSharedPreferenceStore;
    static View rootView;
    public static TextView checkMediLastTakenTime = null;
    public static TextView doses = null;
    public static TextView adherence = null;
    private Button mSettingsButton;
    private String TAGFAF = "FirstAnalyticFragment";
    private Dialog dialog = null;
    private TextView mlt,dinr,atm;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater
                .inflate(R.layout.fragment_first_analytic_screen, null);

        //declaring views
        mSettingsButton = (Button) rootView.findViewById(R.id.fragment_first_screen_settings_button);
        checkMediLastTakenTime = (TextView) rootView.findViewById(R.id.checkMediLastTakenTime);
        doses = (TextView) rootView.findViewById(R.id.doses);
        adherence = (TextView) rootView.findViewById(R.id.adherence);
        updateUI();
        mlt=(TextView)rootView.findViewById(R.id.mlt);
        dinr=(TextView)rootView.findViewById(R.id.dinr);
        atm=(TextView)rootView.findViewById(R.id.atm);
        //setting fonts
        Typeface cf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/garreg.ttf");
        Typeface cfb = Typeface.createFromAsset(getActivity().getAssets(), "fonts/garbold.ttf");
        mlt.setTypeface(cf);
        dinr.setTypeface(cf);
        atm.setTypeface(cf);

        return rootView;

    }

    @Override
    public void onResume(){
        updateUI();
        super.onResume();

    }

    public void updateUI(){

        //calling functions
        updateMediLastTime();
        Log.d(TAGFAF,"AFTER CHK MEDI LAST TIME");
        if (checkMediLastTakenTime != null)
            updateDoses();
        Log.d(TAGFAF, "AFTER UPDATE DOSES");
        updateAdherence();
        Log.d(TAGFAF, "AFTER UPDATE ADHERENCE");
        getSharedPreferences();
        Log.d(TAGFAF, "AFTER SHARED PREFS");
        addButtonListeners();
        Log.d(TAGFAF, "AFTER BUTTON LISTENERS");
    }


    public void addButtonListeners() {
        mSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addDialog();

            }
        });
    }

    public void getSharedPreferences() {

        mSharedPreferenceStore.mPrefsStore = getActivity()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }


    public long checkDrugTakenTimeInterval(String time) {
        //finding the interval of time between today and the 'time'
        long interval = 0;
        long today = new Date().getTime();
        Date tdy= Calendar.getInstance().getTime();
        tdy.setTime(today);
        DatabaseSQLiteHelper sqLite= new DatabaseSQLiteHelper(getActivity());
        long takenDate= sqLite.getFirstTime();
        if(time.compareTo("firstRunTime")==0) {
            if(takenDate!=0) {
                Log.d(TAGFAF, "First Run Time at FAF->" + takenDate);
                Calendar cal=Calendar.getInstance();
                cal.setTimeInMillis(takenDate);
                cal.add(Calendar.MONTH, 1);
                Date start=cal.getTime();
                int weekDay=cal.get(Calendar.DAY_OF_WEEK);
                //calaculating no. of weekdays for weekly drug
                if(SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",false))
                    interval=sqLite.getIntervalWeekly(start,tdy,weekDay);
                else
                    interval=sqLite.getIntervalDaily(start,tdy);
                //^for daily drug only the no. of days
                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria."
                        + time, takenDate).apply();
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

    public void updateAdherence(){

        /**Calculating Adherence**/
        long interval = checkDrugTakenTimeInterval("firstRunTime");
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
        long takenCount = sqLite.getCountTaken();
        double adherenceRate;
        Log.d(TAGFAF,"taken Count:"+takenCount);
        if(interval!=1)
            adherenceRate = ((double)takenCount / (double)interval) * 100;
        else
            adherenceRate = 100;

        String ar = String.format("%.2f %%", adherenceRate);
        Log.d(TAGFAF,"Adherence Rate:"+ar);
        //adherence.setText(ar);

    }

    public void updateDoses()
    {
        /*Updating Doses in a Row for Weekly and Daily Pill Seperately*/
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
        Log.d(TAGFAF, "INSIDE updateDoses");
        if(mSharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",false)) {
            int d = sqLite.getDosesInaRowWeekly();
            mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.weeklyDose", d).apply();
            doses.setText("" + d/*mSharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.weeklyDose", 0)*/);
        }
        else
        {
            int d = sqLite.getDosesInaRowDaily();
            mSharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose",d).apply();
            doses.setText("" + mSharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.dailyDose", 0));
        }
    }

    public void updateMediLastTime() {
        /*Updating the most recent time medication was taken*/
       /* if (checkMediLastTakenTime != null) {
            checkMediLastTakenTime.setText(mSharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.checkMediLastTakenTime", "").toString());
        }*/
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
        String  lastTaken= sqLite.getLastTaken();
      checkMediLastTakenTime.setText(lastTaken);
        Log.d("LastTaken-------------------------: ",lastTaken);

    }

    public void addDialog()
    {   /*Opens the Reset Dialog*/
        dialog = new Dialog(this.getActivity(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setContentView(R.layout.resetdata_dialog);
        dialog.setTitle("Reset Data");

        //final RadioGroup btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReset);
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOKReset);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  // get selected radio button from radioGroup
                int selectedId = btnRadGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton btnRadButton = (RadioButton) dialog.findViewById(selectedId);

                String ch = btnRadButton.getText().toString();

                if(ch.equalsIgnoreCase("yes"))
                {   //if yes, reset the database
                    DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
                    sqLite.resetDatabase();
                    mSharedPreferenceStore.mEditor.clear().commit();
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

}

