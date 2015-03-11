package com.peacecorps.malaria;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceStore {
	public static SharedPreferences mPrefsStore;
	public static SharedPreferences.Editor mEditor;

	public SharedPreferenceStore() {

	}

	public void getSharedPreferences(Context context) {

		mPrefsStore = context.getSharedPreferences("com.pc.storeTimePicked",
				Context.MODE_PRIVATE);
		mEditor = mPrefsStore.edit();
	}

}