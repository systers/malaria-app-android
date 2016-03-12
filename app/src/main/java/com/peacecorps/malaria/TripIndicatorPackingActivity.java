package com.peacecorps.malaria;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Ankita on 8/5/2015.
 */
public class TripIndicatorPackingActivity extends Activity {

    private long mNumDrugs=0;
    TextView numDrugs;
    ListView listView;
    EditText cash;
    TextView whichDrug;
    public static String tripDrugName;

    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> list = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> adapter;

    private SharedPreferenceStore mSharedPreferenceStore;

    private String [] outputStrArr;

    private Intent bkIntent;

    ListView dialog_listView;

    String[] listContent = {"Malarone","Doxycycline","Mefloquine"};
    Integer[] imageId = {R.drawable.mal,R.drawable.doxy,R.drawable.mef};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DatabaseSQLiteHelper sqLite = new DatabaseSQLiteHelper(this);

        /** Setting a custom layout for the list activity */
        setContentView(R.layout.trip_indicator_packing_dialog);

        /** Reference to the button of the layout main.xml */
        ImageButton btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        ImageButton btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        ImageButton btnSubmit = (ImageButton) findViewById(R.id.btnSubmit);

        /** Cash Data**/
        cash=(EditText)findViewById(R.id.cash_et);

        /** List View **/
        listView = (ListView)findViewById(R.id.listV);

        /**Populating the List **/
        Cursor cursor = sqLite.getPackingItem();
        String item="";


        while (cursor.moveToNext())
        {
            try {
                item = cursor.getString(cursor.getColumnIndex("PackingItem"));
                list.add(item);
            }
            catch (Exception e)
            {
                item="";
            }
        }

        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<String>(this,R.layout.trip_packing_item, list);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        bkIntent = new Intent(getApplicationContext(),
                TripIndicatorFragmentActivity.class);


        /** Defining a click event listener for the button "Add" */
        View.OnClickListener listenerAdd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText) findViewById(R.id.packing_et);
                list.add(edit.getText().toString());
                sqLite.insertPackingItem(edit.getText().toString(),1,"no");
                edit.setText("");
                adapter.notifyDataSetChanged();
            }
        };

        /** Setting the event listener for the add button */
        btnAdd.setOnClickListener(listenerAdd);

        /** Setting the adapter to the ListView */
        listView.setAdapter(adapter);
		
		/** Setting the event listeners for the list view */
        addListViewListeners();

        View.OnClickListener listenerDelete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Getting the checked items from the listview **/
                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
                int itemCount = listView.getCount();

                for(int i=itemCount-1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                        adapter.remove(list.get(i));
                    }
                }
                checkedItemPositions.clear();
                adapter.notifyDataSetChanged();
            }
        };

        /** Setting the event listener for the add button **/
        btnDelete.setOnClickListener(listenerDelete);
        /**Setting the Growing List
         * And adding entries to the database
         */
        View.OnClickListener listenerSubmit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add item if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i))
                        selectedItems.add(adapter.getItem(position));
                }

                outputStrArr = new String[selectedItems.size()];
                //adding to database
                for (int i = 0; i < selectedItems.size(); i++) {
                    outputStrArr[i] = selectedItems.get(i);
                    sqLite.insertPackingItem(outputStrArr[i],1,"yes");
                }

                getSharedPreferences();

                mSharedPreferenceStore.mEditor.putInt("Array Size", outputStrArr.length);

                for(int i=0;i<outputStrArr.length;i++)
                    mSharedPreferenceStore.mEditor.putString(outputStrArr + "_" + i, outputStrArr[i]).commit();

                /** Create a bundle object**/
                Bundle b = new Bundle();
                b.putStringArray("selectedItems", outputStrArr);

                /**Add the bundle to the intent.**/
                bkIntent.putExtras(b);
                //.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                int cashV=0;
                try {
                    cashV=Integer.parseInt(cash.getText().toString());
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Enter Integral Value of Cash!",Toast.LENGTH_SHORT);
                }


                sqLite.insertPackingItem("Cash", cashV ,"yes");

                startActivity(bkIntent);

                TripIndicatorPackingActivity.this.finish();

            }
        };

        /** Setting the event listener for the add button **/
        btnSubmit.setOnClickListener(listenerSubmit);


        /**Getting the Identity of Pill**/
        Intent intent = getIntent();
        mNumDrugs=intent.getLongExtra(TripIndicatorFragmentActivity.DRUG_TAG,0);
        numDrugs = (TextView)findViewById(R.id.quantity);
        whichDrug = (TextView)findViewById(R.id.drugName);
        numDrugs.setText("" + mNumDrugs);
        sqLite.insertPackingItem("Pills", (int) mNumDrugs, "yes");

        /** Drug Selection **/
        whichDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });


        tripDrugName=whichDrug.getText().toString();


    }
	
    private void addListViewListeners() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //Update on a scroll, as viewable children may change
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                updateChildBackgrounds();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Update on an item click, as colours change
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateChildBackgrounds();
            }
        });
    }
	
    //Updates in view checked items to green, and unchecked items to red
    private void updateChildBackgrounds() {
        SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
        for (int i = 0 ; i < listView.getChildCount() ; i++) {
            int actualPosition = listView.getFirstVisiblePosition() + i;
            if (checkedItemPositions.get(actualPosition)) {
                listView.getChildAt(i).setBackgroundResource(R.color.light_green);
            } else {
                listView.getChildAt(i).setBackgroundResource(R.color.light_red);
            }
        }
    }
    
    private void getSharedPreferences() {
        // reading the application SharedPreferences for storing of time and
        // drug selected
        mSharedPreferenceStore.mPrefsStore = getSharedPreferences(
                "com.peacecorps.malaria.packingItem", Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
         /**Dialog for Selecting the Drug**/
        final Dialog dialog=new Dialog(TripIndicatorPackingActivity.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setContentView(R.layout.trip_item_dropdown_list);
        dialog.setTitle("Select Drugs");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog_listView=(ListView)dialog.findViewById(R.id.tripDrugDialogList);
        DrugArrayAdapter adapter = new DrugArrayAdapter(this,listContent,imageId,null);
        dialog_listView.setAdapter(adapter);
        dialog_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //setting the text to the drug selected
                whichDrug.setText(parent.getItemAtPosition(position).toString());
                tripDrugName=parent.getItemAtPosition(position).toString();
                TripIndicatorFragmentActivity.packingSelect.setText(mNumDrugs + " " + tripDrugName + " etc.");
                dialog.dismiss();
            }
        });
            return dialog;

    }





}
