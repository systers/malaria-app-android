package com.peacecorps.malaria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import com.peacecorps.malaria.R;

/**
 * Created by Chimdi on 7/18/14.
 */
public class InfoHubFragmentActivity extends FragmentActivity {

	Button homeIconButton, peaceCorpsPolicy;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_hub_screen);

		homeIconButton = (Button) findViewById(R.id.homeButton);
		peaceCorpsPolicy = (Button) findViewById(R.id.peaceCorpsPolicy);

		addListeners();
	}

	public void addListeners() {
		homeIconButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplication()
						.getApplicationContext(), MainActivity.class));
				finish();
			}
		});

		peaceCorpsPolicy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplication()
						.getApplicationContext(),
						PeaceCorpsPolicyFragmentActivity.class));
			}
		});
	}

}
