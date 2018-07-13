package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.reciever.TripAlarmReceiver;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;

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
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mgr.cancel(1);

        //intent to get the Alarm ID
        Intent intent=this.getIntent();
        final int id=intent.getIntExtra("AlarmID",0);

        Log.d("TAA", "AlarmID: " + intent.getIntExtra("AlarmID", 0));

        String return_date = getApplication()
                .getSharedPreferences("com.peacecorps.malaria.storeTimePicked", Context.MODE_PRIVATE)
                .getString("com.peacecorps.malaria.trip_date", null);

        String msg= "Trip return date is : " + return_date + ".\n\n" + "Your Luggage should contain following items:-";

        //setting fonts
        textView.setText(msg);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/garreg.ttf");
        textView.setTypeface(tf);

        final DatabaseSQLiteHelper sqlite = new DatabaseSQLiteHelper(this);

        Cursor cursor= sqlite.getPackingItemChecked();

        /** Create the adapter using the cursor going through each row,
         * and concatenating the item name to the quantity **/

        String[] items_with_quantities = new String[cursor.getCount()];

        int current_index = 0;

        while (cursor.moveToNext())
        {
            String item = cursor.getString(cursor.getColumnIndex("PackingItem"));
            String quantity = cursor.getString(cursor.getColumnIndex("Quantity"));
            items_with_quantities[current_index] = item + "-" + quantity;
            current_index++;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this,
                R.layout.trip_reminder_list_item,
                items_with_quantities);

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
        calender.add(Calendar.MINUTE, 1);


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
                    Ringtone ringtone= TripAlarmReceiver.ringtone;
                    ringtone.stop();
                    Toast.makeText(getApplicationContext(),"Alarm Snoozed",Toast.LENGTH_LONG);

                }
                finish();
            }
        });

    }



}
