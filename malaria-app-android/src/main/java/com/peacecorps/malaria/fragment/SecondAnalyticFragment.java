
package com.peacecorps.malaria.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.peacecorps.malaria.R;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.activities.UserMedicineSettingsFragmentActivity;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;

import java.util.Calendar;

/**Second Analytic Fragment
 * It shows the Progress Bars and Graph
 * **/
public class SecondAnalyticFragment extends Fragment {

    private TextView firstMonthProgressLabel, secondMonthProgressLabel, thirdMonthProgressLabel, fourthMonthProgressLabel;
    private TextView firstMonthProgressPercent, secondMonthProgressPercent, thirdMonthProgressPercent, fourthMonthProgressPercent;
    private ProgressBar firstMonthProgressBar, secondMonthProgressBar, thirdMonthProgressBar, fourthMonthProgressBar;
    private Button mSettingsButton;
    private View rootView;
    public final static String MONTH_REQ = "com.peacecorps.malaria.secondanalyticfragment.MONTHREQ";

    static SharedPreferenceStore mSharedPreferenceStore;


    private static final String DATABASE_NAME = "MalariaDatabase";
    private static final String userMedicationChoiceTable = "userSettings";
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};
    private String TAGSAF = "SecondAnalyticFragment";

    GraphViewSeries drugGraphSeries;
    private GraphViewData[] graphViewData;
    private int date;
    private String choice;
    private Dialog dialog = null;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Declaring the Views
        rootView = inflater.inflate(R.layout.fragment_second_analytic_screen,
                null);
        Log.e("MyTag", "Error message with my own tag");
        mSettingsButton = (Button) rootView.findViewById(R.id.fragment_second_screen_settings_button);

        firstMonthProgressLabel = (TextView) rootView.findViewById(R.id.firstMonthProgressLabel);
        secondMonthProgressLabel = (TextView) rootView.findViewById(R.id.secondMonthProgressLabel);
        thirdMonthProgressLabel = (TextView) rootView.findViewById(R.id.thirdMonthProgressLabel);
        fourthMonthProgressLabel = (TextView) rootView.findViewById(R.id.fourthMonthProgressLabel);

        firstMonthProgressPercent = (TextView) rootView.findViewById(R.id.firstMonthProgressPercent);
        secondMonthProgressPercent = (TextView) rootView.findViewById(R.id.secondMonthProgressPercent);
        thirdMonthProgressPercent = (TextView) rootView.findViewById(R.id.thirdMonthProgressPercent);
        fourthMonthProgressPercent = (TextView) rootView.findViewById(R.id.fourthMonthProgressPercent);

        firstMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.firstMonthProgressBar);
        secondMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.secondMonthProgressBar);
        thirdMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.thirdMonthProgressBar);
        fourthMonthProgressBar = (ProgressBar) rootView.findViewById(R.id.fourthMonthProgressBar);


        Calendar cal = Calendar.getInstance();

        date = Calendar.getInstance().get(Calendar.MONTH);
        //checking choic of pill whether weekly or daily
        if (mSharedPreferenceStore.mPrefsStore.getBoolean(
                "com.peacecorps.malaria.isWeekly", false)) {
            choice = "weekly";
        } else {
            choice = "daily";
        }

        updateUI(choice, date);


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI(choice, date);

    }

    int mdate;
    int myear;

    //finding month from its integer
    public String getMonth(int date) {
        String month[] = getResources().getStringArray(R.array.month);
        if (date == -1) {
            date = 11;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else if (date == -2) {
            date = 10;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else if (date == -3) {
            date = 9;
            myear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else {
            myear = Calendar.getInstance().get(Calendar.YEAR);
            mdate = date;
        }
        return month[date];
    }
    /*Opening Dialog on Clicking Gear Icon*/
    public void addButtonListeners() {
        mSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                addDialog();

            }
        });
        /*On Clicking the Progress Bars Opens Calendar of that Specific Month*/
        firstMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = firstMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "First progress", Toast.LENGTH_SHORT).show();
            }
        });

        secondMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = secondMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                startActivity(intent);
                Toast.makeText(getActivity(), "Second progress", Toast.LENGTH_SHORT).show();
            }
        });

        thirdMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = thirdMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                //yatna
                startActivityForResult(intent, 3);
                Toast.makeText(getActivity(), "Third progress", Toast.LENGTH_SHORT).show();
            }
        });

        fourthMonthProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ThirdAnalyticFragment.class);
                String mon = fourthMonthProgressLabel.getText().toString();
                intent.putExtra(MONTH_REQ, mon); //transfering the month Information for displaying Calendar of Specific Month
                //yatna
                startActivityForResult(intent,2);
                Toast.makeText(getActivity(), "Fourth progress", Toast.LENGTH_SHORT).show();
            }
        });


    }
    /*yatna*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2 || requestCode==3)
        {
            ViewPager vp=(ViewPager)getActivity().findViewById(R.id.vPager);
            //refresh all the pagers in view pager
            vp.getAdapter().notifyDataSetChanged();
        }

    }
    /*Fetching the Details and Settings from Shared Preferences*/
    public void getSharedPreferences() {

        mSharedPreferenceStore.mPrefsStore = getActivity()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }

    /*Finding No. of Days in Month*/
    public int getNumberOfDaysInMonth(int month) {
        return daysOfMonth[month];
    }

    /*Updating the Progress Bars
    * On the basis of drugs taken or not
    * Also on the basis of status of each day modified later in the calendar
    * */
    public void updateProgressBar(String choice, int date) {
        DatabaseSQLiteHelper sqLH = new DatabaseSQLiteHelper(getActivity());
        Typeface cf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/garreg.ttf");
        int SetupMonth= mSharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.SetupMonth",-1);
        int SetupYear= mSharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.SetupYear",-1);
        Log.d("setupYear/myear ",Integer.toString(SetupYear) +" " +Integer.toString(myear));
        firstMonthProgressLabel.setText(getMonth(date - 3));
        firstMonthProgressLabel.setTypeface(cf);
        int progress = sqLH.getData(mdate, myear, choice);
        float progressp = 0;
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;

        if(progressp>=50)
        {
            firstMonthProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.saf_progress_bar_green));
            firstMonthProgressBar.setBackground(getResources().getDrawable(R.drawable.progress_bg_green));

        }
        firstMonthProgressBar.setProgress((int) progressp);
        if((date-3)>=SetupMonth || myear!=SetupYear || (int)progressp!=0)
            firstMonthProgressPercent.setText("" + (int) progressp + "%");
        else
            firstMonthProgressPercent.setText("N.A");

        firstMonthProgressPercent.setTypeface(cf);

        secondMonthProgressLabel.setText(getMonth(date - 2));
        secondMonthProgressLabel.setTypeface(cf);
        progress = sqLH.getData(mdate, myear, choice);
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;

        if(progressp>=50)
        {
            secondMonthProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.saf_progress_bar_green));
            secondMonthProgressBar.setBackground(getResources().getDrawable(R.drawable.progress_bg_green));

        }
        secondMonthProgressBar.setProgress((int) progressp);
        if((date-2)>=SetupMonth || myear!=SetupYear || (int)progressp!=0)
            secondMonthProgressPercent.setText("" + (int) progressp + "%");
        else
            secondMonthProgressPercent.setText("N.A");

        secondMonthProgressPercent.setTypeface(cf);

        thirdMonthProgressLabel.setText(getMonth(date - 1));
        thirdMonthProgressLabel.setTypeface(cf);
        progress = sqLH.getData(mdate, myear, choice);
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;

        if(progressp>=50) {
            thirdMonthProgressBar.setBackground(getResources().getDrawable(R.drawable.progress_bg_green));
            thirdMonthProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.saf_progress_bar_green));
        }
            thirdMonthProgressBar.setProgress((int) progressp);
        if((date-1)>=SetupMonth || myear!=SetupYear || (int)progressp!=0)
            thirdMonthProgressPercent.setText("" + (int) progressp + "%");
        else
            thirdMonthProgressPercent.setText("N.A");
        thirdMonthProgressPercent.setTypeface(cf);

        fourthMonthProgressLabel.setText(getMonth(date));
        fourthMonthProgressLabel.setTypeface(cf);
        progress = sqLH.getData(mdate, myear, choice);
        Log.d(TAGSAF, "Query Return: " + progress);
        if (choice.equalsIgnoreCase("daily"))
            progressp = (float) progress / getNumberOfDaysInMonth(mdate) * 100;
        else
            progressp = progress * 25;
        Log.d(TAGSAF, "" + getNumberOfDaysInMonth(mdate));
        Log.d(TAGSAF, "" + progress);
        Log.d(TAGSAF, "" + progressp);

        if(progressp>=50) {
            fourthMonthProgressBar.setBackground(getResources().getDrawable(R.drawable.progress_bg_green));
            fourthMonthProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.saf_progress_bar_green));
        }
        fourthMonthProgressBar.setProgress((int) progressp);
        fourthMonthProgressPercent.setText("" + (int) progressp + "%");
        fourthMonthProgressPercent.setTypeface(cf);
    }

    /**Update UI is called on resume to Update the Graph and Progress Bars**/
    public void updateUI(String choice, int date) {

        updateProgressBar(choice, date);
        DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
        if (sqLite.getDosesInaRowDaily()!=0)
        {
            SetupAndShowGraph();
        }
        getSharedPreferences();
        addButtonListeners();

    }
    /**Setting Up Graph**/
    public void SetupAndShowGraph() {


        GraphViewData graphViewData[] = new GraphViewData[DatabaseSQLiteHelper.date.size()];
        String verLabels[]={"100%","50%","25%","0%"};
        //adding data
        for (int index = 0; index < DatabaseSQLiteHelper.percentage.size(); index++) {

            graphViewData[index] = new GraphViewData(DatabaseSQLiteHelper.date.get(index), Double.parseDouble("" + DatabaseSQLiteHelper.percentage.get(index)));
        }
        drugGraphSeries = new GraphViewSeries(graphViewData);

        GraphView lineGraphView = new LineGraphView(getActivity(), "");
         //styling graph
        lineGraphView.getGraphViewStyle().setGridColor(getResources().getColor(R.color.lightest_brown));
        lineGraphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.BOTH);
        lineGraphView.getGraphViewStyle().setHorizontalLabelsColor(getResources().getColor(R.color.golden_brown));
        lineGraphView.getGraphViewStyle().setVerticalLabelsColor(getResources().getColor(R.color.golden_brown));
        lineGraphView.setBackground(getResources().getDrawable(R.drawable.graph_bg));
        lineGraphView.getGraphViewStyle().setTextSize(8.0F);
        lineGraphView.setVerticalLabels(verLabels);

        lineGraphView.setTitle("Adherence Rate vs DayWise");


        lineGraphView.setScrollable(true);
        lineGraphView.setScalable(true);

        lineGraphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isXAxis) {


                return null;
            }
        });

        ((LineGraphView) lineGraphView).setDrawBackground(true);
        ((LineGraphView) lineGraphView).setDrawDataPoints(true);
        ((LineGraphView) lineGraphView).setBackgroundColor(getResources().getColor(R.color.light_blue));
         float r=(float)0.20;
        ((LineGraphView) lineGraphView).setDataPointsRadius(r);
        //plotting data
        Log.d("Length"," " +graphViewData.length);

        lineGraphView.addSeries(drugGraphSeries);

        //showing graph
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.graphView);
        linearLayout.addView(lineGraphView);


    }
    /**Reset Dailog**/
    public void addDialog()
    {    //opening the reset Dialog
        dialog = new Dialog(this.getActivity(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setContentView(R.layout.resetdata_dialog);
        dialog.setTitle("Reset Data");

       // final RadioGroup btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReset);
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOKReset);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            /*    // get selected radio button from radioGroup
                int selectedId = btnRadGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton btnRadButton = (RadioButton) dialog.findViewById(selectedId);

                String ch = btnRadButton.getText().toString();

                if (ch.equalsIgnoreCase("yes")) {
                    DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getActivity());
                    sqLite.resetDatabase();
                    mSharedPreferenceStore.mEditor.clear().commit();
                    startActivity(new Intent(getActivity(),
                            UserMedicineSettingsFragmentActivity.class));
                    getActivity().finish();
                } else {
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
