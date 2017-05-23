package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.interfaces.GetUserCallback;
import com.peacecorps.malaria.model.AppUserModel;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.utils.ServerRequests;
import com.peacecorps.malaria.utils.UtilityMethods;

/**
 * Created by yatna on 2/7/16.
 */
public class UserProfile extends Activity{
    private EditText userNameEt;
    private EditText userEmailEt;
    private EditText userAgeEt;
    private EditText userMedicineTypeEt;
    private Button saveData;
    private Button homeIconButton;
    private Button btnTripIndicator;
    private Button infoHub;
    private Button newHomeButton;
    private String userMedicineType;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        userNameEt = (EditText)findViewById(R.id.user_name);
        userEmailEt = (EditText)findViewById(R.id.user_email);
        userAgeEt = (EditText)findViewById(R.id.user_age);
        userMedicineTypeEt = (EditText)findViewById(R.id.user_medicine_type);
        saveData = (Button)findViewById(R.id.user_profile_save);

        //footer buttons
        homeIconButton = (Button) findViewById(R.id.homeButton);
        btnTripIndicator = (Button) findViewById(R.id.tripButton);
        infoHub = (Button) findViewById(R.id.infoButton);
        newHomeButton = (Button)findViewById(R.id.tempButton);
        homeIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        btnTripIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), TripIndicatorFragmentActivity.class));
                finish();
            }
        });
        infoHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication().getApplicationContext(), InfoHubFragmentActivity.class));
                finish();
            }
        });
        newHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewHomeActivity.class));
                finish();
            }
        });
        //footer ends


        getPreviousDetails();
        saveData.setOnClickListener(saveDataSetOnClickListener());
        //check when age is entered
        userAgeEt.addTextChangedListener(new TextWatcher() {
            int age;
            String lastAge;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastAge = s.toString();
                age = 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 2){
                    userAgeEt.setError(getString(R.string.age_limit_exceeded));
                    userAgeEt.setText(lastAge);
                }
                if (s.length() != 0 && Integer.parseInt(s.toString()) == 0){
                    userAgeEt.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    //fetch previously entered details if any
    private void getPreviousDetails(){
        userMedicineType = SharedPreferenceStore.mPrefsStore.getString("com.peacecorps.malaria.drugPicked", null);
        userMedicineTypeEt.setText(userMedicineType);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString("user_name","");
        String userEmail = sharedPreferences.getString("user_email","");
        int userAge = sharedPreferences.getInt("user_age",0);
        userNameEt.setText(userName);
        userEmailEt.setText(userEmail);
        if (userAge == 0) {
            userAgeEt.setText("");
        }
        else {
            userAgeEt.setText("" + userAge);
        }
    }
    //save new values to shared preferences
    private void setNewDetails(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        editor.putString("user_name",userNameEt.getText().toString());
        editor.putString("user_email", userEmailEt.getText().toString());
        editor.putInt("user_age", Integer.parseInt(userAgeEt.getText().toString()));
        editor.commit();
    }

    //Implement the save button
    private View.OnClickListener saveDataSetOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userNameEt.getText().toString();
                String email = userEmailEt.getText().toString();
                String age = userAgeEt.getText().toString();


                if(name.trim().equals("")){
                    userNameEt.setError("Name required");
                }
                else if(email.trim().equals("") || !UtilityMethods.validEmail(email)){
                    userEmailEt.setError("Valid Email required");
                }
                else if(age.trim().equals("") || age.matches("[0]+")){
                    userAgeEt.setError("Age required");
                }
                else{
                    //create object to send
                    AppUserModel user = new AppUserModel();
                    //get medicine type from shared preferences
                    user = user.getAppUser(name,email, Integer.parseInt(age),userMedicineType);
                    postUserDetails(user);
                }

            }
        };
    }
    //create the server request
    private void postUserDetails(AppUserModel user){
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(String status) {
                if(status.equals("200")){
                    setNewDetails();
                    Toast.makeText(UserProfile.this, "User Details submitted", Toast.LENGTH_SHORT).show();
                    UserProfile.this.finish();
                }
                else{
                    Toast.makeText(UserProfile.this, "Failed! Please try again after some time.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}