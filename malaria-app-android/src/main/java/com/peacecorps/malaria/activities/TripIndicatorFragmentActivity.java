package com.peacecorps.malaria.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.peacecorps.malaria.R;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.reciever.TripAlarmReceiver;
import com.peacecorps.malaria.widget.TripAppWidgetProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ankita on 7/3/2015.
 */

/**
 * Activity fro Plan My Trip Main Screen
 */
public class TripIndicatorFragmentActivity extends FragmentActivity {
    //declaring views
    public boolean sent;
    private Button btnInfoHub, btnHome,btnGenerate,btnGear,newHome, userProfile;
    private String mDrugPicked,mLocationPicked;
    public static String mDatesPicked;
    private TextView dateData,monthData,yearData,DepartureDateData,DepartureMonthData,DepartureYearData;
    public static AutoCompleteTextView locationSpinner;
    public static boolean[] checkSelected;
    private ArrayList<String> items;
    public boolean arriv,depar;
    static SharedPreferenceStore mSharedPreferenceStore;
    private Dialog dialog = null;
    private ImageView loc_history;
    public static TextView packingSelect;
    public TextView departureMonth, arrivalMonth;
    public static final String DRUG_TAG="com.peacecorps.malaria.activites.TripIndicatorFragmentActivity.DRUG_TAG";
    long num_drugs=0;
    private String arrival_formattedate, departure_formattedate;
    private String TAGTIFA="Trip Indicator Activity";
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int dep_year,dep_month,dep_day;
    private static TripIndicatorFragmentActivity inst;
    private int ALARM_HOUR=22, ALARM_MINUTE=0, ALARM_SECONDS=0;
    private TextView tripTime;
    private DatabaseSQLiteHelper sqLite, location_sqLite;
    private String loc="";
    private TimePicker tp;
    private View v;
    private TextView pmtLabel;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public Calendar departureDate;

    public static TripIndicatorFragmentActivity instance(){
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting Up Views
        setContentView(R.layout.tripindicator_layout);
        btnInfoHub=(Button)findViewById(R.id.infoButton);
        btnHome=(Button)findViewById(R.id.homeButton);
        locationSpinner=(AutoCompleteTextView)findViewById(R.id.trip_location_select_editText);
        btnGenerate=(Button)findViewById(R.id.generateButton);
        btnGear=(Button)findViewById(R.id.trip_settings_button);
        packingSelect=(TextView)findViewById(R.id.tripSelectBox);
        ((TextView)findViewById(R.id.tripSelectBox)).requestFocus();
        loc_history=(ImageView)findViewById(R.id.locationHistory);
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        tripTime = (TextView)findViewById(R.id.trip_time);
        ((TextView)findViewById(R.id.trip_time)).requestFocus();

        //Getting details of the saved trip.
        SharedPreferences prefs = getSharedPreferences(getString(R.string.trip_pref), MODE_PRIVATE);
        locationSpinner.setText(prefs.getString(getString(R.string.trip_pref_destination), ""));
        ((TextView)findViewById(R.id.trip_month_departure)).setText(prefs.getString(getString(R.string.trip_pref_dep_month), ""));
        ((TextView)findViewById(R.id.trip_date_departure)).setText(prefs.getString(getString(R.string.trip_pref_dep_date), ""));
        ((TextView)findViewById(R.id.trip_year_departure)).setText(prefs.getString(getString(R.string.trip_pref_dep_year), ""));
        ((TextView)findViewById(R.id.trip_month)).setText(prefs.getString(getString(R.string.trip_pref_arr_month), ""));
        ((TextView)findViewById(R.id.trip_date)).setText(prefs.getString(getString(R.string.trip_pref_arr_date), ""));
        ((TextView)findViewById(R.id.trip_year)).setText(prefs.getString(getString(R.string.trip_pref_arr_year), ""));
        packingSelect.setText(prefs.getString(getString(R.string.trip_pref_packed_items), ""));
        tripTime.setText(prefs.getString(getString(R.string.trip_pref_reminder_time), ""));

        //Setting departure and arrival date formats if trip details are present.
        if (!(prefs.getString("reminderTime", "").equals(""))) {
            departure_formattedate = prefs.getString("departure_date", "") + "/" + prefs.getString("departure_month", "") + "/"+ prefs.getString("departure_year", "");
            arrival_formattedate = prefs.getString("arrival_date", "") + "/" + prefs.getString("arrival_month", "") + "/" + prefs.getString("arrival_year", "");
        }

        //implementing the new Home button
        newHome =(Button)findViewById(R.id.tempButton);
        newHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), NewHomeActivity.class), 1);
                finish();
            }
        });

        //implementing the user's profile button
        userProfile =(Button)findViewById(R.id.userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                finish();
            }
        });


        pmtLabel = (TextView)findViewById(R.id.pmt);
        departureMonth=(TextView)findViewById(R.id.trip_month_departure);
        ((TextView)findViewById(R.id.trip_month_departure)).requestFocus();
        arrivalMonth=(TextView)findViewById(R.id.trip_month);
        ((TextView)findViewById(R.id.trip_month)).requestFocus();

        //setting fonts
        Typeface cf = Typeface.createFromAsset(getAssets(),"fonts/garreg.ttf");
        pmtLabel.setTypeface(cf);

        sqLite = new DatabaseSQLiteHelper(this);

        mSharedPreferenceStore = new SharedPreferenceStore();
        mSharedPreferenceStore.getSharedPreferences(this);

        //shared preference for viewing upcoming reminders
        preferences = getSharedPreferences("WidgetReminder", Context.MODE_PRIVATE);
        editor = preferences.edit();

        addListeners();
        createSelectionSpinners();

        Intent intent = getIntent();

        mLocationPicked=intent.getStringExtra(TripIndicatorDialogActivity.LOCATION_TAG);

        //fetching location
        try
        {
            mLocationPicked=SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.TRIP_LOCATION","");
            //locationSpinner.setText(mLocationPicked);
        }
        catch(Exception e)
        {
           // locationSpinner.setText("");
            Log.d(TAGTIFA,"Setting in onCreate");
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.TRIP_LOCATION",mLocationPicked)
                !=null) {
            SharedPreferenceStore.mPrefsStore.getString(
                    "com.peacecorps.malaria.TRIP_LOCATION", mLocationPicked);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.TRIP_LOCATION",
                mLocationPicked)!=null) {
            SharedPreferenceStore.mPrefsStore.getString(
                    "com.peacecorps.malaria.TRIP_LOCATION", mLocationPicked);
          //  locationSpinner.setText(mLocationPicked);
            Log.d(TAGTIFA, "Setting in onResume");


        }

    }

    @Override
    protected void onPause() {

        super.onPause();
        if (SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.TRIP_LOCATION",
                mLocationPicked)!=null) {
            SharedPreferenceStore.mPrefsStore.getString(
                    "com.peacecorps.malaria.TRIP_LOCATION",mLocationPicked);
        }

    }

    /*
    Adding Listeners of Each Button, Generate ,Packing List and Location History Too.
     */
    public void addListeners(){

        btnGear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                addDialog();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        btnInfoHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), InfoHubFragmentActivity.class));
                finish();
            }
        });


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Bundle b = getIntent().getExtras();
                String[] resultArr = b.getStringArray("selectedItems");*/
                departureMonth.setError(null);
                arrivalMonth.setError(null);
                packingSelect.setError(null);
                tripTime.setError(null);

               if("".equals(locationSpinner.getText().toString().trim()))
               {
                   Toast.makeText(getApplicationContext()," Location Missing ",Toast.LENGTH_SHORT).show();
               }
               else if(tripTime.getText().toString().equals(""))
               {
                   Toast.makeText(getApplicationContext()," Remainder Time Missing ",Toast.LENGTH_SHORT).show();
               }
               else if(packingSelect.getText().toString().equals(""))
               {
                   Toast.makeText(getApplicationContext()," Packing List Missing ",Toast.LENGTH_SHORT).show();
               }
               else if(departureMonth.getText().toString().equals(""))
               {
                   Toast.makeText(getApplicationContext()," Departure Date Missing ",Toast.LENGTH_SHORT).show();
               }
               else if(arrivalMonth.getText().toString().equals(""))
               {
                   Toast.makeText(getApplicationContext()," Arrival Date Missing ",Toast.LENGTH_SHORT).show();
               }
                else if(packingSelect.getText().toString().equals(""))
               {
                   packingSelect.setError("Field cannot be left blank.");
               }
               else if(tripTime.getText().toString().equals(""))
               {
                   tripTime.setError("Field cannot be left blank.");
               }

                else
               {
                   String chklist="",item="";
                   int q=0;

               /* for(int i=0;i<resultArr.length;i++)
                    chklist=resultArr[i]+", ";*/

                   Cursor cursor = sqLite.getPackingItemChecked();

                   while (cursor.moveToNext())
                   {
                       q=cursor.getInt(cursor.getColumnIndex("Quantity"));
                       item=cursor.getString(cursor.getColumnIndex("PackingItem"));

                       chklist+=q+" "+item+" ";

                   }
                   mLocationPicked=locationSpinner.getText().toString().trim();
                   mDatesPicked = "Trip to " + mLocationPicked + " is scheduled for " + departure_formattedate + ".\n" +"Stay safe, don't forget to take your pills.";

                   //save mItemPicked to view reminder
                   editor.putString("view_upcoming_reminder",mDatesPicked);
                   editor.apply();

                   Calendar calendar = Calendar.getInstance();
                   Log.d(TAGTIFA, "Date:" + dep_year + " " + dep_month + " " + dep_day);
                   calendar.set(dep_year,dep_month,dep_day,ALARM_HOUR,ALARM_MINUTE,ALARM_SECONDS);
                   long deptime= calendar.getTimeInMillis();
                   long today= Calendar.getInstance().getTimeInMillis();
                   long interval=0;
                   Log.d(TAGTIFA,"Dep Time:"+deptime);
                   Log.d(TAGTIFA, "Today: " + today);
                   if(deptime>today) {
                       //Saving the trip details.
                       SharedPreferences.Editor editTripDetails = getSharedPreferences(getString(R.string.trip_pref), MODE_MULTI_PROCESS).edit();
                       editTripDetails.putString(getString(R.string.trip_pref_destination), mLocationPicked);
                       editTripDetails.putString(getString(R.string.trip_pref_dep_month), ((TextView) findViewById(R.id.trip_month_departure)).getText().toString());
                       editTripDetails.putString(getString(R.string.trip_pref_dep_date), ((TextView) findViewById(R.id.trip_date_departure)).getText().toString());
                       editTripDetails.putString(getString(R.string.trip_pref_dep_year), ((TextView) findViewById(R.id.trip_year_departure)).getText().toString());
                       editTripDetails.putString(getString(R.string.trip_pref_arr_month), ((TextView) findViewById(R.id.trip_month)).getText().toString());
                       editTripDetails.putString(getString(R.string.trip_pref_arr_date), ((TextView) findViewById(R.id.trip_date)).getText().toString());
                       editTripDetails.putString(getString(R.string.trip_pref_arr_year), ((TextView) findViewById(R.id.trip_year)).getText().toString());
                       editTripDetails.putString(getString(R.string.trip_pref_packed_items), ((TextView) findViewById(R.id.tripSelectBox)).getText().toString());
                       editTripDetails.putString(getString(R.string.trip_pref_reminder_time), tripTime.getText().toString());
                       editTripDetails.apply();
                       interval = getTimeInterval(deptime, today);
                       /**
                        * Setting Up Alarm for a Week Before
                        * A day Before
                        * On day of Trip
                        */
                       long sevenDays = 6 * 24 * 60 * 60 * 1000;
                       long oneDay = 24 * 60 * 60 * 1000;
                       Log.d(TAGTIFA,"Alarm Interval: "+interval);
                       if (interval >= 7) {
                           Log.d(TAGTIFA, "Category 1 Alarm Set");
                           Intent myIntent1 = new Intent(TripIndicatorFragmentActivity.this, TripAlarmReceiver.class);
                           myIntent1.putExtra("AlarmID",101);
                           pendingIntent = PendingIntent.getBroadcast(TripIndicatorFragmentActivity.this, 101, myIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
                           alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - sevenDays, pendingIntent);

                           Intent myIntent2 = new Intent(TripIndicatorFragmentActivity.this, TripAlarmReceiver.class);
                           myIntent2.putExtra("AlarmID",102);
                           pendingIntent = PendingIntent.getBroadcast(TripIndicatorFragmentActivity.this, 102, myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
                           alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - oneDay, pendingIntent);

                           Intent myIntent3 = new Intent(TripIndicatorFragmentActivity.this, TripAlarmReceiver.class);
                           myIntent3.putExtra("AlarmID",103);
                           pendingIntent = PendingIntent.getBroadcast(TripIndicatorFragmentActivity.this, 103, myIntent3, PendingIntent.FLAG_UPDATE_CURRENT );
                           alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                           Toast.makeText(getApplicationContext(), "Reminders are Set!", Toast.LENGTH_LONG).show();
                           startActivity(new Intent(getApplication().getApplicationContext(), MainActivity.class));
                           finish();
                       } else if (interval < 7 && interval > 1) {

                           Log.d(TAGTIFA,"Category 2 Alarm Set");
                           Intent myIntent1 = new Intent(TripIndicatorFragmentActivity.this, TripAlarmReceiver.class);
                           myIntent1.putExtra("AlarmID",102);
                           pendingIntent = PendingIntent.getBroadcast(TripIndicatorFragmentActivity.this, 102, myIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
                           alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - oneDay, pendingIntent);
                           Intent myIntent2 = new Intent(TripIndicatorFragmentActivity.this, TripAlarmReceiver.class);
                           myIntent2.putExtra("AlarmID",103);
                           pendingIntent = PendingIntent.getBroadcast(TripIndicatorFragmentActivity.this, 103, myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
                           alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                           Toast.makeText(getApplicationContext(), "Reminders are Set!", Toast.LENGTH_LONG).show();
                           startActivity(new Intent(getApplication().getApplicationContext(), MainActivity.class));
                           finish();

                       } else {
                           Log.d(TAGTIFA,"Category 3 Alarm Set");
                           Intent myIntent = new Intent(TripIndicatorFragmentActivity.this, TripAlarmReceiver.class);
                           myIntent.putExtra("AlarmID",103);
                           pendingIntent = PendingIntent.getBroadcast(TripIndicatorFragmentActivity.this, 103, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                           alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                           Toast.makeText(getApplicationContext(), "Reminders are Set!", Toast.LENGTH_LONG).show();
                           startActivity(new Intent(getApplication().getApplicationContext(), MainActivity.class));
                           finish();
                       }

                       //update the widgets
                       int widgetIds[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), TripAppWidgetProvider.class));
                       TripAppWidgetProvider tripWidgets = new TripAppWidgetProvider();
                       tripWidgets.onUpdate(getApplicationContext(), AppWidgetManager.getInstance(getApplicationContext()),widgetIds);
                   }
                   else
                   {
                       Toast.makeText(getApplicationContext(),"Enter future departure time.", Toast.LENGTH_LONG).show();
                   }



                   loc = locationSpinner.getText().toString();
                   sqLite.insertLocation(loc);
               }


            }
        });

        //Opening the location history dialog
        loc_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplication(), TripIndicatorDialogActivity.class);

                DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getApplicationContext());


                startActivity(intent);

                //TripIndicatorFragmentActivity.this.finish();


            }


        });

        //opening the select packing dialog
        packingSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  if(departureMonth.getText().toString().equals("") || arrivalMonth.getText().toString().equals("") )
                {
                    Toast.makeText(getApplicationContext(),"Enter Departure Date and Arrival Date First ",Toast.LENGTH_SHORT).show();
                }
               else
                {
                    String date = new SimpleDateFormat("dd/MM/yy").format(new Date());
                    Date curr_date = getDateObj(date);
                    Date departure=getDateObj(departure_formattedate);
                    Date arrival=getDateObj(arrival_formattedate);
                    long curr_datel = curr_date.getTime();
                    long departurel = departure.getTime();
                    long arrivall = arrival.getTime();

                    if(departurel < curr_datel) {
                        Toast.makeText(getApplicationContext(),R.string.departuredate_currentdate,Toast.LENGTH_SHORT).show();

                    } else if(arrivall < departurel) {
                        Toast.makeText(getApplicationContext(),R.string.arrivaldate_departuredate,Toast.LENGTH_SHORT).show();

                    } else if(arrivall>=departurel) {
                        Intent intent = new Intent(getApplication(), TripIndicatorPackingActivity.class);
                        setNumDrugs(departure_formattedate, arrival_formattedate);
                        intent.putExtra(DRUG_TAG, num_drugs);
                        startActivity(intent);
                        packingSelect.setText(TripIndicatorPackingActivity.tripDrugName);
                    }
                }
            }
        });
    }

    private void createSelectionSpinners() {

        ArrayAdapter<CharSequence> drugAdapter = ArrayAdapter.createFromResource(
                this, R.array.drug_array,
                R.layout.trip_spinner_item);

        drugAdapter.setDropDownViewResource(R.layout.trip_spinner_popup_item);

        //Creating the autocomplete location spinner
        location_sqLite = new DatabaseSQLiteHelper(this);

        //Store all previously visited locations in a String array
        Cursor cursor = location_sqLite.getLocation();

        String[] location_names = new String[cursor.getCount()];

        int current_index = 0;

        while (cursor.moveToNext())
        {
            String location = cursor.getString(cursor.getColumnIndex("Location"));
            location_names[current_index] = location;
            current_index++;
        }

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                location_names);

        locationSpinner.setAdapter(locationAdapter);

    }
     //opening the reset dialog
    public void addDialog()
    {
        dialog = new Dialog(TripIndicatorFragmentActivity.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setContentView(R.layout.resetdata_dialog);
        //dialog.setTitle("Reset Data");

        //final RadioGroup btnRadGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupReset);
        Button btnOK = (Button) dialog.findViewById(R.id.dialogButtonOKReset);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             /*   // get selected radio button from radioGroup
                int selectedId = btnRadGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton btnRadButton = (RadioButton) dialog.findViewById(selectedId);

                String ch = btnRadButton.getText().toString();

                if (ch.equalsIgnoreCase("yes")) {
                    DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getApplicationContext());
                    sqLite.resetDatabase();
                    mSharedPreferenceStore.mEditor.clear().commit();
                    SharedPreferenceStore.mEditor.clear().commit();
                    startActivity(new Intent(getApplication().getApplicationContext(),
                            UserMedicineSettingsFragmentActivity.class));

                } else {
                    dialog.dismiss();
                }*/
                DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(getApplicationContext());
                sqLite.resetDatabase();
                mSharedPreferenceStore.mEditor.clear().commit();
                SharedPreferenceStore.mEditor.clear().commit();
                startActivity(new Intent(getApplication().getApplicationContext(),
                        UserMedicineSettingsFragmentActivity.class));
                dialog.dismiss();
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

    //finding the no. of drugs to be taken for trip
    public void setNumDrugs(String depart,String arrive){



        Date dep=getDateObj(depart);
        Date arri=getDateObj(arrive);
        if(dep!=null && arri!=null) {
            long depl = dep.getTime();

            long arrl = arri.getTime();

            int oneDay = 24 * 60 * 60 * 1000;

            num_drugs = ((arrl - depl) / oneDay)+1;
        }
        else
        {
            Log.d("TripIndicatorPacking","Date was not parsed properly!");
        }

}

    private Date getDateObj(String s){


        Date dobj=null;

        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        try {
            dobj= sdf.parse(s);
        }
        catch (ParseException e)
        {
            return null;

            //e.printStackTrace();
        }

        return dobj;
    }

    private void setDates()
    {
        //Setting Up the Date
        String date_data=dateData.getText().toString();
        String month_data=monthData.getText().toString();
        String year_data=yearData.getText().toString();
        arrival_formattedate=date_data+"/"+month_data+"/"+year_data;
        SharedPreferenceStore.mEditor.putString("com.peacecorps.malaria.trip_date", arrival_formattedate).commit();

        String departure_date_data=DepartureDateData.getText().toString();
        String departure_month_data=DepartureMonthData.getText().toString();
        String departure_year_data=DepartureYearData.getText().toString();
        departure_formattedate=departure_date_data+"/"+departure_month_data+"/"+departure_year_data;

        SharedPreferenceStore.mEditor.putString("com.peacecorps.malaria.departure_trip_date", departure_formattedate).commit();
        SharedPreferenceStore.mEditor.putString("com.peacecorps.malaria.trip_drug",mDrugPicked).commit();
        //SharedPreferenceStore.mEditor.putString("com.peacecorps.malaria.TRIP_LOCATION",mLocationPicked).commit();


    }

    /**Date Picker Dialog
     * For Arrival and Departure
     */
    public class DatePickerFragmentArrival extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = departureDate;
            c.add(Calendar.DATE, 1);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.MyDatePicker , this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            setTextFields(day,month+1,year);
            arriv=true;

            if (arriv && depar){

                setDates();
                setNumDrugs(departure_formattedate,arrival_formattedate);
                Log.d(TAGTIFA,"Inside Arrival");
            }
        }

        public void setTextFields(int date, int month,int year)
        {
            dateData=(TextView)findViewById(R.id.trip_date);
            monthData=(TextView)findViewById(R.id.trip_month);
            yearData=(TextView)findViewById(R.id.trip_year);

            dateData.setText(""+date);
            monthData.setText(""+month);
            yearData.setText(""+(year-2000));



        }
    }

    public void showDatePickerDialogArrival(View v) {
        if(!depar){
            Toast.makeText(getApplicationContext(), "Set departure date first.", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogFragment newFragment = new DatePickerFragmentArrival();
        newFragment.show(getFragmentManager(), "Arrival Data");
    }

    public class DatePickerFragmentDeparture extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            dep_year = c.get(Calendar.YEAR);
            dep_month = c.get(Calendar.MONTH);
            dep_day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.MyDatePicker , this, dep_year, dep_month, dep_day);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            setTextFields(day,month+1,year);
            departureDate = Calendar.getInstance();
            departureDate.set(year, month, day);

            depar=true;

            if (arriv && depar){

                setDates();
                setNumDrugs(departure_formattedate,arrival_formattedate);
                Log.d(TAGTIFA, "Inside Departure");

            }


        }
        /**Updating in Trip Indicator Activity**/
        public void setTextFields(int date, int month,int year)
        {
            DepartureDateData=(TextView)findViewById(R.id.trip_date_departure);
            DepartureMonthData=(TextView)findViewById(R.id.trip_month_departure);
            DepartureYearData=(TextView)findViewById(R.id.trip_year_departure);

            DepartureDateData.setText(""+date);
            DepartureMonthData.setText(""+month);
            DepartureYearData.setText(""+(year-2000));

        }
    }
    /*Showing the Dialogs*/
    public void showDatePickerDialogDeparture(View v) {
        DialogFragment newFragment = new DatePickerFragmentDeparture();
        newFragment.show(getFragmentManager(), "Departure Date");
    }
   //get time interval
    public long getTimeInterval(long t1,long t2)
    {
        long interval;
        long oneDay= 24*60*60*1000;
        if(t1>=t2)
            interval=(t1-t2)/oneDay;
        else
            interval=(t2-t1)/oneDay;

        return  interval+1;
    }

    public void getSharedPreferences() {
        mSharedPreferenceStore.mPrefsStore = getApplication()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                        Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }


    public class TimePickerDialogDeparture extends DialogFragment implements TimePickerDialog.OnTimeSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            TimePickerDialog view= new TimePickerDialog(getActivity(), R.style.MyTimePicker , this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

            LayoutInflater inflater = getActivity().getLayoutInflater();
            v=inflater.inflate(R.layout.time_picker_style, null);

            view.setView(v);
            tp=(TimePicker)v.findViewById(R.id.tpTrip);
            return view;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {



            ALARM_HOUR=hourOfDay;
            ALARM_MINUTE=minute;


            ALARM_HOUR=tp.getCurrentHour();
            ALARM_MINUTE=tp.getCurrentMinute();

            tripTime.setText("" + ALARM_HOUR + ":" + ALARM_MINUTE);

        }


    }

    public void showTimePickerDialogDeparture(View v)
    {
        DialogFragment newFragment = new TimePickerDialogDeparture();
        newFragment.show(getFragmentManager(),"Departure Time");
    }

}
