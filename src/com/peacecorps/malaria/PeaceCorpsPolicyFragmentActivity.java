package com.peacecorps.malaria;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.peacecorps.malaria.R;

/**
 * Created by Chimdi on 7/18/14.
 */
public class PeaceCorpsPolicyFragmentActivity extends FragmentActivity {

	private TextView mPeaceCorpsPolicyLabel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.peace_corps_policy_fragment);

		mPeaceCorpsPolicyLabel = (TextView) findViewById(R.id.peaceCorpsPolicyLabel);
	}

	public void getJSONDataFromWeb() {
		DefaultHttpClient httpclient = new DefaultHttpClient(
				new BasicHttpParams());
		HttpPost httppost = new HttpPost(
				"WEB LINK TO JSON FROM PEACE CORP WILL BE HERE");

		httppost.setHeader("Content-type", "application/json");

		InputStream inputStream = null;
		String result = null;
		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			mPeaceCorpsPolicyLabel.setText(result);
			Toast.makeText(this, "" + result, Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			Toast.makeText(this, "" + e.getCause(), Toast.LENGTH_LONG).show();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception squish) {
			}
		}
	}
}