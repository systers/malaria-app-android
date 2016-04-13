package com.peacecorps.malaria;

import java.util.Date;

/**
 * Created by yogesh on 12/4/16.
 */
public class Utils {

    public static final String getDayFromDate(Date date) {
        String day = (String) android.text.format.DateFormat.format("dd", date); //20
        return day;
    }
    public static final String getMonthFromDate(Date date) {
        String intMonth = (String) android.text.format.DateFormat.format("MM", date); //06
        if (Integer.parseInt(intMonth) <10){
            return intMonth.substring(1, intMonth.length());
        }else {
            return intMonth;
        }
    }
    public static final String getMonthNameFromDate(Date date) {
        String stringMonth = (String) android.text.format.DateFormat.format("MMMM", date); //Jun
        return stringMonth;
    }
    public static final String getYearFromDate(Date date) {
        String year = (String) android.text.format.DateFormat.format("yyyy", date); //2013
        return year;
    }
    public static final String getDayNameFromDate(Date date) {
        String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);//Thursday
        return dayOfTheWeek;
    }
}
