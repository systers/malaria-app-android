package com.peacecorps.malaria.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferencesHelper implements PreferencesHelper{

    private static final String PREF_KEY_USER_PREFERENCES = "HAS_USER_SET_PREFERENCES";
    private static final String PREF_KEY_USER_SCORE = "USER_SCORE";
    private static final String PREF_KEY_GAME_SCORE = "GAME_SCORE";
    private static final String PREF_KEY_DRUG_PICKED = "com_peacecorps_malaria_drugPicked";
    private static final String PREF_KEY_FIRST_RUN_TIME = "com_peacecorps_malaria_firstRunTime";
    private static final String PREF_KEY_DRUG_ACCEPTED_COUNTED = "com_peacecorps_malaria_drugAcceptedCount";
    private static final String PREF_KEY_IS_WEEKLY = "com_peacecorps_malaria_isWeekly";
    private static final String PREF_KEY_DOSES_DAILY = "com_peacecorps_malaria_daily_dose";
    private static final String PREF_KEY_DOSES_WEEKLY = "com_peacecorps_malaria_weekly_dose";
    private static final String PREF_KEY_MEDICINE_STORE = "MEDICINE_STORE";
    private static final String PREF_KEY_MEDICINE_LAST_TAKEN_TIME = "check_medicine_last_taken_time";
    private static final String PREF_KEY_MYTH_FACT_GAME = "myth_fact_game";
    private static final String PREF_KEY_RAPID_FIRE_GAME= "rapid_fire_game";
    private static final String PREF_KEY_TONE_URI = "TONE_URI";
    private static final String PREF_KEY_TRIP_DATE = "com_peacecorps_malaria_trip_date";
    private static final String PREF_KEY_TRIP_LOCATION = "TRIP_LOCATION";
    private static final String PREF_KEY_USER_NAME = "USER_NAME";
    private static final String PREF_KEY_USER_EMAIL = "USER_EMAIL";
    private static final String PREF_KEY_USER_AGE = "USER_AGE";
    private static final String PREF_KEY_IS_FIRST_RUN = "IS_FIRST_RUN";
    private static final String PREF_KEY_IS_DRUG_TAKEN = "com_peacecorps_malaria_is_drug_taken";
    private static final String PREF_KEY_ALERT_TIME = "NUMBER_ALERT_TIME";

    private final SharedPreferences mPrefs;

    public AppPreferencesHelper(Context context, String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }
    @Override
    public boolean hasUserSetPreferences() {
        return mPrefs.getBoolean(PREF_KEY_USER_PREFERENCES,false);
    }

    @Override
    public void setUserPreferences(boolean value) {
        mPrefs.edit().putBoolean(PREF_KEY_USER_PREFERENCES,value).apply();
    }

    @Override
    public int getUserScore() {
        return mPrefs.getInt(PREF_KEY_USER_SCORE, 0);
    }

    @Override
    public void setUserScore(int score) {
        mPrefs.edit().putInt(PREF_KEY_USER_SCORE, score).apply();
    }

    @Override
    public int getGameScore() {
        return mPrefs.getInt(PREF_KEY_GAME_SCORE, 0);
    }

    @Override
    public void setGameScore(int score) {
        mPrefs.edit().putInt(PREF_KEY_GAME_SCORE, score).apply();
    }

    @Override
    public String getDrugPicked() {
        return mPrefs.getString(PREF_KEY_DRUG_PICKED,"");
    }

    @Override
    public void setDrugPicked(String drug) {
        mPrefs.edit().putString(PREF_KEY_DRUG_PICKED, drug).apply();
    }

    @Override
    public long getFirstRunTime() {
        return mPrefs.getLong(PREF_KEY_FIRST_RUN_TIME, 0 );
    }

    @Override
    public void setFirstRunTime(long value) {
        mPrefs.edit().putLong(PREF_KEY_FIRST_RUN_TIME, value).apply();
    }

    @Override
    public int getDrugAcceptedCount() {
        return mPrefs.getInt(PREF_KEY_DRUG_ACCEPTED_COUNTED, 0);
    }

    @Override
    public void setDrugAcceptedCount(int value) {
        mPrefs.edit().putInt(PREF_KEY_DRUG_ACCEPTED_COUNTED, value).apply();
    }

    @Override
    public boolean isDosesWeekly() {
        return mPrefs.getBoolean(PREF_KEY_IS_WEEKLY, false);
    }

    @Override
    public void setDoesWeekly(boolean value) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_WEEKLY, value).apply();
    }

    @Override
    public int checkDosesDaily() {
        return mPrefs.getInt(PREF_KEY_DOSES_DAILY, 0);
    }

    @Override
    public void setDosesDaily(int value) {
        mPrefs.edit().putInt(PREF_KEY_DOSES_DAILY, value).apply();
    }

    @Override
    public int checkDosesWeekly() {
        return mPrefs.getInt(PREF_KEY_DOSES_WEEKLY, 0);
    }

    @Override
    public void setDosesWeekly(int value) {
        mPrefs.edit().putInt(PREF_KEY_DOSES_WEEKLY, value).apply();
    }

    @Override
    public int getMedicineStoreValue() {
        return mPrefs.getInt(PREF_KEY_MEDICINE_STORE, 0);
    }

    @Override
    public void setMedicineStoreValue(int value) {
        mPrefs.edit().putInt(PREF_KEY_MEDICINE_STORE, value).apply();
    }

    @Override
    public String getMedicineLastTakenTime() {
        return mPrefs.getString(PREF_KEY_MEDICINE_LAST_TAKEN_TIME, "");
    }

    @Override
    public void setMedicineLastTakenTime(String time) {
        mPrefs.edit().putString(PREF_KEY_MEDICINE_LAST_TAKEN_TIME, time).apply();
    }

    @Override
    public boolean getMythFactGame() {
        return mPrefs.getBoolean(PREF_KEY_MYTH_FACT_GAME, true);
    }

    @Override
    public void setMythFactGame(boolean val) {
        mPrefs.edit().putBoolean(PREF_KEY_MYTH_FACT_GAME, val).apply();
    }

    @Override
    public boolean getRapidFireGame() {
        return mPrefs.getBoolean(PREF_KEY_RAPID_FIRE_GAME, true);
    }

    @Override
    public void setRapidFireGame(boolean val) {
        mPrefs.edit().putBoolean(PREF_KEY_RAPID_FIRE_GAME, val).apply();
    }

    //Todo check default value once again
    @Override
    public String getToneUri() {
        return mPrefs.getString(PREF_KEY_TONE_URI, null);
    }

    @Override
    public void setToneUri(String uri) {
        mPrefs.edit().putString(PREF_KEY_TONE_URI, uri).apply();
    }

    @Override
    public String getTripDate() {
        return mPrefs.getString(PREF_KEY_TRIP_DATE, null);
    }

    @Override
    public void setTripDate(String date) {
        mPrefs.edit().putString(PREF_KEY_TRIP_DATE, date).apply();
    }

    @Override
    public String getTripLocation() {
        return mPrefs.getString(PREF_KEY_TRIP_LOCATION, "");
    }

    @Override
    public void setTripLocation(String location) {
        mPrefs.edit().putString(PREF_KEY_TRIP_LOCATION, location).apply();
    }

    @Override
    public String getUserName() {
        return mPrefs.getString(PREF_KEY_USER_NAME, "");
    }

    @Override
    public String getUserEmail() {
        return mPrefs.getString(PREF_KEY_USER_EMAIL, "");
    }

    @Override
    public int getUserAge() {
        return mPrefs.getInt(PREF_KEY_USER_AGE, 0);
    }

    @Override
    public void setUserName(String name) {
        mPrefs.edit().putString(PREF_KEY_USER_NAME, name).apply();
    }

    @Override
    public void setUserEmail(String email) {
        mPrefs.edit().putString(PREF_KEY_USER_EMAIL, email).apply();
    }

    @Override
    public void setUserAge(int age) {
        mPrefs.edit().putInt(PREF_KEY_USER_AGE, age).apply();
    }

    @Override
    public boolean isFirstRun() {
        return mPrefs.getBoolean(PREF_KEY_IS_FIRST_RUN, true);
    }

    @Override
    public void setFirstRun(boolean val) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_FIRST_RUN, val).apply();
    }

    @Override
    public boolean isDrugTaken() {
        return mPrefs.getBoolean(PREF_KEY_IS_DRUG_TAKEN, false);
    }

    @Override
    public void setDrugTaken(boolean value) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_DRUG_TAKEN, value).apply();
    }

    @Override
    public int getAlertNumberDaysOrWeeks() {
        return mPrefs.getInt(PREF_KEY_ALERT_TIME, -1);
    }

    @Override
    public void setAlertNumberDaysOrWeeks(int value) {
        mPrefs.edit().putInt(PREF_KEY_ALERT_TIME, value).apply();
    }
}
