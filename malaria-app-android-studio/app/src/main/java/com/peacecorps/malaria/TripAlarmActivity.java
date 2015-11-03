package com.peacecorps.malaria;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ankita on 8/17/2015.
 */
public class TripAlarmActivity extends Activity {

    private SimpleCursorAdapter dataAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //views
        setContentView(R.layout.trip_alarm_layout);
        TextView textView = (TextView)findViewById(R.id.tripRemindingItems);
        Button btnOK = (Button)findViewById(R.id.reminderOK);
        Button btnCancel = (Button)findViewById(R.id.reminderCancel);

        //intent to get the Alarm ID
        Intent intent=this.getIntent();
        final int id=intent.getIntExtra("AlarmID",0);

        Log.d("TAA", "AlarmID: " + intent.getIntExtra("AlarmID", 0));

        String msg= "Your Luggage should contain following items:-";

        //setting fonts
        textView.setText(msg);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/garreg.ttf");
        textView.setTypeface(tf);

        final DatabaseSQLiteHelper sqlite = new DatabaseSQLiteHelper(this);

        Cursor cursor= sqlite.getPackingItemChecked();

        /** Columns to be Shown in The ListView **/
        String[] columns = {sqlite.KEY_ROW_ID,sqlite.PACKING_ITEM};

        /**XML Bound Views according to the Column**/
        int[] to = new int[] {
                R.id.reminderListItemNumber,R.id.reminderListItem
        };

        /** Create the adapter using the cursor pointing to the desired row in query
         * made to database ,as well as the layout information**/
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.trip_reminder_list_item,
                cursor,
                columns,
                to,
                1);

        ListView listView = (ListView) findViewById(android.R.id.list);
        /** Assign adapter to ListView **/
        listView.setAdapter(dataAdapter);
        listView.setMinimumHeight(30);
        listView.setDividerHeight(1);

        /**Snoozes Alarm if user clicks on Cancel**/
        Intent Pintent = new Intent(this,
                TripAlarmActivity.class);
        final PendingIntent pendingSnooze = PendingIntent.getActivity(
                this.getApplicationContext(), 103, Pintent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) this
                .getSystemService(this.ALARM_SERVICE);
        final Calendar calender = Calendar.getInstance();
        Date date = new Date();
        calender.setTime(date);
        calender.add(Calendar.MINUTE, 5);


        /**Ok Listener **/
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

                //id = intent.getIntExtra("AlarmID", 0);
                /**Refreshing the Packing List Status**/
                if(id==103)
                    sqlite.refreshPackingItemStatus();


            }
        });
        /**Cancel Listener**/
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //id = intent.getIntExtra("AlarmID", 0);
                /*Snoozing the Alarm**/
                if(id==103) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            calender.getTimeInMillis(), pendingSnooze);
                    Toast.makeText(getApplicationContext(),"Alarm Snoozed",Toast.LENGTH_LONG);

                }
                finish();
            }
        });

    }



}
