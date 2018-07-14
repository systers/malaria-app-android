package com.peacecorps.malaria.data.db;

import com.peacecorps.malaria.data.db.entities.AlarmTime;
import com.peacecorps.malaria.data.db.entities.Location;
import com.peacecorps.malaria.data.db.entities.Packing;

import java.util.Date;
import java.util.List;

public interface DbHelper {
    interface LoadIntegerCallback {
        void onDataLoaded(int value);
    }

    interface LoadStringCallback {
        void onDataLoaded(String data);
    }

    interface LoadLongCallback {
        void onDataLoaded(Long value);
    }

    interface LoadListLocationCallback {
        void onDataLoaded(List<Location> locations);
    }

    interface LoadListPackingCallback {
        void onDataLoaded(List<Packing> packingList);
    }

    interface LoadAlarmDataCallback {
        void onDataLoaded(AlarmTime time);
    }

    void getCountForProgressBar( int month, int year, String status, String choice, LoadIntegerCallback callback);

    void setUserMedicineSelection(int drug, String choice, Date date, String status, Double percentage);

    void insertAppSettings(String drug, String choice, long date);

    void getMedicationData(int date, int month, int year, LoadStringCallback callback);

    void updateMedicationEntry(int date, int month, int year, String entry,double percentage);

    void insertOrUpdateMissedMedicationEntry(int drug, String ch, int date, int month, int year,double percentage);

    void isEntered(int date,int month, int year, LoadIntegerCallback callback);

    void getFirstTimeTimeStamp(LoadLongCallback callback);

    void getStatus(int date,int month,int year, LoadStringCallback callback);

    void getDosesInaRowWeekly(LoadIntegerCallback callback);

    void resetDatabase();

    void insertLocation(String location);

    void getLocation(LoadListLocationCallback callback);

    void insertPackingItem(String pItem,int quantity, String status);

    void getPackingItemChecked(LoadListPackingCallback callback);

    void getPackingItem(LoadListPackingCallback callback);

    void refreshPackingItemStatus();

    void getLastTaken(LoadStringCallback callback);

    void getMedicineCountTaken(LoadIntegerCallback callback);

    void getCountTakenBetween(Date s,Date e, LoadIntegerCallback callback);

    void getAlarmData(LoadAlarmDataCallback callback);

    void insertAlarmData(AlarmTime time);
}
