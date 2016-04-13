package com.peacecorps.malaria;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by yogesh on 11/4/16.
 */
public class CalendarViewActivity extends FragmentActivity {

    private static final String TAG = "CalendarViewActivity";
    public final static String DATE_TAG = "com.peacecorps.malaria.thirdanalyticfragment.SELECTED_DATE";

    private boolean undo = false;
    private CaldroidFragment caldroidFragment;
    private SimpleDateFormat formatter;
    private Calendar _calendar;
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
    private Intent DayFragmentintent;

    @SuppressLint("NewApi")
    private int month, year;

    @SuppressWarnings("unused")
    @SuppressLint({"NewApi", "NewApi", "NewApi", "NewApi"})
    private final DateFormat dateFormatter = new DateFormat();

    private static final String dateTemplate = "MMMM yyyy";
    DatabaseSQLiteHelper dbSQLH = new DatabaseSQLiteHelper(this);

    private final String[] monthsArray = {"January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    private final int[] daysOfMonthArray = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};


    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendarview);

        formatter = new SimpleDateFormat("dd MMM yyyy");

        DayFragmentintent = new Intent(getApplication(), DayFragmentActivity.class);

        /** Added by Ankita for getting specific month **/
        Intent intent = getIntent();
        String mon = intent.getStringExtra(SecondAnalyticFragment.MONTH_REQ);
        Calendar cal;
        int intmon = 0;
        Date dat;
        try {
            dat = new SimpleDateFormat("MMMM").parse(mon);
            cal = Calendar.getInstance();
            cal.setTime(dat);
            intmon = cal.get(Calendar.MONTH);
        } catch (ParseException e) {
            Log.d(TAG, "Parse Month Error!");
        }
        _calendar = Calendar.getInstance();
        year = _calendar.get(Calendar.YEAR);
        _calendar.set(Calendar.MONTH, intmon + 1);
        month = _calendar.get(Calendar.MONTH);

        Calendar cal_head = Calendar.getInstance();
        cal_head.set(Calendar.MONTH, intmon);

        Log.d(TAG, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
                + year);

        /** Setup caldroid fragment **/
        caldroidFragment = new CaldroidFragment();

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    Constants.CALENDAR_SAVED_STATE_KEY);
        }

        /** If activity is created from fresh **/
        else {
            Bundle args = new Bundle();
            _calendar = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, month);
            args.putInt(CaldroidFragment.YEAR, _calendar.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            caldroidFragment.setArguments(args);
        }

        /** Attach to the activity **/
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.calendarViewFragment, caldroidFragment);
        fragmentTransaction.commit();

        /** Setup listener **/
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                String date_to_send = formatter.format(date);
                Log.d(TAG, "date is this : " + date_to_send);
                Date parsedDate = Calendar.getInstance().getTime();
                try {
                    parsedDate = formatter.parse(date_to_send);
                    Log.d(TAG, "Parsed Date: " + parsedDate.toString());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String selectedDate = parsedDate.toString();
                DayFragmentintent.putExtra(DATE_TAG, selectedDate);
                startActivity(DayFragmentintent);
            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                /*Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                /*Toast.makeText(getApplicationContext(),
                        "Long click " + formatter.format(date),
                        Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    /*Toast.makeText(getApplicationContext(),
                            "Caldroid view is created", Toast.LENGTH_SHORT)
                            .show();*/
                }
            }

        };

        /*Drawable drawable = getResources().getDrawable(R.drawable.red_border);
        Date currentDate = _calendar.getTime();
        caldroidFragment.setBackgroundDrawableForDate(drawable, currentDate)*/;

        /** Setup Caldroid **/
        caldroidFragment.setCaldroidListener(listener);
        updateDatesWithDrawable();
        caldroidFragment.refreshView();

    }

    /**
     * Getting the number of Month from the Progress Bars in Second Analytic Fragment
     */
    public int getMonthNumber(String month) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        Date date = Calendar.getInstance().getTime();
        try {
            date = sdf.parse(month);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int monthInteger = cal.get(Calendar.MONTH);
        Log.d(TAG, "Month Integer is:" + monthInteger);
        return monthInteger;

    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, Constants.CALENDAR_SAVED_STATE_KEY);
        }
    }

    /**
     * iterate from first to current month and calls updateCalendar method to update calendar cell with their
     * respective background
     * TODO : a better approach to update drawable is needed
     */
    public void updateDatesWithDrawable() {
        Date date = Calendar.getInstance().getTime();
        String currentMonth = Utils.getMonthFromDate(date);
        Log.d(TAG, "current month" + currentMonth);
        int currentIntMonth = Integer.parseInt(currentMonth);

        for (int i = 1 ; i < currentIntMonth ; i++) {
            switch (i){
                case 1 : updateCalendar("2016-01-01", "2016-01-31"); break;
                case 2 : updateCalendar("2016-02-01", "2016-02-28"); break;
                case 3 : updateCalendar("2016-03-01", "2016-03-31"); break;
                case 4 : updateCalendar("2016-04-01", "2016-04-30"); break;
                case 5 : updateCalendar("2016-05-01", "2016-05-31"); break;
                case 6 : updateCalendar("2016-06-01", "2016-06-30"); break;
                case 7 : updateCalendar("2016-07-01", "2016-07-31"); break;
                case 8 : updateCalendar("2016-08-01", "2016-08-31"); break;
                case 9 : updateCalendar("2016-09-01", "2016-09-30"); break;
                case 10 : updateCalendar("2016-10-01", "2016-10-31"); break;
                case 11 : updateCalendar("2016-11-01", "2016-11-30"); break;
                case 12 : updateCalendar("2016-12-01", "2016-12-31"); break;
            }
        }


    }

    /**
     * iterate through all the dates of month and update each calendar cell with
     * respective background
     *
     * @param startDate
     * @param endDate
     */
    public void updateCalendar(String startDate, String endDate){
        ColorDrawable green = new ColorDrawable(Color.GREEN);
        ColorDrawable red = new ColorDrawable(Color.RED);
        int status;
        String theDay, theMonth, theYear;
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date janStartDate = format.parse(startDate);
            Date janEndDate = format.parse(endDate);
            Calendar start = Calendar.getInstance();
            start.setTime(janStartDate);
            Calendar end = Calendar.getInstance();
            end.setTime(janEndDate);

            for (Date dat = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                // Do your job here with `date`.
                Log.v(TAG, "months dates are " + String.valueOf(date));
                theDay = Utils.getDayFromDate(date);
                theMonth = Utils.getMonthNameFromDate(date);
                theYear = Utils.getYearFromDate(date);
                Log.v(TAG, "the dates in format - " + theDay + theMonth + theYear);
                status=dbSQLH.isEntered(Integer.parseInt(theDay),getMonthNumber(theMonth),Integer.parseInt(theYear));
                Log.v(TAG, "status = " + String.valueOf(status));

                switch (status){
                    case 0 : caldroidFragment.setBackgroundDrawableForDate(green, date); caldroidFragment.refreshView(); break;
                    case 1 : caldroidFragment.setBackgroundDrawableForDate(red, date); caldroidFragment.refreshView(); break;
                    //case 2 : caldroidFragment.setBackgroundDrawableForDate(selector, date); break;
                }
                caldroidFragment.refreshView();
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDatesWithDrawable();
        caldroidFragment.refreshView();
    }
}
