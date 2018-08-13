package com.peacecorps.malaria.data.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.peacecorps.malaria.data.db.entities.UserMedicine;

import java.util.List;

@Dao
public interface UserMedicineDao {

    @Query("SELECT * FROM UserMedicine ORDER BY timeStamp ASC")
    List<UserMedicine> getUserMedicineChoiceByTimeAsc();

    @Query("SELECT COUNT(*) FROM usermedicine WHERE month= :month and year = :year and status = :status and choice= :choice")
    int getDataForProgressBar(int month, int year, String status, String choice);

    @Insert
    void setUserMedicineSelection(UserMedicine userMedicine);

    @Query("SELECT * FROM UserMedicine WHERE month= :month and year= :year and choice= :choice")
    List<UserMedicine> getMedicationData(String choice,int month, int year);

    @Query("UPDATE UserMedicine SET status = :entry and percentage = :percentage WHERE month= :month and year= :year and date= :date")
    void updateMedicationEntry(int date, int month, int year, String entry,double percentage);

    @Query("SELECT status FROM usermedicine WHERE date= :date and month= :month and year= :year")
    String isEntered(int date,int month, int year);

    @Query("SELECT timeStamp FROM usermedicine ORDER BY Timestamp ASC LIMIT 1")
    String getFirstTimeTimeStamp();

    @Query("SELECT status FROM usermedicine WHERE date= :date and month= :month and year= :year")
    String getDailyStatus(int date,int month,int year);

    @Query("SELECT * FROM UserMedicine ORDER BY timeStamp DESC")
    List<UserMedicine> getDosesInaRow();

    @Query("DELETE FROM UserMedicine")
    void deleteTableRows();

    @Query("SELECT timeStamp FROM UserMedicine WHERE status= :status ORDER BY timeStamp ASC")
    List<String> getLastTaken(String status);

    @Query("SELECT COUNT(*) FROM UserMedicine WHERE status= :status")
    int getCountTaken(String status);

    @Query("SELECT status FROM UserMedicine WHERE date= :date and month= :month and year= :year")
    List<String> getStatusListByDateMonthYear (int date, int month, int year);

    @Query("SELECT date FROM UserMedicine WHERE month= :month and year= :year ORDER BY date ASC")
    List<Integer> getDateListByMonthYear(int month, int year);
}
