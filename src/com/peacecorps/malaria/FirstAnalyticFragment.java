package com.peacecorps.malaria;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.peacecorps.malaria.R;

public class FirstAnalyticFragment extends Fragment {

	static SharedPreferenceStore mSharedPreferenceStore;
	static View rootView;
	public static TextView checkMediLastTakenTime = null;
	public static TextView doses = null;
	public static TextView adherence = null;
	private Button mSettingsButton;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_first_analytic_screen,
				null);

		mSettingsButton = (Button) rootView
				.findViewById(R.id.fragment_first_screen_settings_button);
		checkMediLastTakenTime = (TextView) rootView
				.findViewById(R.id.checkMediLastTakenTime);
		doses = (TextView) rootView.findViewById(R.id.doses);
		adherence = (TextView) rootView.findViewById(R.id.adherence);

		if (checkMediLastTakenTime != null) {
			checkMediLastTakenTime.setText(mSharedPreferenceStore.mPrefsStore
					.getString("com.peacecorps.malaria.checkMediLastTakenTime",
							"").toString());
			doses.setText(""
					+ mSharedPreferenceStore.mPrefsStore.getInt(
							"com.peacecorps.malaria.acceptedCount", 0));
		}

		getSharedPreferences();
		addButtonListeners();

		return rootView;

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
	}

	public void getSharedPreferences() {

		mSharedPreferenceStore.mPrefsStore = getActivity()
				.getSharedPreferences("com.peacecorps.malaria.storeTimePicked",
						Context.MODE_PRIVATE);
		mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
				.edit();
	}
}