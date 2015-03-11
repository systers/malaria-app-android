package com.peacecorps.malaria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chimdi on 7/18/14.
 */
public class DatabaseSQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "MalariaDatabase";
	private static final String userMedicationChoiceTable = "userSettings";

	public DatabaseSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("CREATE TABLE "
				+ userMedicationChoiceTable
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, Drug INTEGER,Choice VARCHAR, Month VARCHAR, Year VARCHAR,Status VARCHAR,Date INTEGER,Percentage DOUBLE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
	}

	public static ArrayList<Double> percentage;
	public static ArrayList<Integer> date;

	public int getData(int month, int year, String choice) {

		percentage = new ArrayList<Double>();
		date = new ArrayList<Integer>();
		int count = 0;
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		String column[] = { "_id", "Date", "Percentage" };
		String args[] = { "" + month, "" + year, "yes", choice };
		Cursor cursor = sqLiteDatabase.query(userMedicationChoiceTable, column,
				"Month =? AND Year =? AND Status =? AND Choice =?", args, null,
				null, null, null);
		boolean isDataFound = false;
		while (cursor.moveToNext()) {
			isDataFound = true;
			count += 1;
			date.add(cursor.getInt(1));
			percentage.add(cursor.getDouble(2));

		}
		if (isDataFound) {
			if (!(date.get(date.size() - 1) == Calendar.getInstance().getTime()
					.getDate())) {
				percentage.add(0.0);
				date.add(Calendar.getInstance().getTime().getDate());
			}
		}
		return count;
	}

	public void getUserMedicationSelection(Context context, String choice,
			Date date, String status, Double percentage) {
		ContentValues values = new ContentValues(2);

		values.put("Drug", SharedPreferenceStore.mPrefsStore.getInt(
				"com.peacecorps.malaria.drug", 0));
		values.put("Choice", choice);
		values.put("Month", "" + date.getMonth());
		values.put("Year", "" + date.getYear());
		values.put("Status", status);
		values.put("Date", date.getDate());
		values.put("Percentage", percentage);

		this.getWritableDatabase().insert(userMedicationChoiceTable,
				"medication", values);

	}

	public Cursor getMedicationData() {
		return null;
	}
}