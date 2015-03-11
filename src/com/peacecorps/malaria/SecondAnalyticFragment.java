package com.peacecorps.malaria;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.Calendar;

public class SecondAnalyticFragment extends Fragment {

	private TextView firstMonthProgressLabel, secondMonthProgressLabel,
			thirdMonthProgressLabel, fourthMonthProgressLabel;
	private TextView firstMonthProgressPercent, secondMonthProgressPercent,
			thirdMonthProgressPercent, fourthMonthProgressPercent;
	private ProgressBar firstMonthProgressBar, secondMonthProgressBar,
			thirdMonthProgressBar, fourthMonthProgressBar;
	private Button mSettingsButton;
	private View rootView;

	static SharedPreferenceStore mSharedPreferenceStore;

	GraphViewSeries drugGraphSeries;
	private GraphViewData[] graphViewData;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_second_analytic_screen,
				null);

		mSettingsButton = (Button) rootView
				.findViewById(R.id.fragment_second_screen_settings_button);

		firstMonthProgressLabel = (TextView) rootView
				.findViewById(R.id.firstMonthProgressLabel);
		secondMonthProgressLabel = (TextView) rootView
				.findViewById(R.id.secondMonthProgressLabel);
		thirdMonthProgressLabel = (TextView) rootView
				.findViewById(R.id.thirdMonthProgressLabel);
		fourthMonthProgressLabel = (TextView) rootView
				.findViewById(R.id.fourthMonthProgressLabel);

		firstMonthProgressPercent = (TextView) rootView
				.findViewById(R.id.firstMonthProgressPercent);
		secondMonthProgressPercent = (TextView) rootView
				.findViewById(R.id.secondMonthProgressPercent);
		thirdMonthProgressPercent = (TextView) rootView
				.findViewById(R.id.thirdMonthProgressPercent);
		fourthMonthProgressPercent = (TextView) rootView
				.findViewById(R.id.fourthMonthProgressPercent);

		firstMonthProgressBar = (ProgressBar) rootView
				.findViewById(R.id.firstMonthProgressBar);
		secondMonthProgressBar = (ProgressBar) rootView
				.findViewById(R.id.secondMonthProgressBar);
		thirdMonthProgressBar = (ProgressBar) rootView
				.findViewById(R.id.thirdMonthProgressBar);
		fourthMonthProgressBar = (ProgressBar) rootView
				.findViewById(R.id.fourthMonthProgressBar);

		DatabaseSQLiteHelper databaseSQLiteHelper = new DatabaseSQLiteHelper(
				getActivity());
		int date = Calendar.getInstance().getTime().getMonth();
		String choice;
		if (mSharedPreferenceStore.mPrefsStore.getBoolean(
				"com.peacecorps.malaria.isWeekly", false)) {
			choice = "weekly";
		} else {
			choice = "daily";
		}

		// firstMonthProgressLabel.setText(getMonth(date - 3));
		int progress = databaseSQLiteHelper.getData(mdate, myear, choice) * 100;
		firstMonthProgressBar.setProgress(progress);
		firstMonthProgressPercent.setText("" + progress + "%");

		// secondMonthProgressLabel.setText(getMonth(date - 2));
		progress = databaseSQLiteHelper.getData(mdate, myear, choice) * 100;
		secondMonthProgressBar.setProgress(progress);
		secondMonthProgressPercent.setText("" + progress + "%");

		// thirdMonthProgressLabel.setText(getMonth(date - 1));
		progress = databaseSQLiteHelper.getData(mdate, myear, choice) * 100;
		thirdMonthProgressBar.setProgress(progress);
		thirdMonthProgressPercent.setText("" + progress + "%");

		// fourthMonthProgressLabel.setText(getMonth(date));
		progress = databaseSQLiteHelper.getData(mdate, myear, choice) * 100;
		fourthMonthProgressBar.setProgress(progress);
		fourthMonthProgressPercent.setText("" + progress + "%");

		getSharedPreferences();
		addButtonListeners();
		setupAndShowGraph();

		return rootView;
	}

	int mdate;
	int myear;

	public String getMonth(int date) {
		String month[] = getResources().getStringArray(R.array.month);
		if (date == 0) {
			date = 12;
			myear = Calendar.getInstance().getTime().getYear() - 1;
		} else if (date == -1) {
			date = 11;
			myear = Calendar.getInstance().getTime().getYear() - 1;
		} else if (date == -2) {
			date = 10;
			myear = Calendar.getInstance().getTime().getYear() - 1;
		}
		myear = Calendar.getInstance().getTime().getYear();
		mdate = date;
		return month[date];
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

		firstMonthProgressBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "First progress",
						Toast.LENGTH_SHORT).show();
			}
		});

		secondMonthProgressBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Second progress",
						Toast.LENGTH_SHORT).show();
			}
		});

		thirdMonthProgressBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Third progress",
						Toast.LENGTH_SHORT).show();
			}
		});

		fourthMonthProgressBar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Fourth progress",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void getSharedPreferences() {

		mSharedPreferenceStore.mPrefsStore = getActivity()
				.getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
						Context.MODE_PRIVATE);
		mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
				.edit();
	}

	public void setupAndShowGraph() {
		GraphViewData graphViewData[] = new GraphViewData[DatabaseSQLiteHelper.percentage
				.size()];
		for (int index = 0; index < DatabaseSQLiteHelper.percentage.size(); index++) {
			graphViewData[index] = new GraphViewData(
					DatabaseSQLiteHelper.date.get(index), Double.parseDouble(""
							+ DatabaseSQLiteHelper.percentage.get(index)));
		}
		drugGraphSeries = new GraphViewSeries(graphViewData);

		GraphView lineGraphView = new LineGraphView(getActivity(), "");

		lineGraphView.getGraphViewStyle().setGridColor(
				getResources().getColor(R.color.golden_brown));
		lineGraphView.getGraphViewStyle().setHorizontalLabelsColor(
				getResources().getColor(R.color.golden_brown));
		lineGraphView.getGraphViewStyle().setVerticalLabelsColor(
				getResources().getColor(R.color.golden_brown));
		lineGraphView.getGraphViewStyle().setTextSize(15.0F);
		lineGraphView.setScrollable(true);
		lineGraphView.setScalable(true);

		lineGraphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isXAxis) {

				return null;
			}
		});

		((LineGraphView) lineGraphView).setDrawBackground(true);
		lineGraphView.addSeries(drugGraphSeries);

		LinearLayout linearLayout = (LinearLayout) rootView
				.findViewById(R.id.graphView);
		linearLayout.addView(lineGraphView);

	}
}