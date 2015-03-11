package com.peacecorps.malaria;

import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class AlertCallerFragmentActivity extends FragmentActivity {
	static SharedPreferenceStore mSharedPreferenceStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/** Creating an Alert Dialog Window */
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				String weeklyDate = "weeklyDate";
				getSharedPreferences();
				if (mSharedPreferenceStore.mPrefsStore.getBoolean(
						"com.peacecorps.malaria.isWeekly", false)) {

					if (checkDrugTakenTimeInterval(weeklyDate) == 0
							|| checkDrugTakenTimeInterval(weeklyDate) >= 7) {
						callAlarm();
					} else {
						finish();
					}
				} else {
					callAlarm();
				}
			}
		});

	}

	public void callAlarm() {
		AlertDialogFragment alert = new AlertDialogFragment();

		alert.show(getSupportFragmentManager(), "alertDemo");

		alert.setCancelable(false);
	}

	public long checkDrugTakenTimeInterval(String time) {
		long interval = 0;
		long today = new Date().getTime();
		long takenDate = mSharedPreferenceStore.mPrefsStore.getLong(
				"com.peacecorps.malaria." + time, 0);
		long oneDay = 1000 * 60 * 60 * 24;
		interval = (today - takenDate) / oneDay;
		return interval;
	}

	public void getSharedPreferences() {
		// reading the application SharedPreferences for storing of time and
		// drug selected
		mSharedPreferenceStore.mPrefsStore = getSharedPreferences(
				"com.peacecorps.malaria.storeTimePicked", Context.MODE_PRIVATE);
		mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
				.edit();
	}
}