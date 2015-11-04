package com.peacecorps.malariatest;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;
import android.widget.Toast;

import com.peacecorps.malaria.DatabaseSQLiteHelper;
import com.peacecorps.malaria.FirstAnalyticFragment;
import com.peacecorps.malaria.SharedPreferenceStore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ankita on 7/7/2015.
 */
public class MalariaDatabaseTest extends AndroidTestCase {

    private DatabaseSQLiteHelper db;
    private FirstAnalyticFragment faf;
	int date=0,month=0,year=0;
	double percentage=0.0;
	String yesResponse="yes";

    public void setUp()
    {
        RenamingDelegatingContext context =new RenamingDelegatingContext(getContext(),"test_");
        db=new DatabaseSQLiteHelper(context);
        faf=new FirstAnalyticFragment();
    }

    public void testAddEntryinIOMMEmethod()
    {
        date=30;
        month=5;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);
        long x=db.getFirstTime();
        String date_header=""+date+"/"+month+"/"+year;
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d = Calendar.getInstance().getTime();
        try{
            d=sdf.parse(date_header);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        Calendar c=Calendar.getInstance();
        c.setTime(d);
        long y = c.getTimeInMillis();
        assertEquals(x,y);
    }

    public void testDosesInaRowWithNoEntry(){
        int x = db.getDosesInaRowDaily();
        assertEquals(0,x);
    }

    public void testDosesInaRowWithASingleEntry(){
        date=30;
        month=5;
        year=2015;
        double percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        int x = db.getDosesInaRowDaily();
        assertEquals(1,x);
    }

    public void testDosesInaRowWithDoubleEntry(){

            date=30;
            month=5;
            year=2015;
            percentage=23.00;
            db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
 			db.updateMedicationEntry(date, month, year, yesResponse, percentage);
            date=29;month=5;year=2015;percentage=28.00;
            db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
            db.updateMedicationEntry(date,month,year,yesResponse,percentage);
            int x = db.getDosesInaRowDaily();
            assertEquals(2,x);
    }

    public void testDosesInaRowWithDiscontinuedYesEntry(){
            date=30;
            month=5;
            year=2015;
            percentage=23.00;
            db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
            db.updateMedicationEntry(date, month, year, yesResponse, percentage);
            date=29;month=5;year=2015;percentage=28.00;
			db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
            db.updateMedicationEntry(date,month,year,yesResponse,percentage);
			date=28;month=5;year=2015;percentage=28.00;
            db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
            db.updateMedicationEntry(date,month,year,"no",percentage);
            date=27;month=5;year=2015;percentage=28.00;
            db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
            db.updateMedicationEntry(date, month, year, yesResponse, percentage);
            int x = db.getDosesInaRowDaily();
            assertEquals(2, x);
    }

    public void testDosesInaRowWithDiscontiuedDateYesEntry(){
        date=30;
        month=5;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year,yesResponse, percentage);
		date=29;month=5;year=2015;percentage=28.00;
		db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
	    db.updateMedicationEntry(date,month,year,yesResponse,percentage);
		date=27;month=5;year=2015;percentage=28.00;
	    db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
	    db.updateMedicationEntry(date,month,year,yesResponse,percentage);
	    date=26;month=5;year=2015;percentage=28.00;
	    db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
	    db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        int x = db.getDosesInaRowDaily();
        long a=db.getFirstTime();
        assertEquals(2, x);
    }

    public void testDosesInaRowWithDiscontinuedBoundaryDateYesEntry(){
        date=30;
        month=5;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=1;month=6;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=2;month=6;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=3;month=6;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year,yesResponse, percentage);
        x = db.getDosesInaRowDaily();
        assertEquals(4,x);
    }

    public void testDosesInaRowWeeklyIrregular(){
        date=24;
        month=5;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=17;month=5;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=9;month=5;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=3;month=5;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        int x = db.getDosesInaRowWeekly();
        assertEquals(4,x);
    }

    public void testDosesInaRowWeeklyDiscontinued(){
        date=30;
        month=5;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=23;month=5;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=16;month=5;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=3;month=5;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year,yesResponse, percentage);
        int x = db.getDosesInaRowWeekly();
        assertEquals(3,x);
    }

    public void testDosesInaRowWeeklyNoEntry(){
        int x = db.getDosesInaRowWeekly();
        assertEquals(0,x);
    }

    public void testDosesInaRowWeeklyIrregularDifferentMonths(){
        date=24;
        month=5;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=17;month=5;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=9;month=5;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=31;month=4;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=28;month=4;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        int x = db.getDosesInaRowWeekly();
        assertEquals(5,x);
    }
   public void testAdherence()
    {
        date=30;
        month=7;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=29;month=7;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=27;month=7;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=26;month=7;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, "yesResponse", percentage);
        long a=db.getFirstTime();
        date=30;month=7;year=2015;
        String date_header=""+date+"/"+month+"/"+year;
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
        Date d = Calendar.getInstance().getTime();
        try{
            d=sdf.parse(date_header);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        Calendar c=Calendar.getInstance();
        c.setTime(d);
        long y = c.getTimeInMillis();
        long today= Calendar.getInstance().getTimeInMillis();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(Calendar.getInstance().getTime());
        long oneDay = 1000 * 60 * 60 * 24;
        long interval=(y-a)/oneDay+1;
        long takenCount=db.countTaken();
        double ar=(double)takenCount/(double)interval*100;
        assertEquals(80.0,ar);
    }


    public void testCountTaken()
    {
        date=28;
        month=6;
        year=2015;
        percentage=00.00;
        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=7;
        month=6;
        year=2015;
        percentage=00.00;
        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);
        db.updateMedicationEntry(date, month,year,yesResponse,percentage);
        int x= db.countTaken();
        assertEquals(2,x);
    }


    public void testWeekdays()
    {
        date=30;
        month=12;
        year=2014;
        Date s=db.getDateObject(""+year+"-"+month+"-"+date);
        date=6;
        month=1;
        year=2015;
        Date e = db.getDateObject(""+year+"-"+month+"-"+date);
        int x= db.getIntervalWeekly(s, e, 3);
        assertEquals(2,x);
    }

    public void testDailyDays()
    {
        date=30;
        month=12;
        year=2014;
        Date s=db.getDateObject(""+year+"-"+month+"-"+date);
        date=6;
        month=1;
        year=2015;
        Date e = db.getDateObject(""+year+"-"+month+"-"+date);
        int x= db.getIntervalDaily(s, e);
        assertEquals(8,x);
    }


    public void testgetCountTakenBetween()
    {
        date=1;
        month=8;
        year=2015;
        Date s=db.getDateObject(""+year+"-"+month+"-"+date);
        date=15;
        month=8;
        year=2015;
        Date e = db.getDateObject(""+year+"-"+month+"-"+date);
        date=2;
        month=8;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=3;month=8;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=4;month=8;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=6;month=8;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        int cnt=db.getCountTakenBetween(s,e);
        assertEquals(4,cnt);
    }

    public void testgetCountTakenBetweenBoundary()
    {
        date=1;
        month=8;
        year=2015;
        Date s=db.getDateObject(""+year+"-"+month+"-"+date);
        date=15;
        month=8;
        year=2015;
        Date e = db.getDateObject(""+year+"-"+month+"-"+date);
        date=1;
        month=8;
        year=2015;
        percentage=23.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        date=3;month=8;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=4;month=8;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date,month,year,yesResponse,percentage);
        date=15;month=8;year=2015;percentage=28.00;
        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);
        db.updateMedicationEntry(date, month, year, yesResponse, percentage);
        int cnt=db.getCountTakenBetween(s,e);
        assertEquals(4,cnt);

    }





}
