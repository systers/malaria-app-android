package com.peacecorps.malaria.activities;

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
import com.peacecorps.malaria.utils.AuthJSONObjectRequest;
import com.peacecorps.malaria.R;
import com.peacecorps.malaria.utils.VolleyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Ankita on 6/8/2015.
 */
public class SideEffectsNPCVFragmentActivity extends FragmentActivity {

    private TextView mSideEffectsNPCVLabel,sen;

    private static String TAGSEN = SideEffectsNPCVFragmentActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    //json object response url
    private String urlJsonObj = "http://pc-web-dev.systers.org/api/posts/4/?format=json";
    //private String urlJsonObj = "http://pc-web-dev.systers.org/api/malaria_posts";

    // temporary string to show the parsed response
    private String jsonResponse;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //views
        setContentView(R.layout.side_effects_npcv_fragment);

        mSideEffectsNPCVLabel = (TextView) findViewById(R.id.sideEffectsNPCVLabel);
        sen = (TextView) findViewById(R.id.sen);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

       //mking authenticated json object Request
        Log.i(TAGSEN, "INSIDE SIDE EFFECTS NPCV");
        makeJsonObjectRequest();
        //setting fonts
        mSideEffectsNPCVLabel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/garreg.ttf"));
        sen.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/garreg.ttf"));
    }
    private void makeJsonObjectRequest(){
        Log.i(TAGSEN, "INSIDE JSON OBJECT REQUEST");
        showpDialog(); //progress dialog shows loading...while the data is being fetched

        //making an authenticated JSON Object  Request below
        AuthJSONObjectRequest jsonObjReq = new AuthJSONObjectRequest(Request.Method.GET, urlJsonObj, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAGSEN, response.toString());

                try {
                    Log.i(TAGSEN,"INSIDE JSON RESPONSE");
                    //parsing json object response
                    String name = response.getString("title_post");
                    String desc = response.getString("description_post");

                    jsonResponse = "";
                    //jsonResponse += "Post: \n" + name + "\n\n";
                    jsonResponse += /*"Description: \n" +*/ desc + "\n\n";


                    mSideEffectsNPCVLabel.setText(jsonResponse);
                    //reading from cache if no connectivity

                    String content = mSideEffectsNPCVLabel.getText().toString();
                    File file;
                    FileOutputStream outputStream;
                    try {
                        // file = File.createTempFile("MyCache", null, getCacheDir());
                        file = new File(getCacheDir(), "SENCache");

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
                VolleyLog.d(TAGSEN, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error Retreiving Data! Loading from cache... ", Toast.LENGTH_LONG).show();
                //offline cache
                BufferedReader input = null;
                File file = null;
                try {
                    file = new File(getCacheDir(), "SENCache"); // Pass getFilesDir() and "MyFile" to read file

                    input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String line;
                    StringBuffer buffer = new StringBuffer();
                    while ((line = input.readLine()) != null) {
                        buffer.append(line);
			buffer.append("\n");
                    }
                    mSideEffectsNPCVLabel.setText(buffer.toString());
                    Log.d(TAGSEN, buffer.toString());
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
