package com.peacecorps.malaria.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public class ToastLogUtil {
    private static final String TAG = "myTag";

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showDebugLog (String message) {
        Log.d(TAG, message);
    }

    public static void showErrorLog (String message) {
        Log.d(TAG, message);
    }
}
