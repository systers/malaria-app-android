package com.peacecorps.malaria.test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.peacecorps.malaria.db.DatabaseSQLiteHelper;
import com.peacecorps.malaria.fragment.FirstAnalyticFragment;

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

    public void setUp()
    {
        RenamingDelegatingContext context =new RenamingDelegatingContext(getContext(),"test_");
        db=new DatabaseSQLiteHelper(context);
        faf=new FirstAnalyticFragment();
    }

    public void testAddEntryinIOMMEmethod()
    {
        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

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

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(1,x);


    }

    public void testDosesInaRowWithDoubleEntry(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=29;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(2,x);

    }

    public void testDosesInaRowWithDiscontinuedYesEntry(){
        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=29;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=28;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"no",percentage);

        date=27;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(2, x);



    }

    public void testDosesInaRowWithDiscontiuedDateYesEntry(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=29;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=27;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=26;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowDaily();

        long a=db.getFirstTime();

        assertEquals(2, x);
    }

    public void testDosesInaRowWithDiscontinuedBoundaryDateYesEntry(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=1;month=6;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=2;month=6;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=3;month=6;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowDaily();

        assertEquals(4,x);

    }

    public void testDosesInaRowWeeklyIrregular(){

        int date=24;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=17;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=9;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=3;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowWeekly();

        assertEquals(4,x);


    }

    public void testDosesInaRowWeeklyDiscontinued(){

        int date=30;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=23;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=16;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=3;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowWeekly();

        assertEquals(3,x);


    }

    public void testDosesInaRowWeeklyNoEntry(){

        int x = db.getDosesInaRowWeekly();

        assertEquals(0,x);


    }

    public void testDosesInaRowWeeklyIrregularDifferentMonths(){

        int date=24;
        int month=5;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=17;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=9;month=5;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=31;month=4;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=28;month=4;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        int x = db.getDosesInaRowWeekly();

        assertEquals(5,x);


    }


   public void testAdherence()
    {


        int date=30;
        int month=7;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=29;month=7;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=27;month=7;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=26;month=7;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);


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
        // formattedDate have current date/time
        //Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();

        long oneDay = 1000 * 60 * 60 * 24;

        long interval=(y-a)/oneDay+1;

        long takenCount=db.getCountTaken();

        double ar=(double)takenCount/(double)interval*100;

        //double arm= faf.updateAdherence();

        assertEquals(80.0,ar);

        /*int date=22;
        int month=6;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

         date=23;
         month=6;
         year=2015;

         percentage=45.00;

        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=27;month=6;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);
        long x=0;
        db.getFirstTime();

        Log.d("TestRunner","first run time->"+x);

        Date d = Calendar.getInstance().getTime();

        Calendar c=Calendar.getInstance();

        c.setTime(d);

        long y = c.getTimeInMillis();

        long oneDay = 1000 * 60 * 60 * 24;

        double chk=(double)(y-x)/(double)oneDay;

        double ar=faf.updateAdherence();

        assertEquals(ar,chk);

        /*int date=22;
        int month=6;
        int year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=23;month=6;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=27;month=6;year=2015;percentage=28.00;

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
       */

    }


    public void testCountTaken()
    {
        int date=28;
        int month=6;
        int year=2015;

        double percentage=00.00;

        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=7;
        month=6;
        year=2015;

        percentage=00.00;

        db.insertOrUpdateMissedMedicationEntry(date,month,year,percentage);

        db.updateMedicationEntry(date, month,year,"yes",percentage);

        int x= db.getCountTaken();


        assertEquals(2,x);

    }


    public void testWeekdays()
    {
        int date=30;
        int month=12;
        int year=2014;

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
        int date=30;
        int month=12;
        int year=2014;

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
        int date=1;
        int month=8;
        int year=2015;

        Date s=db.getDateObject(""+year+"-"+month+"-"+date);

        date=15;
        month=8;
        year=2015;

        Date e = db.getDateObject(""+year+"-"+month+"-"+date);

        date=2;
        month=8;
        year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=3;month=8;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=4;month=8;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=6;month=8;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);


        int cnt=db.getCountTakenBetween(s,e);

        assertEquals(4,cnt);

    }

    public void testgetCountTakenBetweenBoundary()
    {
        int date=1;
        int month=8;
        int year=2015;

        Date s=db.getDateObject(""+year+"-"+month+"-"+date);

        date=15;
        month=8;
        year=2015;

        Date e = db.getDateObject(""+year+"-"+month+"-"+date);

        date=1;
        month=8;
        year=2015;

        double percentage=23.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);

        date=3;month=8;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=4;month=8;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date,month,year,"yes",percentage);

        date=15;month=8;year=2015;percentage=28.00;

        db.insertOrUpdateMissedMedicationEntry(date, month, year, percentage);

        db.updateMedicationEntry(date, month, year, "yes", percentage);


        int cnt=db.getCountTakenBetween(s,e);

        assertEquals(4,cnt);

    }





}
