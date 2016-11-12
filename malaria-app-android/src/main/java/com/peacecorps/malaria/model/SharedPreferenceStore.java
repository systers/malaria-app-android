package com.peacecorps.malaria.model;

import android.content.Context;
import android.content.SharedPreferences;

/**Class for Getting the Shared Preferences**/
public class SharedPreferenceStore {
    public static SharedPreferences mPrefsStore;
    public static SharedPreferences.Editor mEditor;

    public SharedPreferenceStore() {

    }

    public void getSharedPreferences(Context context) {

        mPrefsStore = context.getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
                Context.MODE_PRIVATE);
        mEditor = mPrefsStore.edit();
    }

}
