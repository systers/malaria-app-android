package com.peacecorps.malaria;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager.LayoutParams;

public class AlertDialogFragment extends DialogFragment {

	private static int mDrugAcceptedCount;
	private static int mDrugRejectedCount;
	static SharedPreferenceStore mSharedPreferenceStore;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		/**
		 * Turn Screen On and Unlock the keypad when this alert dialog is
		 * displayed
		 */
		getActivity().getWindow().addFlags(
				LayoutParams.FLAG_TURN_SCREEN_ON
						| LayoutParams.FLAG_DISMISS_KEYGUARD);

		/** Creating a alert dialog builder */
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		/** Setting title for the alert dialog */

		builder.setTitle(R.string.alert_dialog_title);

		/** Setting the content for the alert dialog */
		builder.setMessage(R.string.alert_dialog_message);

		/** Defining an OK button event listener */
		builder.setPositiveButton("Taken", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						getSharedPreferences();
						getSettings();
						if (mSharedPreferenceStore.mPrefsStore.getBoolean(
								"com.pc.isWeekly", false)) {
							saveUsersettings(true, true);
							changeWeeklyAlarmTime();
						} else {
							if (checkDrugTakenTimeInterval("dateDrugTaken") > 0) {
								saveUsersettings(true, false);
							}
						}
						getActivity().finish();
					}
				});

			}
		}).setNegativeButton("Not Taken", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						getSharedPreferences();
						getActivity().finish();
					}
				});

			}
		}).setNeutralButton("Snooze", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				snooze();

			}
		}).setCancelable(false);

		/** Creating the alert dialog window */
		return builder.create();
	}

	public long checkDrugTakenTimeInterval(String time) {
		long interval = 0;
		long today = new Date().getTime();
		long takenDate = mSharedPreferenceStore.mPrefsStore.getLong("com.pc."
				+ time, 0);
		long oneDay = 1000 * 60 * 60 * 24;
		interval = (today - takenDate) / oneDay;
		return interval;
	}

	public void snooze() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				getSharedPreferences();
				Intent intent = new Intent(getActivity(),
						AlertCallerFragmentActivity.class);
				PendingIntent pendingSnooze = PendingIntent.getActivity(
						getActivity().getApplicationContext(), 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager alarmManager = (AlarmManager) getActivity()
						.getSystemService(getActivity().ALARM_SERVICE);
				Calendar calender = Calendar.getInstance();
				Date date = new Date();
				calender.setTime(date);
				calender.add(Calendar.MINUTE, 30);
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calender.getTimeInMillis(), pendingSnooze);
			}
		});
	}

	public void changeWeeklyAlarmTime() {
		int hour = Calendar.getInstance().getTime().getHours();
		int minute = Calendar.getInstance().getTime().getMinutes() - 1;
		getActivity().startService(
				new Intent(getActivity(), AlarmService.class));
		mSharedPreferenceStore.mEditor.putInt("com.pc.AlarmHour", hour)
				.commit();
		mSharedPreferenceStore.mEditor.putInt("com.pc.AlarmMinute", minute)
				.commit();
	}

	public void saveUsersettings(Boolean state, Boolean isWeekly) {
		if (isWeekly) {
			mSharedPreferenceStore.mEditor.putLong("com.pc.weeklyDate",
					new Date().getTime()).commit();
			mSharedPreferenceStore.mEditor.putBoolean(
					"com.pc.isWeeklyDrugTaken", state).commit();
		} else {
			mSharedPreferenceStore.mEditor.putLong("com.pc.dateDrugTaken",
					new Date().getTime()).commit();
			mSharedPreferenceStore.mEditor.putBoolean("com.pc.isDrugTaken",
					state).commit();
		}
		mSharedPreferenceStore.mEditor.putInt("com.pc.drugRejectedCount",
				mDrugRejectedCount).commit();
		mSharedPreferenceStore.mEditor.putInt("com.pc.drugAcceptedCount",
				mDrugAcceptedCount).commit();

	}

	public void getSettings() {
		mDrugAcceptedCount = mSharedPreferenceStore.mPrefsStore.getInt(
				"com.pc.drugAcceptedCount", 0);
		mDrugRejectedCount = mSharedPreferenceStore.mPrefsStore.getInt(
				"com.pc.drugRejectedCount", 0);
	}

	public void getSharedPreferences() {

		mSharedPreferenceStore.mPrefsStore = getActivity()
				.getSharedPreferences("com.pc.storeTimePicked",
						Context.MODE_PRIVATE);
		mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
				.edit();
	}

	/**
	 * The application exits, if the user presses the back button
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().finish();
	}

}