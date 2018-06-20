package com.peacecorps.malaria.activities;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.fragment.ThirdAnalyticFragment;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;
import com.peacecorps.malaria.widget.MedicineStatusWidgetProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ankita on 6/13/2015.
 */
public class DayFragmentActivity extends FragmentActivity {


    private static String TAGD = DayFragmentActivity.class.getSimpleName();
    private final String[] months = { "January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };
    private String date_header=""; private String mon="";
    private int day,month, year;
    private String drugPicked="";
    static SharedPreferenceStore mSharedPreferenceStore;
    public Context mFragmentContext;
    final Context con = this;
    private RadioGroup btnRadGroup;
    private RadioButton btnRadButton;
    private String ch="";
    private int flag=0;
    private long curr_time=0;
    private static DatabaseSQLiteHelper sqLite;
    //yatna
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        /* setting view for Day Fragment Activity*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_day);

        /*declaring variables for accessing Shared Preferences*/
        mSharedPreferenceStore = new SharedPreferenceStore();
        mFragmentContext = DayFragmentActivity.this
                .getApplicationContext();
        mSharedPreferenceStore.getSharedPreferences(this);

        /*defining variables for accessing Database*/
        sqLite= new DatabaseSQLiteHelper(this);

        ImageButton btnChangeData = (ImageButton)findViewById(R.id.btnChangeData);
        final ImageView indicator = (ImageView)findViewById(R.id.medi_indicator);

        /*displaying clicked date on the Day Fragment*/
        Intent intent = getIntent();
        String selected_date = intent.getStringExtra(ThirdAnalyticFragment.DATE_TAG);
        TextView dayDB = (TextView)findViewById(R.id.dayDB);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        Date comp_date=Calendar.getInstance().getTime();
        Log.d(TAGD,""+comp_date.getDate());
        Log.d(TAGD,""+comp_date.getMonth());
        Log.d(TAGD,""+comp_date.getYear()+1900);
        try {
           comp_date   = dateFormatter.parse(selected_date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(comp_date);
        month=cal.get(Calendar.MONTH);
        day=cal.get(Calendar.DATE);
        mon=months[month];
        year=cal.get(Calendar.YEAR);
        curr_time=cal.getTimeInMillis();

        date_header=String.valueOf(day)+" "+mon+" "+String.valueOf(year);
        Log.d(TAGD, date_header);
        dayDB.setText(date_header);

        /*dispaying chosen drug out of the three*/
        TextView drugID = (TextView)findViewById(R.id.drugID);
        drugPicked=SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.drugPicked",null);
        drugID.setText(drugPicked);
        //Log.d(TAGD, drugPicked);

        /*fetching information about the first time drug was taken*/
        long firstTime = SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria.firstRunTime", 0);
        //Log.d(TAGD,String.valueOf(firstTime));
        cal.setTimeInMillis(firstTime);
        int firstDate=cal.get(Calendar.DATE);
        int firstMonth= cal.get(Calendar.MONTH);
        int firstYear= cal.get(Calendar.YEAR);
        Log.d(TAGD,"First Run Time:"+firstDate+" "+firstMonth+" "+firstYear);

        /*Querying the database whether drug was taken on that specific day or not*/
        Log.d(TAGD,"querying  "+day+" "+month+" "+year+" ");
        final String data=sqLite.getMedicationData(day, month, year);
        Log.d(TAGD, "++++++" + data + "++++++");


        /*Implementing Editing Data Button for a Day Page*/
        btnChangeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(con,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                dialog.setContentView(R.layout.day_dialog);
                dialog.setTitle("Medicine Consumption");

                ImageView drug = (ImageView) dialog.findViewById(R.id.image);
                drug.setBackgroundResource(R.drawable.drug_normal);

                TextView tv = (TextView) dialog.findViewById(R.id.text);
                tv.setText("Added inaccurate data? Don't worry, you can change here.");
                tv.setTextSize(17);
                tv.setTextColor(getResources().getColor(R.color.golden_brown));

                btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);

                Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get selected radio button from radioGroup
                        int selectedId = btnRadGroup.getCheckedRadioButtonId();

                        // find the radiobutton by returned id
                        btnRadButton = (RadioButton) dialog.findViewById(selectedId);

                        //acting according to the radio button response
                        ch = btnRadButton.getText().toString();
                        Log.d(TAGD, "Radio Button Response-" + ch);
                        if (flag == 0) {
                            Log.d(TAGD, "Radio Button Response-" + ch);
                            if (ch.equalsIgnoreCase("yes")) {
                                Toast.makeText(con,
                                        btnRadButton.getText(), Toast.LENGTH_SHORT).show();
                                int accept_count = SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drugAcceptedCount", 0) + 1;
                                Log.d(TAGD, "accept count:" + accept_count);
                                SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugAcceptedCount", accept_count).apply();
                                long firstTime = sqLite.getFirstTime();
                                Log.d(TAGD, "" + SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria.firstRunTime", 0));
                                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.firstRunTime", firstTime).apply();
                                //updating adherence
                                double prcntage = 0.0;
                                Log.d(TAGD, "Adherence when Yes:" + prcntage);
                                sqLite.updateMedicationEntry(day, month, year, "yes", prcntage);

                                Log.d(TAGD, "Getting count:" + sqLite.getCountTaken());
                                prcntage=computeAdherenceRate(curr_time);
                                sqLite.updateMedicationEntry(day, month, year, "yes", prcntage);
                                //updating doses in a row
                                int dosesInaRow = 0;
                                if (SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly", false)) {
                                    dosesInaRow = sqLite.getDosesInaRowWeekly();
                                } else {
                                    dosesInaRow = sqLite.getDosesInaRowDaily();
                                }
                                Log.d(TAGD, "Doses in a Row:" + dosesInaRow);
                                SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose", dosesInaRow).apply();
                                indicator.setBackgroundResource(R.drawable.accept_medi_checked_);

                                //yatna
                                sharedPreferences= PreferenceManager.getDefaultSharedPreferences(con);
                                editor=sharedPreferences.edit();
                                int score=sharedPreferences.getInt("userScore",0);
                                int medicineStore=sharedPreferences.getInt("medicineStore",0);
                                if(data.compareTo("yes")==0){;
                                    //do nothing as medicine has already been taken
                                }else{
                                    score=score+1;
                                    editor.putInt("userScore",score);
                                    editor.putInt("medicineStore",medicineStore-1);
                                    editor.commit();
                                }

                            } else if (ch.equalsIgnoreCase("no")) {
                                Toast.makeText(con,
                                        btnRadButton.getText(), Toast.LENGTH_SHORT).show();
                                long firstTime = sqLite.getFirstTime();
                                //Log.d(TAGD, "" + SharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria.firstRunTime", 0));
                                SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria.firstRunTime", firstTime).apply();
                                String st = sqLite.getStatus(day, month, year);
                                if (st != null && st.equalsIgnoreCase("yes")) {
                                    int accept_count = SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drugAcceptedCount", 0) - 1;
                                    SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.drugAcceptedCount", accept_count).apply();
                                }
                                //updating adherence
                                double prcntage = computeAdherenceRate(curr_time);
                                Log.d(TAGD, "Adherence when No:" + prcntage);
                                sqLite.updateMedicationEntry(day, month, year, "no", prcntage);

                                //upating doses in a row
                                int dosesInaRow = 0;
                                if (SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly", false)) {
                                    dosesInaRow = sqLite.getDosesInaRowWeekly();
                                } else {
                                    dosesInaRow = sqLite.getDosesInaRowDaily();
                                }
                                Log.d(TAGD, "Doses in a Row:" + dosesInaRow);
                                SharedPreferenceStore.mEditor.putInt("com.peacecorps.malaria.dailyDose", dosesInaRow).apply();
                                indicator.setBackgroundResource(R.drawable.reject_medi_checked);
                                //yatna
                                sharedPreferences= PreferenceManager.getDefaultSharedPreferences(con);
                                editor=sharedPreferences.edit();
                                int score=sharedPreferences.getInt("userScore",0);
                                int medicineStore=sharedPreferences.getInt("medicineStore",0);
                                if(data.compareTo("no")==0){;
                                    //do nothing as medicine has already been taken
                                }else{
                                    score=score-1;
                                    editor.putInt("userScore",score);
                                    editor.putInt("medicineStore",medicineStore+1);
                                    editor.commit();
                                }
                            } else
                                dialog.dismiss();

                            //update the widgets
                            int widgetIds[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MedicineStatusWidgetProvider.class));
                            MedicineStatusWidgetProvider medicineWidgets = new MedicineStatusWidgetProvider();
                            medicineWidgets.onUpdate(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),widgetIds);
                        } else {
                            //Future Date is not allowed to Edit
                            Toast.makeText(getApplicationContext(), "You are not allowed to edit!", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();

                    }
                });
                Button btnCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        /*Setting the Indicator according to the Result*/

        if(data.compareTo("yes")==0)
        {
            Toast.makeText(getApplicationContext(),"I took medicine!",Toast.LENGTH_LONG).show();
            indicator.setBackgroundResource(R.drawable.accept_medi_checked_);
        }
        else if(data.compareTo("no")==0) {
            indicator.setBackgroundResource(R.drawable.reject_medi_checked);
            Toast.makeText(getApplicationContext(), "I didn't take medicine!", Toast.LENGTH_LONG).show();
        }
        else
        {   Log.d(TAGD,"Inside Missed Drug Entry");

            selected_date=SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.checkMediLastTakenTime","");
            SimpleDateFormat dateF = new SimpleDateFormat("dd/MM");
            Date cd=Calendar.getInstance().getTime();
            try {
                cd   = dateF.parse(selected_date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cal.setTime(cd);

            Log.d(TAGD,"Medication Last Taken Time");
            Log.d(TAGD, "" + cal.get(Calendar.DATE));
            Log.d(TAGD, "" + cal.get(Calendar.MONTH));
            Log.d(TAGD, "" + cal.get(Calendar.YEAR));

            long queried_date=comp_date.getTime();
            long last_medication_date = cd.getTime();

            Calendar c= Calendar.getInstance();
            long current_date= c.getTimeInMillis();


            if(queried_date>=firstTime)
            {
                if(queried_date<=current_date)
                {
                    Toast.makeText(getApplicationContext(), "I missed entering medicine!", Toast.LENGTH_LONG).show();
                    sqLite.insertOrUpdateMissedMedicationEntry(day, month, year, 0);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "This is a future date, you cannot edit it!", Toast.LENGTH_LONG).show();
                    btnChangeData.setBackgroundResource(R.drawable.roundedbutton_grey);
                    btnChangeData.setClickable(false);
                    flag=1;
                }
            }
            else {
                //Toast.makeText(getApplicationContext(), "This is a date before medication even started, you can't edit it!", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "I missed entering medicine!", Toast.LENGTH_LONG).show();
                sqLite.insertOrUpdateMissedMedicationEntry(day, month, year, 0);

            }





        }



    }

    /**Computing the Adherence Rate for selected Date**/
    public double computeAdherenceRate(long day_time) {
        long interval = checkDrugTakenTimeInterval("firstRunTime", day_time);

        Date e=new Date();
        e.setTime(day_time);

        Date s=new Date();
        s.setTime(sqLite.getFirstTime());

        long takenCount = sqLite.getCountTakenBetween(s,e);
        Log.d(TAGD,"Taken Count while computing adherence :"+takenCount);
        double adherenceRate = ((double)takenCount /(double) interval) * 100;
        return adherenceRate;
    }


    /**Finding the Time Interval between two dates**/
    public  long checkDrugTakenTimeInterval(String time,long day_time) {
        long interval = 0;

        long takenDate = sqLite.getFirstTime();
        if(takenDate!=0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(day_time);
            cal.add(Calendar.MONTH, 1);
            Calendar calt = Calendar.getInstance();
            calt.setTimeInMillis(takenDate);
            Log.d(TAGD, "First :" + calt.get(Calendar.MONTH));
            calt.add(Calendar.MONTH, 2);
            Date start = calt.getTime();
            Date end = cal.getTime();
            SharedPreferenceStore.mEditor.putLong("com.peacecorps.malaria."
                    + time, takenDate).apply();
            if (SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly", false)) {
                interval = sqLite.getIntervalWeekly(start, end, SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.weeklyDay", 1));
            } else {
                interval = sqLite.getIntervalDaily(start, end);
            }
            Log.d(TAGD,"First Date :"+ calt.get(Calendar.DATE));
            Log.d(TAGD, "Current Date :" + cal.get(Calendar.DATE));
            Log.d(TAGD,"First :"+ calt.get(Calendar.MONTH));
            Log.d(TAGD, "Current :" + cal.get(Calendar.MONTH));
            Log.d(TAGD, "Interval :" + interval);
            Log.d(TAGD, time + ":" + takenDate);
        }
        else
             interval=1;

        return interval;
    }


}
