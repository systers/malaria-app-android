package com.peacecorps.malaria.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.peacecorps.malaria.code.model.SharedPreferenceStore;
import com.peacecorps.malaria.utils.CalendarFunction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.peacecorps.malaria.utils.CalendarFunction.getDateObject;
import static com.peacecorps.malaria.utils.CalendarFunction.getHumanDateFormat;

/**
 * Created by Chimdi on 7/18/14.
 * Edited by Ankita
 **/
public class DatabaseSQLiteHelper extends SQLiteOpenHelper {

    // database name and table names
    private static final String DATABASE_NAME = "MalariaDatabase";
    private static final String userMedicationChoiceTable = "userSettings";
    private static final String appSettingTable = "appSettings";
    private static final String locationTable = "locationSettings";
    private static final String packingTable = "packingSettings";

    // tag
    private static final String TAGDSH= "DatabaseSQLiteHelper";

    public static final String LOCATION = "Location";
    public static final String PACKING_ITEM = "PackingItem";
    public static final String KEY_ROW_ID = "_id";
    public static final String QUANTITY = "Quantity";

    // constructor
    public DatabaseSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // creating tables
    @Override
    public void onCreate(SQLiteDatabase database) {
        /**Creating Tables**/
        database.execSQL("CREATE TABLE " + userMedicationChoiceTable + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, Drug INTEGER,Choice VARCHAR, Month VARCHAR, Year VARCHAR,Status VARCHAR,Date INTEGER,Percentage DOUBLE, Timestamp VARCHAR);");
        database.execSQL("CREATE TABLE " + appSettingTable + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, Drug VARCHAR, Choice VARCHAR, WeeklyDay INTEGER, FirstTime LONG, FreshInstall VARCHAR);");
        database.execSQL("CREATE TABLE " + locationTable + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, Location VARCHAR, Times INTEGER);");
        database.execSQL("CREATE TABLE " + packingTable + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, PackingItem VARCHAR, Quantity INTEGER, Status VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {


    }

    public static ArrayList<Double> percentage;
    public static ArrayList<Integer> date;

    /**Method to Update the Progress Bars**/
    // only returning count value
    public int getData(int month, int year, String choice) {

        percentage = new ArrayList<Double>();
        date = new ArrayList<Integer>();
        int count = 0;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String column[] = {"_id", "Date", "Percentage"};
        String args[] = {"" + month, "" + year, "yes", choice};
        Cursor cursor = sqLiteDatabase.query(userMedicationChoiceTable, column, "Month =? AND Year =? AND Status =? AND Choice =?", args,
                null, null,"Date ASC");
        boolean isDataFound = false;
        while (cursor.moveToNext()) {
            isDataFound = true;
            count += 1;
            date.add(cursor.getInt(1));

            percentage.add(cursor.getDouble(2));
            Log.d(TAGDSH, "INSIDE GET DATA DATE: " + cursor.getInt(1));
            Log.d(TAGDSH,"INSIDE GET DATA PERCENTAGE:"+cursor.getDouble(2));

        }
        if (isDataFound) {
            if (!(date.get(date.size() - 1) == Calendar.getInstance().get(Calendar.DATE))) {
                percentage.add(0.0);
                date.add(Calendar.getInstance().get(Calendar.DATE));
            }
        }
        sqLiteDatabase.close();
        Log.d(TAGDSH, "" + count);
        return count;
    }

    /**Method to Update the User Selection of Medicine and it's Status of whether Medicine was taken or not.
     * Used in Alert Dialog to Directly update the current Status
     * Used in Home Screen Fragment for updating the current status through tick marks**/
    public void getUserMedicationSelection(Context context, String choice, Date date, String status, Double percentage) {
        ContentValues values = new ContentValues(2);
        Calendar cal;
        cal = Calendar.getInstance();
        cal.setTime(date);
        int d=cal.get(Calendar.DATE);
        String ts="";
        if(d>=10)
        {
            ts=""+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DATE);
        }
        else {
            ts=""+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-0"+cal.get(Calendar.DATE);
        }
        String []args={""+ts};


        Log.d(TAGDSH,ts);
        values.put("Drug", SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drug", 0));
        values.put("Choice", choice);
        values.put("Month", "" + cal.get(Calendar.MONTH));
        values.put("Year", "" + cal.get(Calendar.YEAR));
        values.put("Status", status);
        values.put("Date", cal.get(Calendar.DATE));
        values.put("Percentage", percentage);
        values.put("Timestamp",ts);
        this.getWritableDatabase().insert(userMedicationChoiceTable, "medication", values);
        this.getWritableDatabase().close();

    }

    /*Method to Be used in Future for storing appSettings directly in the Database, decreasing complexity**/
    public void insertAppSettings(String drug, String choice, long date)
    {
        SQLiteDatabase sqDB= getWritableDatabase();
        String [] column={"FreshInstall"};
        ContentValues cv =new ContentValues(2);
        Cursor cursor=sqDB.query(appSettingTable,column,null,null,null,null,"_id ASC LIMIT 1");
        cursor.moveToNext();
        try {

            if (cursor.getString(0).compareTo("true")==0)
            {
                cv.put("Drug", drug);
                cv.put("Choice", choice);
                cv.put("FirstTime", date);
                Calendar c =Calendar.getInstance();
                c.setTimeInMillis(date);
                int w=c.get(Calendar.DAY_OF_WEEK);
                cv.put("WeeklyDay",w);
                cv.put("FreshInstall","true");
                String [] args={"1"};
                sqDB.delete(appSettingTable,"_id = ?",args);
                sqDB.insert(appSettingTable,"settings",cv);
            }

        }
        catch(Exception e)
        {   cv.put("Drug", drug);
            cv.put("Choice", choice);
            cv.put("FirstTime", date);
            Calendar c =Calendar.getInstance();
            c.setTimeInMillis(date);
            int w=c.get(Calendar.DAY_OF_WEEK);
            cv.put("WeeklyDay", w);
            cv.put("FreshInstall", "true");
            String [] args={"1"};
            sqDB.insert(appSettingTable,"settings",cv);
        }
    }


    /**Getting Medication Data of Each Day in Day Fragment Activity**/
    public String getMedicationData(int date,int month, int year) {
        SQLiteDatabase sqDB = getReadableDatabase();
        String choice ="daily";
        String[] columns = {"_id", "Date", "Percentage", "Status"};
        String[] selArgs = {"" + month, "" + year, choice};

        StringBuffer buffer = new StringBuffer();
        Cursor cursor = sqDB.query(userMedicationChoiceTable, columns, "Month =? AND Year =? AND Choice =?", selArgs, null, null, null, null);
        int idx0,idx1,idx2;

         /**Queried for a Month in an Year, stopping when the date required is found**/
        while (cursor.moveToNext()) {

             idx0 = cursor.getColumnIndex("Date");
             idx1 = cursor.getColumnIndex("Percentage");
             idx2 = cursor.getColumnIndex("Status");
            int d= cursor.getInt(idx0);
            String ch = cursor.getString(idx2);

            Log.d(TAGDSH,"Passed Date:"+date+"Found date:"+d);

            Log.d(TAGDSH,""+year);

            if(d==date)
            {
                buffer.append(ch);
            }
        }
        sqDB.close();
        return buffer.toString();
    }

    /**Method to Modify the entry of Each Day**/
    public void updateMedicationEntry(int date, int month, int year, String entry,double percentage){

        SQLiteDatabase sqDB = getReadableDatabase();
        ContentValues values = new ContentValues(2);

        values.put("Status", entry);
        values.put("Percentage", percentage);

        String[] args = new String[]{String.valueOf(date), String.valueOf(month),String.valueOf(year)};
        String[] column = {"Percentage"};

        // Update is used instead of Insert, because the entry already exist**/
        sqDB.update(userMedicationChoiceTable, values, "Date=? AND Month=? AND YEAR=?", args);
        //Cursor cursor=sqDB.query(userMedicationChoiceTable,column,null,null,null,null,null);
//        while(cursor.moveToNext())
//        {
//         Log.d(TAGDSH, "Percentage:" + cursor.getDouble(0));
//        }
        sqDB.close();

    }

    /*If No Entry will be found it will enter in the database, so that it can be later updated.
     * Usage is in Day Fragment Activity **/
    // take drug value too from prefernces, hence in param
    // send preference value too in parameters Is Weekly -- calculation of Choice
    public void insertOrUpdateMissedMedicationEntry(int date, int month, int year,double percentage)
    {
        SQLiteDatabase sqDB = this.getWritableDatabase();
        ContentValues cv = new ContentValues(2);
        String Choice="",ts="";
        if(SharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly",false))
            Choice="weekly";
        else
            Choice="daily";

        // calculation of timestamp
        if(date>=10)
            ts=""+year+"-"+month+"-"+date;
        else
            ts=""+year+"-"+month+"-0"+date;

        String []columns={"Status"};
        String []selArgs= {""+date,""+month,""+year};
        Cursor cursor = sqDB.query(userMedicationChoiceTable, columns, "Date=? AND Month =? AND Year =?", selArgs,
                null, null, null, null);

        int idx0; String st=""; int flag=0;
        while(cursor.moveToNext())
        {
            // st is status where date, month and year given is same in database & if such value exists, put flag = 1
            idx0=cursor.getColumnIndex("Status");
            st=cursor.getString(idx0);
            flag=1;
        }

        Log.d(TAGDSH, "" + year);
        if(flag==0 && st.equalsIgnoreCase(""))
        {
            cv.put("Drug", SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drug", 0));
            cv.put("Choice", Choice);
            cv.put("Month", "" + month);
            cv.put("Year", "" + year);
            cv.put("Date", date);
            cv.put("Percentage", percentage);
            cv.put("Timestamp", ts);
            sqDB.insert(userMedicationChoiceTable, "medicaton", cv);

            String []col ={"Date"};
            String []arg = {""+month,""+year};
            Cursor crsr = sqDB.query(userMedicationChoiceTable,col,"Month =? AND Year =?",arg,null,null,"Date ASC");
            int count=1,p,lim,ft=0;
            while (cursor.moveToNext())
            {
                p = crsr.getInt(0);
                count++;
                if(count==1)
                {
                    ft=p;
                }
                if(count==2)
                {
                   lim=p-ft;
                   for (int i=1;i<lim;i++)
                   {   if((date+i)>=10)
                       ts=""+year+"-"+month+"-"+(date+i);
                       else
                       ts=""+year+"-"+month+"-0"+(date+i);
                       cv.put("Drug", SharedPreferenceStore.mPrefsStore.getInt("com.peacecorps.malaria.drug", 0));
                       cv.put("Choice", Choice);
                       cv.put("Month", "" + month);
                       cv.put("Year", "" + year);
                       cv.put("Date", date+i);
                       cv.put("Percentage", percentage);
                       cv.put("Timestamp", ts);
                       sqDB.insert(userMedicationChoiceTable, "medicaton", cv);
                   }

                }

            }



        }
        sqDB.close();

    }

    /*Is Entered is Used for Getting the Style of Each Calendar Grid Cell According to the Medication Status Taken or Not Taken*/
    public int isEntered(int date,int month, int year)
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String column[] = {"Status"};
        String args[] = {""+date,"" + month, ""+year};
        Cursor cursor = sqDB.query(userMedicationChoiceTable, column, "Date=? AND Month =? AND Year =?", args, null, null, null, null);
        int idx=0;
        String status="";
        while(cursor.moveToNext())
        {
            idx = cursor.getColumnIndex("Status");
            status=cursor.getString(idx);
            if(status!=null) {
                if (status.equalsIgnoreCase("yes"))
                    return 0;
                else if (status.equalsIgnoreCase("no"))
                    return 1;
            }

        }
        sqDB.close();
        return 2;


    }

    /**Getting the oldest registered entry of Pill**/
    public long getFirstTime() {
        SQLiteDatabase sqDB = getWritableDatabase();
        String column[]={"Timestamp"};
        Cursor cursor = sqDB.query(userMedicationChoiceTable,column,null,null,null,null,"Timestamp ASC LIMIT 1");
        int idx=0; String selected_date=""; long firstRunTime=0;
        while (cursor.moveToNext())
        {
            idx=cursor.getColumnIndex("Timestamp");
            selected_date=cursor.getString(idx);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date comp_date=Calendar.getInstance().getTime();
            try {
                comp_date   = sdf.parse(selected_date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAGDSH,"First Time: "+selected_date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(comp_date);
            firstRunTime=cal.getTimeInMillis();
        }
        sqDB.close();
        return firstRunTime;
    }

    /**Getting the Status of Each Day, like whether the Medicine was taken or not.
     * Usages in Alert Dialog Fragment for geting the status of pill for setting up Reminder
     * Usages in Day Fragment Activity for getting the previous status of day before updating it as not taken. **/
    public String getStatus(int date,int month,int year){

        SQLiteDatabase sqDB = getWritableDatabase();
        String []column = {"Status"};
        String []selArgs = {""+date,""+month,""+year};
        Cursor cursor= sqDB.query(userMedicationChoiceTable,column,"Date =? AND Month =? AND Year =?",
                selArgs,null,null,null,null);

        while(cursor.moveToNext())
        {
            return cursor.getString(0);
        }

        return "miss";
    }


    /**From the Last Time Pill was Taken it Calculates the maximum days in a row medication was taken
     * Need at Home Screen, First Analytic Scrren, Second Analytic Scrren, Day Fragment Screen
     * Main Activity for updating the dosesInArow as it changes according to the status we enter.**/
    public int getDosesInaRowDaily()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"Status","Timestamp","Date","Month","Year","Choice"};
        Cursor cursor= sqDB.query(userMedicationChoiceTable,column,null,null,null,null,"Timestamp DESC");
        int dosesInaRow=0,prevDate=0,currDate=0,currDateMonth=0,prevDateMonth=0,prevDateYear=0,currDateYear=0;
        String ts="";
        /**One Iteration is done before entering the while loop for updating the previous and current date**/
        if(cursor!=null) {
            cursor.moveToNext();
            if (cursor != null) {
                try {
                    ts = cursor.getString(cursor.getColumnIndex("Timestamp"));
                    currDate = cursor.getInt(2);
                    Log.d(TAGDSH, "curr date 1->" + ts);
                } catch (Exception e) {
                    return 0;
                }

                if(cursor.getString(0)!=null){
                    if (cursor.getString(0).compareTo("yes") == 0){
                        prevDate = cursor.getInt(2);
                        prevDateMonth = cursor.getInt(3);
                        if (Math.abs(currDate - prevDate) <= 1)
                            dosesInaRow++;
                    }
                }

                /**Since Previous and Current Date our Updated,
                 * Now backwards scan is done till we receive consecutive previous and current date **/
                while (cursor != null && cursor.moveToNext()) {
                    currDate = cursor.getInt(2);
                    currDateMonth = cursor.getInt(3);
                    currDateYear = cursor.getInt(4);
                    ts = cursor.getString(cursor.getColumnIndex("Timestamp"));

                    Log.d(TAGDSH, "curr date ->" + ts);
                    int parameter = Math.abs(currDate - prevDate);
                    if ((cursor.getString(0)) != null) {
                        if (currDateMonth == prevDateMonth) {
                            if (cursor.getString(0).compareTo("yes") == 0 && parameter == 1)
                                dosesInaRow++;
                            else
                                break;
                        } else {
                            parameter = Math.abs(currDate - prevDate) % (CalendarFunction.getNumberOfDaysInMonth(currDateMonth, currDateYear) - 1);
                            if (cursor.getString(0).compareTo("yes") == 0 && parameter <= 1)
                                dosesInaRow++;
                            else
                                break;

                        }
                    }
                    Log.d(TAGDSH, "Doses in Row->" + dosesInaRow);
                    prevDate = currDate;
                    prevDateMonth = currDateMonth;
                }
            }
        }
        Log.d(TAGDSH, "Doses in Row->" + dosesInaRow);
        sqDB.close();
        return dosesInaRow;
    }


    /**From the Last Time Pill was Taken it Calculates the maximum weeks in a row medication was taken
     * Need at Home Screen, First Analytic Scrren, Second Analytic Scrren, Day Fragment Screen
     * Main Activity for updating the dosesInArow as it changes according to the status we enter.**/
    public int getDosesInaRowWeekly()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"Status","Timestamp","Date","Month","Year"};
        Cursor cursor= sqDB.query(userMedicationChoiceTable,column,null,null,null,null,"Timestamp DESC");
        int dosesInaRow=1,aMonth=0,pMonth=0;
        Date ado,pdo;
        int pPara=0;
        long aPara=0;
        int numDays=0;
        String ats="",pts="";
        if(cursor!=null) {
            cursor.moveToNext();
            if(cursor!=null) {
                try {
                    ats = cursor.getString(1);
                }
                catch (CursorIndexOutOfBoundsException e)
                {
                    return 0;
                }
                aMonth = cursor.getInt(3) + 1;
                ats = getHumanDateFormat(ats, aMonth);
                ado = getDateObject(ats);
                while (cursor.moveToNext()) {
                    pts = cursor.getString(1);
                    pMonth = cursor.getInt(3) + 1;
                    pts = getHumanDateFormat(pts, pMonth);
                    pdo = getDateObject(pts);
                    numDays = CalendarFunction.getDayofWeek(pdo);
                    pPara = 7 - numDays + 7;
                    aPara = CalendarFunction.getNumberOfDays(pdo, ado);
                    if (aPara <= pPara)
                        dosesInaRow++;
                    else
                        break;
                    ats = pts;
                    ado = pdo;
                }
            }
        }
        return dosesInaRow;


    }

    public String getMediLastTakenTime()
    {
        SQLiteDatabase sqDB=getWritableDatabase();
        String [] column={"Date","Month","Year"};
        String recentDate="";
        Cursor cursor = sqDB.query(userMedicationChoiceTable, column, null, null,
                null, null, "Timestamp DESC LIMIT 1");
        if(cursor!=null)
        { cursor.moveToNext();
            try
            {
                recentDate=cursor.getString(0)+"/"+cursor.getString(1);
            }
            catch (Exception e)
            {
                return "";
            }
        }      sqDB.close();
        return recentDate;
    }

    /*Deleting the Database*/
    public void resetDatabase()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        sqDB.delete(userMedicationChoiceTable,null,null);
        sqDB.delete(appSettingTable,null,null);
        sqDB.delete(locationTable,null,null);
        sqDB.delete(packingTable,null,null);
        sqDB.close();
    }

    /**Inseting the location for maintaining Location History**/
    public void insertLocation(String location)
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        ContentValues cv =new ContentValues(2);
        int a=0,flag=0;
        cv.put("Location", location);

        String [] columns = {"Location","Times"};
        String [] selArgs = {""+location};

        Cursor cursor = sqDB.query(locationTable,columns,"Location = ?",selArgs,null,null,null);

        while (cursor.moveToNext())
        {
            a= cursor.getInt(1);
            a++;
            flag=1;
        }
        cv.put("Times", a);

        if(flag==1)
            sqDB.update(locationTable, cv, "Location= ?", selArgs);
        else
            sqDB.insert(locationTable, "location", cv);


    }

    /**Fetching the Location**/
    public Cursor getLocation()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"_id","Location"};

        return sqDB.query(locationTable, column,
                null, null, null, null,
                KEY_ROW_ID + " asc ");
    }

    /**Inserting the Packing Item in DataBase when using Add Item Edit Text**/
    public void insertPackingItem(String pItem,int quantity, String status)
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        ContentValues cv =new ContentValues(2);
        int flag=0,q=0;
        cv.put("PackingItem",pItem);
        cv.put("Status",status);

        String [] columns = {"PackingItem","Quantity","Status"};
        String [] selArgs = {""+pItem};

        Cursor cursor = sqDB.query(packingTable,columns,"PackingItem = ?",selArgs,null,null,null);

        while (cursor.moveToNext())
        {
            q= cursor.getInt(1);
            flag++;
            Log.d(TAGDSH,"Flag: "+flag);
        }


        if(flag==1) {

            cv.put("Quantity", quantity);
            sqDB.update(packingTable, cv, "PackingItem= ?", selArgs);
        }
        else {
            cv.put("Quantity", quantity);
            sqDB.insert(packingTable, "item", cv);
        }


    }

    /**Fetching the Packing Item to be taken**/
    public Cursor getPackingItemChecked()
    {

        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"_id","PackingItem","Quantity"};
        String []selArgs={"yes"};
        /*return sqDB.query(packingTable, column,
                null, null, null, null,
                KEY_ROW_ID + " asc ");*/


        Cursor cursor = sqDB.query(packingTable,column,"Status= ?",selArgs,null,null,KEY_ROW_ID+" asc ");

        return cursor;

    }

    /**Fetching the list of Packing Item from which one can be chosen**/
    public Cursor getPackingItem()
    {

        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"_id","PackingItem","Quantity"};
        return sqDB.query(packingTable, column,
                null, null, null, null,
                KEY_ROW_ID + " asc ");

    }


    /**Refreshing the status of each packing item to its original state**/
    public void refreshPackingItemStatus()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"_id","PackingItem"};
        String pItem="";
        String []selArgs={pItem};
        ContentValues cv = new ContentValues(2);
        cv.put("Status","no");
        Cursor cursor= sqDB.query(packingTable, column,
                null, null, null, null,
                KEY_ROW_ID + " asc ");

        while (cursor.moveToNext())
        {
            try {
                selArgs[0]=cursor.getString(1);
                sqDB.update(packingTable,cv,"PackingItem=?",selArgs);
            }
            catch (Exception e)
            {
                break;
            }
        }
    }


    /**Finding the Last Date the Drug was taken**/
    public String getLastTaken()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"Status","Timestamp","Date","Month","Year","Choice"};
        Cursor cursor= sqDB.query(userMedicationChoiceTable,column,null,null,null,null,"Timestamp ASC");
        String lastDate="";
        if(cursor!=null)
        {
            while (cursor.moveToNext())
            {
                try {
                    if (cursor.getString(0).equalsIgnoreCase("yes")) {
                        lastDate=cursor.getString(1);
                    }
                }
                catch(NullPointerException npe)
                {
                    return "";

                }
            }
        }
        sqDB.close();
        return lastDate;



    }

    /**Finding the No. of Drugs**/
    public int getCountTaken()
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"Status","Timestamp","Date","Month","Year","Choice"};
        Cursor cursor= sqDB.query(userMedicationChoiceTable,column,null,null,null,
                null,"Timestamp ASC");
        int count=0;
        if(cursor!=null)
        {
            while (cursor.moveToNext())
            {
                try {
                    if (cursor.getString(0).equalsIgnoreCase("yes") == true) {
                        count++;
                        Log.d(TAGDSH,"Counter :"+count);
                    }
                }
                catch(NullPointerException npe)
                {
                    return 0;

                }
            }
        }
        sqDB.close();
        return count;



    }

    /**Finding the Drugs between two dates for updating Adherence in Day Fragment Activity of any selected date**/
    public int getCountTakenBetween(Date s,Date e)
    {
        SQLiteDatabase sqDB = getWritableDatabase();
        String []column={"Status","Timestamp","Date","Month","Year","Choice"};
        Cursor cursor= sqDB.query(userMedicationChoiceTable,column,null,null,null,null,"Timestamp ASC");
        int count=0;

        if(cursor!=null)
        {
            while (cursor.moveToNext())
            {
                    String d= cursor.getString(1);
                    Log.d(TAGDSH,"Curr Time:"+d);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date curr= Calendar.getInstance().getTime();
                    try{

                        curr=sdf.parse(d);
                    }
                    catch (Exception ex)
                    {
                    }
                    Log.d(TAGDSH,e.toString());
                    Log.d(TAGDSH,s.toString());
                    long currt=curr.getTime();
                    long endt=e.getTime();

                    Calendar cal =Calendar.getInstance();
                    cal.setTime(s);
                    cal.add(Calendar.MONTH, 1);
                    s=cal.getTime();
                    long strt=s.getTime();

                    Log.d(TAGDSH,"Current Long:"+currt);
                    Log.d(TAGDSH,"End Long:"+endt);
                    Log.d(TAGDSH,"Start Long:"+strt);

                    if (cursor.getString(0).equalsIgnoreCase("yes")) {

                        if (currt >= strt && currt <= endt)
                            count++;
                        else if (strt == endt) {
                            count++;
                        }
                    }
            }
        }
        sqDB.close();
        return count;


    }



}