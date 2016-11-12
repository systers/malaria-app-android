package com.peacecorps.malaria.activities;

/**
 * Created by Ankita on 8/3/2015.
 */
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.db.DatabaseSQLiteHelper;
import com.peacecorps.malaria.model.SharedPreferenceStore;

import java.util.ArrayList;

/**
 * This Dialog is the Location History Dialog
 */
public class TripIndicatorDialogActivity extends ListActivity {

    private DatabaseSQLiteHelper sqlite;
    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> list = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    private SimpleCursorAdapter dataAdapter;

    private String location="";

    public final static String LOCATION_TAG = "com.peacecorps.malaria.tripIndicator.LOCATION";

    static SharedPreferenceStore mSharedPreferenceStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_location_dialog);

        mSharedPreferenceStore = new SharedPreferenceStore();
        mSharedPreferenceStore.getSharedPreferences(this);

        sqlite = new DatabaseSQLiteHelper(this);
         //fetching location for location history
        Cursor cursor= sqlite.getLocation();

        /** Columns to be Shown in The ListView **/
        String[] columns = {sqlite.KEY_ROW_ID,sqlite.LOCATION};

        /**XML Bound Views according to the Column**/
        int[] to = new int[] {
                R.id.locationListItemNumber,R.id.locationListItem
        };

        /** Create the adapter using the cursor pointing to the desired row in query
         * made to database ,as well as the layout information**/
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.trip_location_list_item,
                cursor,
                columns,
                to,
                1);

        ListView listView = (ListView) findViewById(android.R.id.list);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setMinimumHeight(30);
        listView.setDividerHeight(1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                location= cursor.getString(cursor.getColumnIndexOrThrow("Location"));

                Toast.makeText(getApplicationContext(),
                        location, Toast.LENGTH_SHORT).show();

                mSharedPreferenceStore.mEditor.putString("com.peacecorps.malaria.TRIP_LOCATION", location).commit();

                TripIndicatorFragmentActivity.locationSpinner.setText(location);

                Intent intent = new Intent(getApplication(),TripIndicatorFragmentActivity.class);

                intent.putExtra(LOCATION_TAG, location);

                startActivity(intent);

                TripIndicatorDialogActivity.this.finish();

            }
        });




    }




}