package com.peacecorps.malaria.data.prefs;

public interface PreferencesHelper {
    boolean hasUserSetPreferences();
    void setUserPreferences(boolean value);
    int getUserScore();
    void setUserScore(int score);
    int getGameScore();
    void setGameScore(int score);
    String getDrugPicked();
    void setDrugPicked(String drug);
    long getFirstRunTime();
    void setFirstRunTime(long value);
    int getDrugAcceptedCount();
    void setDrugAcceptedCount(int value);
    boolean isDosesWeekly();
    void setDoesWeekly(boolean value);
    int checkDosesDaily();
    void setDosesDaily(int value);
    int checkDosesWeekly();
    void setDosesWeekly(int value);
    int getMedicineStoreValue();
    void setMedicineStoreValue(int value);
    String getMedicineLastTakenTime();
    void setMedicineLastTakenTime(String time);
    boolean getMythFactGame();
    void setMythFactGame(boolean val);
    boolean getRapidFireGame();
    void setRapidFireGame(boolean val);
    String getToneUri();
    void setToneUri(String uri);
    String getTripDate();
    void setTripDate(String date);
    String getTripLocation();
    void setTripLocation(String location);
    String getUserName();
    String getUserEmail();
    int getUserAge();
    void setUserName(String name);
    void setUserEmail(String email);
    void setUserAge(int age);
    boolean isFirstRun();
    void setFirstRun(boolean val);
    boolean isDrugTaken();
    void setDrugTaken(boolean value);

}
