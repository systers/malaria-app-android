package com.peacecorps.malaria.data;

import com.peacecorps.malaria.data.db.DbHelper;
import com.peacecorps.malaria.data.db.entities.AlarmTime;
import com.peacecorps.malaria.data.prefs.PreferencesHelper;

import java.util.Date;

public class AppDataManager implements DataManager {

    private final DbHelper dbHelper;
    private final PreferencesHelper preferencesHelper;

    public AppDataManager(DbHelper dbHelper,
                          PreferencesHelper preferencesHelper) {
        this.dbHelper = dbHelper;
        this.preferencesHelper = preferencesHelper;

    }

    @Override
    public void getCountForProgressBar(int month, int year, String status, String choice, LoadIntegerCallback callback) {
        dbHelper.getCountForProgressBar(month, year, status, choice, callback);
    }

    @Override
    public void setUserMedicineSelection(int drug, String choice, Date date, String status, Double percentage) {
        dbHelper.setUserMedicineSelection(drug, choice, date, status, percentage);
    }

    @Override
    public void insertAppSettings(String drug, String choice, long date) {
        dbHelper.insertAppSettings(drug, choice, date);
    }

    @Override
    public void getMedicationData(int date, int month, int year, LoadStringCallback callback) {
        dbHelper.getMedicationData(date, month, year, callback);
    }

    @Override
    public void updateMedicationEntry(int date, int month, int year, String entry, double percentage) {
        dbHelper.updateMedicationEntry(date, month, year, entry, percentage);
    }

    @Override
    public void insertOrUpdateMissedMedicationEntry(int drug, String ch, int date, int month, int year, double percentage) {
        dbHelper.insertOrUpdateMissedMedicationEntry(drug, ch, date, month, year, percentage);
    }

    @Override
    public void isEntered(int date, int month, int year, LoadIntegerCallback callback) {
        dbHelper.isEntered(date, month, year, callback);
    }

    @Override
    public void getFirstTimeTimeStamp(LoadLongCallback callback) {
        dbHelper.getFirstTimeTimeStamp(callback);
    }

    @Override
    public void getStatus(int date, int month, int year, LoadStringCallback callback) {
        dbHelper.getStatus(date, month, year, callback);
    }

    @Override
    public void getDosesInaRowWeekly(LoadIntegerCallback callback) {
        dbHelper.getDosesInaRowWeekly(callback);
    }

    @Override
    public void resetDatabase() {
        dbHelper.resetDatabase();
    }

    @Override
    public void insertLocation(String location) {
        dbHelper.insertLocation(location);
    }

    @Override
    public void getLocation(LoadListLocationCallback callback) {
        dbHelper.getLocation(callback);
    }

    @Override
    public void insertPackingItem(String pItem, int quantity, String status) {
        dbHelper.insertPackingItem(pItem, quantity, status);
    }

    @Override
    public void getPackingItemChecked(LoadListPackingCallback callback) {
        dbHelper.getPackingItemChecked(callback);
    }

    @Override
    public void getPackingItem(LoadListPackingCallback callback) {
        dbHelper.getPackingItem(callback);
    }

    @Override
    public void refreshPackingItemStatus() {
        dbHelper.refreshPackingItemStatus();
    }

    @Override
    public void getLastTaken(LoadStringCallback callback) {
        dbHelper.getLastTaken(callback);
    }

    @Override
    public void getMedicineCountTaken(LoadIntegerCallback callback) {
        dbHelper.getMedicineCountTaken(callback);
    }

    @Override
    public void getCountTakenBetween(Date s, Date e, LoadIntegerCallback callback) {
        dbHelper.getCountTakenBetween(s,e,callback);
    }

    @Override
    public void getAlarmData(LoadAlarmDataCallback callback) {
        dbHelper.getAlarmData(callback);
    }

    @Override
    public void insertAlarmData(AlarmTime time) {
        dbHelper.insertAlarmData(time);
    }

    @Override
    public boolean hasUserSetPreferences() {
        return preferencesHelper.hasUserSetPreferences();
    }

    @Override
    public void setUserPreferences(boolean value) {
        preferencesHelper.setUserPreferences(value);
    }

    @Override
    public int getUserScore() {
        return preferencesHelper.getUserScore();
    }

    @Override
    public void setUserScore(int score) {
        preferencesHelper.setUserScore(score);
    }

    @Override
    public int getGameScore() {
        return preferencesHelper.getGameScore();
    }

    @Override
    public void setGameScore(int score) {
        preferencesHelper.setGameScore(score);
    }

    @Override
    public String getDrugPicked() {
        return preferencesHelper.getDrugPicked();
    }

    @Override
    public void setDrugPicked(String drug) {
        preferencesHelper.setDrugPicked(drug);
    }

    @Override
    public long getFirstRunTime() {
        return preferencesHelper.getFirstRunTime();
    }

    @Override
    public void setFirstRunTime(long value) {
        preferencesHelper.setFirstRunTime(value);
    }

    @Override
    public int getDrugAcceptedCount() {
        return preferencesHelper.getDrugAcceptedCount();
    }

    @Override
    public void setDrugAcceptedCount(int value) {
        preferencesHelper.setDrugAcceptedCount(value);
    }

    @Override
    public boolean isDosesWeekly() {
        return preferencesHelper.isDosesWeekly();
    }

    @Override
    public void setDoesWeekly(boolean value) {
        preferencesHelper.setDoesWeekly(value);
    }

    @Override
    public int checkDosesDaily() {
        return preferencesHelper.checkDosesDaily();
    }

    @Override
    public void setDosesDaily(int value) {
        preferencesHelper.setDosesDaily(value);
    }

    @Override
    public int checkDosesWeekly() {
        return preferencesHelper.checkDosesWeekly();
    }

    @Override
    public void setDosesWeekly(int value) {
        preferencesHelper.setDosesWeekly(value);
    }

    @Override
    public int getMedicineStoreValue() {
        return preferencesHelper.getMedicineStoreValue();
    }

    @Override
    public void setMedicineStoreValue(int value) {
        preferencesHelper.setMedicineStoreValue(value);
    }

    @Override
    public String getMedicineLastTakenTime() {
        return preferencesHelper.getMedicineLastTakenTime();
    }

    @Override
    public void setMedicineLastTakenTime(String time) {
        preferencesHelper.setMedicineLastTakenTime(time);
    }

    @Override
    public boolean getMythFactGame() {
        return preferencesHelper.getMythFactGame();
    }

    @Override
    public void setMythFactGame(boolean val) {
        preferencesHelper.setMythFactGame(val);
    }

    @Override
    public boolean getRapidFireGame() {
        return preferencesHelper.getRapidFireGame();
    }

    @Override
    public void setRapidFireGame(boolean val) {
        preferencesHelper.setRapidFireGame(val);
    }

    @Override
    public String getToneUri() {
        return preferencesHelper.getToneUri();
    }

    @Override
    public void setToneUri(String uri) {
        preferencesHelper.setToneUri(uri);
    }

    @Override
    public String getTripDate() {
        return preferencesHelper.getTripDate();
    }

    @Override
    public void setTripDate(String date) {
        preferencesHelper.setTripDate(date);
    }

    @Override
    public String getTripLocation() {
        return preferencesHelper.getTripLocation();
    }

    @Override
    public void setTripLocation(String location) {
        preferencesHelper.setTripLocation(location);
    }

    @Override
    public String getUserName() {
        return preferencesHelper.getUserName();
    }

    @Override
    public String getUserEmail() {
        return preferencesHelper.getUserEmail();
    }

    @Override
    public int getUserAge() {
        return preferencesHelper.getUserAge();
    }

    @Override
    public void setUserName(String name) {
        preferencesHelper.setUserName(name);
    }

    @Override
    public void setUserEmail(String email) {
        preferencesHelper.setUserEmail(email);
    }

    @Override
    public void setUserAge(int age) {
        preferencesHelper.setUserAge(age);
    }

    @Override
    public boolean isFirstRun() {
        return preferencesHelper.isFirstRun();
    }

    @Override
    public void setFirstRun(boolean val) {
        preferencesHelper.setFirstRun(val);
    }

    @Override
    public boolean isDrugTaken() {
        return preferencesHelper.isDrugTaken();
    }

    @Override
    public void setDrugTaken(boolean value) {
        preferencesHelper.setDrugTaken(value);
    }
}
