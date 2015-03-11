package com.peacecorps.malaria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import java.util.Date;

public class MainActivity extends FragmentActivity {

	FragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;
	Button mInfoButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mInfoButton = (Button) findViewById(R.id.infoButton);
		mInfoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplication()
						.getApplicationContext(), InfoHubFragmentActivity.class));
				finish();

			}
		});

		mAdapter = new FragmentAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.vPager);
		mPager.setAdapter(mAdapter);

		mIndicator = (CirclePageIndicator) findViewById(R.id.vIndicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				if (position == 1) {
					if (FirstAnalyticFragment.checkMediLastTakenTime != null) {
						FirstAnalyticFragment.checkMediLastTakenTime
								.setText(SharedPreferenceStore.mPrefsStore
										.getString(
												"com.peacecorps.malaria.checkMediLastTakenTime",
												"").toString());

						int currentDose = 0;
						if (SharedPreferenceStore.mPrefsStore.getBoolean(
								"com.peacecorps.malaria.isWeekly", false)) {
							currentDose = SharedPreferenceStore.mPrefsStore
									.getInt("com.peacecorps.malaria.weeklyDose",
											0);
						} else {
							currentDose = SharedPreferenceStore.mPrefsStore
									.getInt("com.peacecorps.malaria.dailyDose",
											0);
						}
						FirstAnalyticFragment.doses.setText("" + currentDose);

						long interval = checkDrugTakenTimeInterval("firstRunTime");
						int takenCount = SharedPreferenceStore.mPrefsStore
								.getInt("com.peacecorps.malaria.drugAcceptedCount",
										0);
						double adherenceRate = (takenCount / interval) * 100;
						FirstAnalyticFragment.adherence.setText(""
								+ adherenceRate + "%");
					}

				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}

			@Override
			public void onPageScrolled(int i, float v, int i2) {

			}

		});

	}

	public long checkDrugTakenTimeInterval(String time) {
		long interval = 0;
		long today = new Date().getTime();
		long takenDate = SharedPreferenceStore.mPrefsStore.getLong(
				"com.peacecorps.malaria." + time, 0);
		long oneDay = 1000 * 60 * 60 * 24;
		interval = (today - takenDate) / oneDay;
		return interval + 1;
	}

}