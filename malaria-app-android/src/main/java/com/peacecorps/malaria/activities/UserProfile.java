package com.peacecorps.malaria.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.model.SharedPreferenceStore;
import com.peacecorps.malaria.utils.UtilityMethods;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;

/**
 * Created by yatna on 2/7/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UserProfile extends Activity implements LoaderManager.LoaderCallbacks <String> {
    private EditText userNameEt;
    private EditText userEmailEt;
    private EditText userAgeEt;
    private EditText userMedicineTypeEt;
    private ProgressDialog progressDialog;
    private String userMedicineType;
    private SharedPreferences sharedPreferences;
    private static final int UPDATE_USER_LOADER = 888;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://pc-web-dev.systers.org/api/malaria_users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        userNameEt = (EditText)findViewById(R.id.user_name);
        userEmailEt = (EditText)findViewById(R.id.user_email);
        userAgeEt = (EditText)findViewById(R.id.user_age);
        userMedicineTypeEt = (EditText)findViewById(R.id.user_medicine_type);
        Button saveData = (Button) findViewById(R.id.user_profile_save);

        //footer buttons
        Button homeIconButton = (Button) findViewById(R.id.homeButton);
        Button btnTripIndicator = (Button) findViewById(R.id.tripButton);
        Button infoHub = (Button) findViewById(R.id.infoButton);
        Button newHomeButton = (Button) findViewById(R.id.tempButton);
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
            userAgeEt.setText(userAge);
        }
    }
    //save new values to shared preferences
    private void setNewDetails(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name",userNameEt.getText().toString());
        editor.putString("user_email", userEmailEt.getText().toString());
        editor.putInt("user_age", Integer.parseInt(userAgeEt.getText().toString()));
        editor.apply();
    }

    //Implement the save button
    private View.OnClickListener saveDataSetOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = userNameEt.getText().toString();
                String email = userEmailEt.getText().toString();
                String age = userAgeEt.getText().toString();

                if (name.trim().equals("")) {
                    userNameEt.setError("Name required");
                    return;
                }
                if (email.trim().equals("") || !UtilityMethods.validEmail(email)) {
                    userEmailEt.setError("Valid Email required");
                    return;
                }
                if (age.trim().equals("") || age.matches("[0]+")) {
                    userAgeEt.setError("Age required");
                    return;
                }

                postUserDetails(name, email, age, userMedicineType);
            }
        };
    }
    //create the server request TODO change this
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void postUserDetails(String name, String email, String age, String userMedicineType){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        Bundle userDetails = new Bundle();
        userDetails.putString("name", name);
        userDetails.putString("email", email);
        userDetails.putString("age", age);
        userDetails.putString("medicine", userMedicineType);
        LoaderManager loaderManager = getLoaderManager();
        // COMPLETED (22) Get our Loader by calling getLoader and passing the ID we specified
        Loader<String> updateUserLoader = getLoaderManager().getLoader(UPDATE_USER_LOADER);
        // COMPLETED (23) If the Loader was null, initialize it. Else, restart it.
        if (updateUserLoader == null) {
            loaderManager.initLoader(UPDATE_USER_LOADER, userDetails, this);
        } else {
            loaderManager.restartLoader(UPDATE_USER_LOADER, userDetails, this);
        }


    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                progressDialog.show();
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                //add details to send
                ArrayList<NameValuePair> dataToSend = new ArrayList<>();
                dataToSend.add(new BasicNameValuePair("name", args.getString("name")));
                dataToSend.add(new BasicNameValuePair("email", args.getString("email")));
                dataToSend.add(new BasicNameValuePair("age", args.getString("age")));
                dataToSend.add(new BasicNameValuePair("medicine", args.getString("medicine")));

                HttpParams httpRequestParams = getHttpRequestParams();
                HttpClient client = new DefaultHttpClient(httpRequestParams);
                HttpPost post = new HttpPost(SERVER_ADDRESS);
                String status="";
                try {
                    post.setEntity(new UrlEncodedFormEntity(dataToSend));
                    HttpResponse response=client.execute(post);
                    status=response.getStatusLine().getStatusCode()+"";
                    Log.d("MyResponseCode ", "-> "+status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return status;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        progressDialog.dismiss();
        if (data.equals("200")){
            setNewDetails();
            Toast.makeText(UserProfile.this, "User Details submitted", Toast.LENGTH_SHORT).show();
            UserProfile.this.finish();
        }
        else {
            Toast.makeText(UserProfile.this, "Failed! Please try again after some time.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private HttpParams getHttpRequestParams() {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpRequestParams,
                CONNECTION_TIMEOUT);
        return httpRequestParams;
    }
}