package com.peacecorps.malaria.utils;

/**
 * Created by Ankita on 6/8/2015.
 */
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthJSONObjectRequest extends JsonObjectRequest {
    /**Custom JSON Object Request For Webpage Which Requires Authentication**/
    public AuthJSONObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener,Response.ErrorListener errorListener) {

        super(method, url, jsonRequest, listener, errorListener);
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();
        /**Setting Up Password and Username for Posts at MACC**/
        String creds = String.format("%s:%s","TestUser","password");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
        Log.d("AUTHJSONObjectRequest",auth);
        params.put("Authorization", auth);
        return params;
    }



}