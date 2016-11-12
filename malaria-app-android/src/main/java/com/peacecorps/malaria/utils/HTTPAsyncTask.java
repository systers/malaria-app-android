package com.peacecorps.malaria.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ankita on 6/12/2015.
 */

/*
* Will be used for checking continuous connectivity of Info Hub with Internet
* */
public class HTTPAsyncTask extends AsyncTask<String, Void, String> {

    Context syncontext;
    private String TAGHAT = "HTTPAsyncTask: ";
    public HTTPAsyncTask(Context syncontext)
    {
        this.syncontext=syncontext;
    }

    @Override
    protected String doInBackground(String... urls) {
       Log.i(TAGHAT, "INSIDE DO IN BACKGROUND");
       if(!isConnectedToInternet()){
           publishProgress();
           return "Not Connected!";
       }
        publishProgress();
        return  "Connected!";
    }

    // onPostExecute displays the results of the AsyncTask.
    //@Override

    /*protected void onPostExecute(String result) {
        Log.i(TAGHAT,"INSIDE ON POST EXECUTE");
        Log.i("Status : ",result);*/



    @Override
    protected void onProgressUpdate(Void... values) {


    }

    public boolean isConnectedToInternet() {

        Log.i(TAGHAT,"INSIDE IS CONNECTED");
        ConnectivityManager connectivity = (ConnectivityManager)syncontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
                try {
                    URL url = new URL("http://pc-web-dev.systers.org");
                    HttpURLConnection urlc = (HttpURLConnection) url
                            .openConnection();
                    urlc.setConnectTimeout(4000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
        return false;
    }
}


