package com.peacecorps.malaria;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by Ankita on 6/8/2015.
 */
public class EffectivenessFragmentActivity extends FragmentActivity{

    private TextView mEffectivenessLabel,mETitle;

    private static String TAGE = EffectivenessFragmentActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    //json object response url
    private String urlJsonObj = "http://pc-web-dev.systers.org/api/posts/6/?format=json";

    // temporary string to show the parsed response
    private String jsonResponse;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.effectiveness_fragment);

        mEffectivenessLabel = (TextView) findViewById(R.id.effectivenessLabel);
        mETitle = (TextView)findViewById(R.id.eam);
        //"Please Wait..." Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        //mking json object Request
        Log.i(TAGE, "INSIDE EFFECTIVENESS");
        mEffectivenessLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/garreg.ttf"));
        mETitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/garreg.ttf"));
        makeJsonObjectRequest();
    }
    private void makeJsonObjectRequest(){
        Log.i(TAGE, "INSIDE JSON OBJECT REQUEST");
        showpDialog(); //progress dialog shows loading...while the data is being fetched

        //making a authenticated JSON Object  Request below
        AuthJSONObjectRequest jsonObjReq = new AuthJSONObjectRequest(Request.Method.GET, urlJsonObj, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAGE, response.toString()+"....!");

                try {
                    Log.i(TAGE,"INSIDE JSON RESPONSE");
                    //parsing json object response
                    String name = response.getString("title_post");
                    String desc = response.getString("description_post");

                    jsonResponse = "";
                    //jsonResponse += "Post: \n" + name + "\n\n";
                    jsonResponse += /*"Description: \n" +*/ desc + "\n\n";


                    mEffectivenessLabel.setText(jsonResponse);

                    String content = mEffectivenessLabel.getText().toString();
                    File file;
                    FileOutputStream outputStream;
                    try {
                        file = new File(getCacheDir(), "ECache");

                        outputStream = new FileOutputStream(file);
                        outputStream.write(content.getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAGE, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error Retreiving Data! Loading from cache... ", Toast.LENGTH_LONG).show();
                //cache implementation
                BufferedReader input = null;
                File file = null;
                try {
                    file = new File(getCacheDir(), "ECache"); // Pass getFilesDir() and "MyFile" to read file

                    input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = input.readLine()) != null) {
                        buffer.append(line);
                    }
                    mEffectivenessLabel.setText(buffer.toString());
                    Log.d(TAGE, buffer.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //hide progress dialog
                hidepDialog();
            }
        });

        //Adding Request to request queue
        VolleyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showpDialog(){
        if(!progressDialog.isShowing())
            progressDialog.show();
        //for showing the loading animation in Activity
    }

    private void hidepDialog(){
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        //for dismissing the loading animation in Activity
    }




}
