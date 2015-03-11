package com.peacecorps.malaria;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeScreenFragment extends Fragment {

	static final private int INIT_HOUR = 5;
	static final private int INIT_MINUTE = 30;

	private Button mAcceptMedicationButton;
	private Button mRejectMedicationButton;
	private Button mSettingsButton;
	private TextView mCurrentDateLabel;
	private TextView mCurrentDayOfweekLabel;
	private static CharSequence mGetCurrentDate;
	private static int mDrugAcceptedCount;
	private static int drugRejectedCount;
	private Calendar mCalendar;
	private String[] mPossibledays = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	private static View rootView;

	int checkDay = -1;

	static SharedPreferenceStore mSharedPreferenceStore;

	@Override
	public void onResume() {

		super.onResume();
		updateUI();

	}

	public void getSharedPreferences() {

		mSharedPreferenceStore.mPrefsStore = getActivity()
				.getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
						Context.MODE_PRIVATE);
		mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
				.edit();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_home_screen, null);

		updateUI();
		return rootView;

	}

	public double computeAdherenceRate() {
		long interval = checkDrugTakenTimeInterval("firstRunTime") + 1;
		int takenCount = SharedPreferenceStore.mPrefsStore.getInt(
				"com.peacecorps.malaria.drugAcceptedCount", 0);
		double adherenceRate = (takenCount / interval) * 100;
		return adherenceRate;
	}

	public void addButtonListeners() {
		mSettingsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mSharedPreferenceStore.mEditor.putBoolean(
						"com.peacecorps.malaria.hasUserSetPreference", false)
						.commit();
				startActivity(new Intent(getActivity(),
						UserMedicineSettingsFragmentActivity.class));
				getActivity().finish();

			}
		});

		mAcceptMedicationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MediaPlayer.create(getActivity(), R.raw.accept_button_sound)
						.start();
				mDrugAcceptedCount += 1;
				int value = SharedPreferenceStore.mPrefsStore.getInt(
						"com.peacecorps.malaria.AcceptedCount", 0) + 1;
				SharedPreferenceStore.mEditor.putInt(
						"com.peacecorps.malaria.AcceptedCount", value).commit();
				if (mSharedPreferenceStore.mPrefsStore.getBoolean(
						"com.peacecorps.malaria.isWeekly", false)) {
					int currentDose = SharedPreferenceStore.mPrefsStore.getInt(
							"com.peacecorps.malaria.weeklyDose", 0) + 1;
					mSharedPreferenceStore.mEditor.putInt(
							"com.peacecorps.malaria.weeklyDose", currentDose)
							.commit();
					decideDrugTakenUIBoolean(true, true);
					DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(
							getActivity());
					databaseSQLiteHelper.getUserMedicationSelection(
							getActivity(), "weekly", Calendar.getInstance()
									.getTime(), "yes", computeAdherenceRate());
				} else {
					decideDrugTakenUIBoolean(false, true);
					int currentDose = SharedPreferenceStore.mPrefsStore.getInt(
							"com.peacecorps.malaria.dailyDose", 0) + 1;
					mSharedPreferenceStore.mEditor.putInt(
							"com.peacecorps.malaria.dailyDose", currentDose)
							.commit();
					DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(
							getActivity());
					databaseSQLiteHelper.getUserMedicationSelection(
							getActivity(), "daily", Calendar.getInstance()
									.getTime(), "yes", computeAdherenceRate());
				}

			}
		});

		mRejectMedicationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MediaPlayer.create(getActivity(), R.raw.reject_button_sound)
						.start();
				drugRejectedCount += 1;
				if (mSharedPreferenceStore.mPrefsStore.getBoolean(
						"com.peacecorps.malaria.isWeekly", false)) {
					decideDrugTakenUIBoolean(true, false);
					DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(
							getActivity());
					databaseSQLiteHelper.getUserMedicationSelection(
							getActivity(), "weekly", Calendar.getInstance()
									.getTime(), "no", computeAdherenceRate());

				} else {
					decideDrugTakenUIBoolean(false, false);
					DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(
							getActivity());
					databaseSQLiteHelper.getUserMedicationSelection(
							getActivity(), "daily", Calendar.getInstance()
									.getTime(), "no", computeAdherenceRate());

				}

			}
		});
	}

	public void createView() {

		mAcceptMedicationButton = (Button) rootView
				.findViewById(R.id.fragment_home_screen_accept_medication_button);
		mRejectMedicationButton = (Button) rootView
				.findViewById(R.id.fragment_home_screen__reject_medication_button);
		mSettingsButton = (Button) rootView
				.findViewById(R.id.fragment_home_screen_settings_button);
		mCurrentDateLabel = (TextView) rootView
				.findViewById(R.id.fragment_home_screen_current_date);
		mCurrentDayOfweekLabel = (TextView) rootView
				.findViewById(R.id.fragment_home_screen_current_day_of_week);

		mCurrentDateLabel.setTextColor(Color.rgb(89, 43, 21));
		mCurrentDayOfweekLabel.setTextColor(Color.rgb(89, 43, 21));
		mCurrentDateLabel.setText(mGetCurrentDate);
		mCurrentDayOfweekLabel
				.setText(decideDayofWeek(checkDay, mPossibledays));
	}

	public void updateUI() {
		getSharedPreferences();

		mCalendar = Calendar.getInstance();

		checkDay = mCalendar.get(Calendar.DAY_OF_WEEK);

		getSettings();
		createView();
		addButtonListeners();
		getSettings();
		decideisDrugTakenUI();
	}

	public void saveUsersettings(Boolean state, Boolean isWeekly) {
		if (isWeekly) {
			mSharedPreferenceStore.mEditor.putLong(
					"com.peacecorps.malaria.weeklyDate", new Date().getTime())
					.commit();
			mSharedPreferenceStore.mEditor.putBoolean(
					"com.peacecorps.malaria.isWeeklyDrugTaken", state).commit();
		} else {
			mSharedPreferenceStore.mEditor.putLong(
					"com.peacecorps.malaria.dateDrugTaken",
					new Date().getTime()).commit();
			mSharedPreferenceStore.mEditor.putBoolean(
					"com.peacecorps.malaria.isDrugTaken", state).commit();
		}
		mSharedPreferenceStore.mEditor.putInt(
				"com.peacecorps.malaria.drugRejectedCount", drugRejectedCount)
				.commit();
		mSharedPreferenceStore.mEditor.putInt(
				"com.peacecorps.malaria.drugAcceptedCount", mDrugAcceptedCount)
				.commit();

	}

	public void getSettings() {
		checkDay = mCalendar.get(Calendar.DAY_OF_WEEK);
		mGetCurrentDate = new SimpleDateFormat("dd/MM/yyyy",
				Locale.getDefault()).format(mCalendar.getTime());
		mDrugAcceptedCount = mSharedPreferenceStore.mPrefsStore.getInt(
				"com.peacecorps.malaria.drugAcceptedCount", 0);
		drugRejectedCount = mSharedPreferenceStore.mPrefsStore.getInt(
				"com.peacecorps.malaria.drugRejectedCount", 0);
		decideDayofWeek(checkDay, mPossibledays);
	}

	public void missedWeekUI() {
		mSharedPreferenceStore.mEditor.putInt(
				"com.peacecorps.malaria.weeklyDose", 0).commit();
		mCurrentDateLabel.setTextColor(Color.RED);
		mCurrentDayOfweekLabel.setTextColor(Color.RED);
	}

	public void decideisDrugTakenUI() {
		if (mSharedPreferenceStore.mPrefsStore.getBoolean(
				"com.peacecorps.malaria.isWeekly", false)) {
			if (checkDrugTakenTimeInterval("weeklyDate") == 0) {
				if ((mSharedPreferenceStore.mPrefsStore.getBoolean(
						"com.peacecorps.malaria.isWeeklyDrugTaken", false))) {
					isDrugTakenUI();
				} else {
					newDayUI();
				}
			} else {
				if (checkDrugTakenTimeInterval("weeklyDate") < 7
						&& checkDrugTakenTimeInterval("weeklyDate") > 0) {
					if ((mSharedPreferenceStore.mPrefsStore.getBoolean(
							"com.peacecorps.malaria.isWeeklyDrugTaken", false))) {
						isDrugTakenUI();
					} else {
						missedWeekUI();
						newDayUI();
					}
				} else if (checkDrugTakenTimeInterval("weeklyDate") > 7) {
					SharedPreferenceStore.mEditor.putInt(
							"com.peacecorps.malaria.AcceptedCount", 0).commit();
					missedWeekUI();
					newDayUI();
				}
			}
		} else {
			if (checkDrugTakenTimeInterval("dateDrugTaken") == 0) {
				if (mSharedPreferenceStore.mPrefsStore.getBoolean(
						"com.peacecorps.malaria.isDrugTaken", false)) {
					isDrugTakenUI();
				} else {

					isDrugNotTakenUI();
				}

			} else {

				if (checkDrugTakenTimeInterval("dateDrugTaken") > 1) {
					mSharedPreferenceStore.mEditor.putInt(
							"com.peacecorps.malaria.dailyDose", 0);
				}

				newDayUI();
			}
		}
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

	public void newDayUI() {
		mAcceptMedicationButton
				.setBackgroundResource(R.drawable.accept_medi_checked_);
		mRejectMedicationButton
				.setBackgroundResource(R.drawable.reject_medi_checked);
		setButtonState(true);

	}

	public void isDrugNotTakenUI() {
		mAcceptMedicationButton
				.setBackgroundResource(R.drawable.accept_medi_grayscale);
		mRejectMedicationButton
				.setBackgroundResource(R.drawable.reject_medi_checked);
		setButtonState(false);
	}

	public void isDrugTakenUI() {
		mCurrentDateLabel.setTextColor(Color.rgb(89, 43, 21));
		mCurrentDayOfweekLabel.setTextColor(Color.rgb(89, 43, 21));
		mAcceptMedicationButton
				.setBackgroundResource(R.drawable.accept_medi_checked_);
		mRejectMedicationButton
				.setBackgroundResource(R.drawable.reject_medi_grayscale);
		setButtonState(false);
		storeMediTimeLastChecked();

	}

	public void setButtonState(boolean state) {
		mAcceptMedicationButton.setEnabled(state);
		mRejectMedicationButton.setEnabled(state);
	}

	public void decideDrugTakenUIBoolean(Boolean isWeekly, Boolean isTaken) {
		if (isWeekly) {
			if (checkDrugTakenTimeInterval("weeklyDate") > 1) {
				changeWeeklyAlarmTime();
			}
		}
		saveUsersettings(isTaken, isWeekly);
		if (isTaken) {
			isDrugTakenUI();
		} else {
			isDrugNotTakenUI();
		}
	}

	public void storeMediTimeLastChecked() {
		CharSequence lastMedicationCheckedTime = "";
		Calendar c = Calendar.getInstance();
		lastMedicationCheckedTime = new SimpleDateFormat("dd/MM",
				Locale.getDefault()).format(c.getTime());

		mSharedPreferenceStore.mEditor.putString(
				"com.peacecorps.malaria.checkMediLastTakenTime",
				lastMedicationCheckedTime.toString()).commit();
	}

	public void changeWeeklyAlarmTime() {
		int hour = Calendar.getInstance().getTime().getHours();
		int minute = Calendar.getInstance().getTime().getMinutes() - 1;
		getActivity().startService(
				new Intent(getActivity(), AlarmService.class));
		mSharedPreferenceStore.mEditor.putInt(
				"com.peacecorps.malaria.AlarmHour", hour).commit();
		mSharedPreferenceStore.mEditor.putInt(
				"com.peacecorps.malaria.AlarmMinute", minute).commit();
	}

	public String decideDayofWeek(int checkDay, String possibleDays[]) {
		String currentDayOfWeek = null;
		switch (checkDay) {
		case 1:
			currentDayOfWeek = possibleDays[0];
			break;
		case 2:
			currentDayOfWeek = possibleDays[1];
			break;
		case 3:
			currentDayOfWeek = possibleDays[2];
			break;
		case 4:
			currentDayOfWeek = possibleDays[3];
			break;
		case 5:
			currentDayOfWeek = possibleDays[4];
			break;
		case 6:
			currentDayOfWeek = possibleDays[5];
			break;
		case 7:
			currentDayOfWeek = possibleDays[6];
			break;

		}
		return currentDayOfWeek;
	}
}