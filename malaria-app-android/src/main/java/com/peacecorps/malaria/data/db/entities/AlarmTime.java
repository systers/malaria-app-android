package com.peacecorps.malaria.data.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class AlarmTime {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hour;
    private int minute;
    private int month;
    private int year;
    private int checkDay;

    public AlarmTime(int hour, int minute, int month, int year, int checkDay) {
        this.hour = hour;
        this.minute = minute;
        this.month = month;
        this.year = year;
        this.checkDay = checkDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCheckDay() {
        return checkDay;
    }

    public void setCheckDay(int checkDay) {
        this.checkDay = checkDay;
    }
}