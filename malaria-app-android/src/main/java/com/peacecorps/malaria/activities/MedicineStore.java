package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.model.SharedPreferenceStore;

/**
 * Created by yatna on 15/6/16.
 */
public class MedicineStore extends Activity {
    private TextView medicineName;
    private TextView daysLeft;
    private Button addMedicine;
    private Button orderMedicine;
    private Button settings;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int medicineStore;
    private String drugName;
    private boolean isLimitExceede;
    static SharedPreferenceStore mSharedPreferenceStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_store);
        medicineName=(TextView)findViewById(R.id.medicine_name);
        daysLeft=(TextView)findViewById(R.id.days_left);
        addMedicine=(Button)findViewById(R.id.add_medicine);
        orderMedicine=(Button)findViewById(R.id.order_medicine);
        settings=(Button)findViewById(R.id.dialog_setting);
        addMedicine.setOnClickListener(addMedicinesOnClickListener());
        orderMedicine.setOnClickListener(orderMedicineOnClickListener());
        settings.setOnClickListener(settingsOnClickListener());
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferenceStore = new SharedPreferenceStore();
        mSharedPreferenceStore.getSharedPreferences(this);
        drugName=SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.drugPicked", null);
        medicineName.setText(drugName);
        displayMedicineStore();
    }
    private void displayMedicineStore(){
        medicineStore=preferences.getInt("medicineStore",0);
        //if drug is weekly
        if(drugName.compareTo("Mefloquine")==0){
            if(medicineStore>=0){
                daysLeft.setText(medicineStore+" Weeks");
            }
            else{
                medicineStore=medicineStore*-1;
                daysLeft.setText(medicineStore+" Weeks Due");
            }
        }
        //if drug is daily
        else{
            if(medicineStore>=0){
                daysLeft.setText(medicineStore+" Days");
            }
            else{
                medicineStore=medicineStore*-1;
                daysLeft.setText(medicineStore+" Days Due");
            }
        }
    }
    //implement order medicine button
    public View.OnClickListener orderMedicineOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog orderMedicineDialog = new Dialog(MedicineStore.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                orderMedicineDialog.setContentView(R.layout.order_medicine_dialog);

                // Setting Dialog Title
                Button email=(Button)orderMedicineDialog.findViewById(R.id.order_by_email);
                Button message=(Button)orderMedicineDialog.findViewById(R.id.order_by_msg);
                final EditText medicineQuantityEt=(EditText)orderMedicineDialog.findViewById(R.id.order_quantity);
                ((EditText)orderMedicineDialog.findViewById(R.id.order_quantity)).requestFocus();
                //implement the email button

                medicineQuantityEt.setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        try {
                            if (!medicineQuantityEt.getText().toString().equals("") &&
                                    Integer.parseInt(medicineQuantityEt.getText().toString().trim()) > 100) {
                                medicineQuantityEt.setError(getResources().getString(R.string.limit_exceeded));
                                isLimitExceede = true;
                            }
                            else {
                                isLimitExceede = false;
                            }
                        }
                        catch (NumberFormatException exception){
                            medicineQuantityEt.setError(getResources().getString(R.string.limit_exceeded));
                        }

                        return false;
                    }
                });

                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(medicineQuantityEt.getText().toString().trim().equals("")){
                            medicineQuantityEt.setError("Quantity Required");
                        }
                        else if(medicineQuantityEt.getText().toString().matches("[0]+")){
                            medicineQuantityEt.setError("Quantity Required");
                        }
                        else if(isLimitExceede){
                            medicineQuantityEt.setError(getResources().getString(R.string.limit_exceeded));
                        }
                        else{
                            //send and email
                            String msgBody="My malaria pills will last for the coming  "+"<b>"+medicineStore+"</b>"+" days only.<br> Send the following immediately: <br>" +
                                    "Medicine Name:     " +"<b>"+drugName+"</b>"+"<br>"+
                                    "Quantity:          " ;
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.setType("message/rfc822");
                            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"yatnavermaa@gmail.com", "yatna.verma.ece13@itbhu.ac.in"});
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "URGENT: Reqiured Malaria Medicines");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(msgBody+"<b>"+Integer.parseInt(medicineQuantityEt.getText().toString())+"</b>"));
                            startActivity(Intent.createChooser(emailIntent, "Send mail via..."));
                            orderMedicineDialog.dismiss();
                        }
                    }
                });
                //implement the message button
                message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(medicineQuantityEt.getText().toString().trim().equals("")){
                            medicineQuantityEt.setError("Quantity Required");
                        }
                        else if(medicineQuantityEt.getText().toString().matches("[0]+")){
                            medicineQuantityEt.setError("Quantity Required");
                        }
                        else if(isLimitExceede){
                            medicineQuantityEt.setError(getResources().getString(R.string.limit_exceeded));
                        }
                        else{
                            String msgBody="My malaria pills will last for the coming "+medicineStore+" days only.\n Send the following immediately: \n" +
                                    "Medicine Name:     " +drugName+"\n"+
                                    "Quantity:          " ;
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage("123", null,msgBody + Integer.parseInt(medicineQuantityEt.getText().toString()), null, null);
                            Toast.makeText(MedicineStore.this, "SMS sent.", Toast.LENGTH_SHORT).show();
                            orderMedicineDialog.dismiss();
                        }
                    }
                });

                // Showing Alert Message
                orderMedicineDialog.show();
            }
        };
    }
    //Implement the add medicine button
    public View.OnClickListener addMedicinesOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog addMedicineDialog = new Dialog(MedicineStore.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                addMedicineDialog.setContentView(R.layout.add_medicine_dialog);

                // Setting Dialog Title
                Button add=(Button)addMedicineDialog.findViewById(R.id.add_medicine_dialog);
                Button cancel=(Button)addMedicineDialog.findViewById(R.id.cancel_medicine_dialog);
                final EditText medicineQuantityEt=(EditText)addMedicineDialog.findViewById(R.id.add_medicine_quantity);
                ((EditText)addMedicineDialog.findViewById(R.id.add_medicine_quantity)).requestFocus();
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(medicineQuantityEt.getText().toString().trim().equals("")){
                            medicineQuantityEt.setError("Quantity Required");
                        }
                        else{
                            medicineStore=preferences.getInt("medicineStore",0);
                            editor=preferences.edit();
                            //get medicines already in store
                            int quantity = Integer.parseInt(medicineQuantityEt.getText().toString());
                            //add the new medicines to previous ones
                            editor.putInt("medicineStore", medicineStore + quantity);
                            editor.commit();
                            displayMedicineStore();
                            addMedicineDialog.dismiss();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addMedicineDialog.dismiss();
                    }
                });

                // Showing Alert Message
                addMedicineDialog.show();

            }
        };

    }
    //Implement the settings button
    public View.OnClickListener settingsOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog settingsDialog = new Dialog(MedicineStore.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
                settingsDialog.setContentView(R.layout.reminder_screen_setting_dialog);

                // Setting Dialog Title
                Button set=(Button)settingsDialog.findViewById(R.id.set);
                Button cancel=(Button)settingsDialog.findViewById(R.id.cancel);
                final EditText time=(EditText)settingsDialog.findViewById(R.id.time);
                ((EditText)settingsDialog.findViewById(R.id.time)).requestFocus();
                TextView warningTv=(TextView)settingsDialog.findViewById(R.id.warning_textview);
                //if drug is weekly
                if(drugName.compareTo("Mefloquine")==0){
                    warningTv.setText("How many weeks before your medicine ends, do you want to get the reminder? ");
                }
                //if drug is daily
                else{
                    warningTv.setText("How many days before your medicine ends, do you want to get the reminder? ");
                }
                //get previous value if any
                int prevAlertTime=preferences.getInt("alertTime",-1);
                //fill editText with previous set values
                if(prevAlertTime!=-1){
                    time.setText(prevAlertTime+"");
                }

                time.setOnKeyListener(new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        try {
                            if (!time.getText().toString().equals("") &&
                                    Integer.parseInt(time.getText().toString().trim()) > 100) {
                                time.setError(getResources().getString(R.string.limit_exceeded));
                                isLimitExceede = true;
                            }
                            else {
                                isLimitExceede = false;
                            }
                        }
                        catch (NumberFormatException exception) {
                            time.setError(getResources().getString(R.string.limit_exceeded));
                        }

                        return false;
                    }
                });
                set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(time.getText().toString().trim().equals("")){
                            //If nothing is entered display error
                            time.setError("Entry Required");
                        }
                        else if(isLimitExceede){
                            time.setError(getResources().getString(R.string.limit_exceeded));
                        }
                        else{
                            editor=preferences.edit();
                            int intTime = Integer.parseInt(time.getText().toString());
                            //Update shared preference with new value
                            editor.putInt("alertTime",intTime);
                            editor.commit();
                            settingsDialog.dismiss();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        settingsDialog.dismiss();
                    }
                });

                // Showing Alert Message
                settingsDialog.show();

            }
        };
    }
}
