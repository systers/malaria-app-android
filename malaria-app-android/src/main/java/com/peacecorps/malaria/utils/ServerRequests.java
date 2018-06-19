package com.peacecorps.malaria.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.peacecorps.malaria.interfaces.GetUserCallback;
import com.peacecorps.malaria.model.AppUserModel;

//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;

// TODO: Since servers are not working,http errors should be fixed after community's decision. Issue #281 ;)

/**
 * Created by yatna on 10/7/16.
 */
public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://pc-web-dev.systers.org/api/malaria_users/";

    //define a progress dialog on start
    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }

    public void storeUserDataInBackground(AppUserModel user, GetUserCallback userCallBack) {
        progressDialog.show();
       // new StoreUserDataAsyncTask(user, userCallBack).execute();
    }

//    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, String> {
//        AppUserModel user;
//        GetUserCallback userCallBack;
//
//        public StoreUserDataAsyncTask(AppUserModel user, GetUserCallback userCallBack) {
//            this.user = user;
//            this.userCallBack = userCallBack;
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            //add details to send
//            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
//            dataToSend.add(new BasicNameValuePair("name", user.getName()));
//            dataToSend.add(new BasicNameValuePair("email", user.getEmail()));
//            dataToSend.add(new BasicNameValuePair("age", user.getAge()+""));
//            dataToSend.add(new BasicNameValuePair("medicine", user.getMedicineType()));
//
//            HttpParams httpRequestParams = getHttpRequestParams();
//            HttpClient client = new DefaultHttpClient(httpRequestParams);
//            HttpPost post = new HttpPost(SERVER_ADDRESS);
//            String status="";
//            try {
//                post.setEntity(new UrlEncodedFormEntity(dataToSend));
//                HttpResponse response=client.execute(post);
//                status=response.getStatusLine().getStatusCode()+"";
//                Log.d("MyResponseCode ", "-> "+status);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return status;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            progressDialog.dismiss();
//            userCallBack.done(result);
//        }
//
//        private HttpParams getHttpRequestParams() {
//            HttpParams httpRequestParams = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
//                    CONNECTION_TIMEOUT);
//            HttpConnectionParams.setSoTimeout(httpRequestParams,
//                    CONNECTION_TIMEOUT);
//            return httpRequestParams;
//        }
//
//    }

}

