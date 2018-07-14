package com.peacecorps.malaria.data.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.peacecorps.malaria.data.db.dao.*;
import com.peacecorps.malaria.data.db.entities.*;
import com.peacecorps.malaria.utils.AppExecutors;
import com.peacecorps.malaria.utils.CalendarFunction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.peacecorps.malaria.utils.CalendarFunction.getDateObject;
import static com.peacecorps.malaria.utils.CalendarFunction.getHumanDateFormat;

public class AppDbHelper implements DbHelper {

    private static volatile AppDbHelper INSTANCE;

    private AppSettingDao appSettingDao;
    private LocationDao locationDao;
    private PackingDao packingDao;
    private UserMedicineDao userMedicineDao;
    private AlarmDao alarmDao;
    private AppExecutors appExecutors;

    // prevent direct instantiation
    private AppDbHelper(@NonNull AppExecutors appExecutors, @NonNull AppSettingDao appSettingDao, @NonNull LocationDao locationDao,
                        @NonNull PackingDao packingDao, @NonNull UserMedicineDao userMedicineDao, @NonNull AlarmDao alarmDao) {
        this.appExecutors = appExecutors;
        this.locationDao = locationDao;
        this.packingDao = packingDao;
        this.userMedicineDao = userMedicineDao;
        this.appSettingDao = appSettingDao;
        this.alarmDao = alarmDao;
    }

    // returns a singleton instance
    public static AppDbHelper getInstance (@NonNull AppExecutors appExecutors, @NonNull AppSettingDao appSettingDao, @NonNull LocationDao locationDao,
                                           @NonNull PackingDao packingDao, @NonNull UserMedicineDao userMedicineDao, @NonNull AlarmDao alarmDao) {
        if(INSTANCE == null) {
            synchronized ( (AppDbHelper.class)) {
                if( INSTANCE == null) {
                    INSTANCE = new AppDbHelper(appExecutors, appSettingDao, locationDao, packingDao, userMedicineDao, alarmDao);
                }
            }
        }
        return INSTANCE;
    }


    public static void clearInstance() {
        INSTANCE = null;
    }


    /**Method to Update the Progress Bars**/
    @Override
    public void getCountForProgressBar(final int month, final int year, final String status, final String choice, final LoadIntegerCallback callback) {
        Runnable countRunnable = new Runnable() {
            @Override
            public void run() {
                final int value = userMedicineDao.getDataForProgressBar(month, year, status, choice);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(value);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(countRunnable);

    }

    /**Method to Update the User Selection of Medicine and it's Status of whether Medicine was taken or not.
     * Used in Alert Dialog to Directly update the current Status
     * Used in Home Screen Fragment for updating the current status through tick marks**/
    @Override
    public void setUserMedicineSelection(int drug, String choice, Date date, String status, Double percentage) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String ts="";
        if((cal.get(Calendar.DATE)) >=10)
        {
            ts=""+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DATE);
        }
        else {
            ts=""+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-0"+cal.get(Calendar.DATE);
        }
        final UserMedicine userMedicine = new UserMedicine(drug, choice, cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.DATE), status, percentage, ts);
        Runnable medicineRunnable = new Runnable() {
            @Override
            public void run() {
                userMedicineDao.setUserMedicineSelection(userMedicine);
            }
        };
        appExecutors.diskIO().execute(medicineRunnable);
    }

    /*Method to Be used in Future for storing appSettings directly in the Database, decreasing complexity**/
    @Override
    public void insertAppSettings(String drug, String choice, long date) {
        Calendar c =Calendar.getInstance();
        c.setTimeInMillis(date);
        int w=c.get(Calendar.DAY_OF_WEEK);
        final AppSetting appSetting = new AppSetting(drug, choice,w , date, "true");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String value = appSettingDao.checkFirstInstall();
                if(value.compareTo("true") == 0) {
                    appSettingDao.deleteFirstRow();
                    appSettingDao.insertAppSettings(appSetting);
                } else {
                    appSettingDao.insertAppSettings(appSetting);
                }
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**Getting Medication Data of Each Day in Day Fragment Activity**/
    @Override
    public void getMedicationData(final int date, final int month, final int year, final LoadStringCallback callback) {
        final StringBuffer buffer = new StringBuffer();
        Runnable medicationRunnable = new Runnable() {
            @Override
            public void run() {
                List<UserMedicine> userMedicines = userMedicineDao.getMedicationData("daily", month, year);
                for(UserMedicine medicine: userMedicines) {
                    int d = medicine.getDate();
                    String ch = medicine.getStatus();
                    if(d == date) {
                        buffer.append(ch);
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onDataLoaded(buffer.toString());
                            }
                        });
                    }
                }
            }
        };
        appExecutors.diskIO().execute(medicationRunnable);
    }

    /**Method to Modify the entry of Each Day**/
    @Override
    public void updateMedicationEntry(final int date, final int month, final int year, final String entry, final double percentage) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                userMedicineDao.updateMedicationEntry(date, month, year, entry, percentage);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    //Todo Confused in logic -- followed existing code as it is, check logic later
    /*If No Entry will be found it will enter in the database, so that it can be later updated.
     * Usage is in Day Fragment Activity **/
    @Override
    public void insertOrUpdateMissedMedicationEntry(final int drug, final String ch , final int date, final int month, final int year, final double percentage) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String ts;
                // calculation of timestamp
                if(date>=10)
                    ts=""+year+"-"+month+"-"+date;
                else
                    ts=""+year+"-"+month+"-0"+date;
                int flag = 0;
                List<String> statusList = userMedicineDao.getStatusListByDateMonthYear(date, month, year);
                for(String status: statusList) {
                    flag = 1;
                }
                if (flag == 0) {
                    UserMedicine userMedicine = new UserMedicine(drug, ch, month, year, date,"" , percentage, ts);
                    userMedicineDao.setUserMedicineSelection(userMedicine);

                    List<Integer> dateList = userMedicineDao.getDateListByMonthYear(month, year);
                    int count=1,p,lim,ft=0;
                    for (int i: dateList) {
                        p = i;
                        count++;
                        if(count == 1) {
                            ft = p;
                        }
                        else if (count == 2) {
                            lim = p - ft;
                            for (int j=1; j<lim; j++) {
                                if((date+j)>=10)
                                    ts=""+year+"-"+month+"-"+(date+j);
                                else
                                    ts=""+year+"-"+month+"-0"+(date+j);
                                userMedicineDao.setUserMedicineSelection(new UserMedicine(drug, ch, month, year, date+j, "", percentage, ts));
                            }
                        }
                    }
                }
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /*Is Entered is Used for Getting the Style of Each Calendar Grid Cell According to the Medication Status Taken or Not Taken*/
    @Override
    public void isEntered(final int date, final int month, final int year, final LoadIntegerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String status = userMedicineDao.isEntered(date, month, year);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (status.equalsIgnoreCase("yes"))
                            callback.onDataLoaded(0);
                        else if (status.equalsIgnoreCase("no"))
                            callback.onDataLoaded(1);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);

    }

    /**Getting the oldest registered entry of Pill**/
    @Override
    public void getFirstTimeTimeStamp(final LoadLongCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String timeStamp = userMedicineDao.getFirstTimeTimeStamp();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date comp_date=Calendar.getInstance().getTime();
                try {
                    comp_date   = sdf.parse(timeStamp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final Calendar cal = Calendar.getInstance();
                cal.setTime(comp_date);
                final long firstRunTime=cal.getTimeInMillis();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(firstRunTime);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**Getting the Status of Each Day, like whether the Medicine was taken or not.
     * Usages in Alert Dialog Fragment for getting the status of pill for setting up Reminder
     * Usages in Day Fragment Activity for getting the previous status of day before updating it as not taken. **/
    @Override
    public void getStatus(final int date, final int month, final int year, final LoadStringCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String status = userMedicineDao.getStatus(date, month, year);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (status!=null)
                            callback.onDataLoaded(status);
                        else callback.onDataLoaded("miss");
                    }
                });

            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**From the Last Time Pill was Taken it Calculates the maximum weeks in a row medication was taken
     * Need at Home Screen, First Analytic Scrren, Second Analytic Scrren, Day Fragment Screen
     * Main Activity for updating the dosesInArow as it changes according to the status we enter.**/
    @Override
    public void getDosesInaRowWeekly(final LoadIntegerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<UserMedicine> userMedicines = userMedicineDao.getDosesInaRowWeekly();
                int dosesInaRow=1,aMonth=0,pMonth=0;
                Date ado,pdo;
                int pPara=0;
                long aPara=0;
                int numDays=0;
                String ats="",pts="";
                if(userMedicines!=null) {
                    ats = userMedicines.get(0).getTimeStamp();
                    aMonth = userMedicines.get(0).getMonth() + 1;
                    ats = getHumanDateFormat(ats, aMonth);
                    ado = getDateObject(ats);

                    int size = userMedicines.size() - 1;
                    for(int i = 1; i<= size; i++) {
                        pts = userMedicines.get(i).getTimeStamp();
                        pMonth = userMedicines.get(i).getMonth() + 1;
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
                final int finalDosesInaRow = dosesInaRow;
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(finalDosesInaRow);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /*Deleting the Database*/
    @Override
    public void resetDatabase() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                appSettingDao.deleteTableRows();
                locationDao.deleteTableRows();
                packingDao.deleteTableRows();
                userMedicineDao.deleteTableRows();
            }
        };
        appExecutors.diskIO().execute(deleteRunnable);
    }

    /**Inseting the location for maintaining Location History**/
    @Override
    public void insertLocation(final String location) {
        final int[] a = {0};
        final int[] flag = { 0 };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Location> locations = locationDao.getLocationListByLocation(location);
                for (Location l: locations) {
                    a[0] = l.getTime();
                    a[0]++;
                    flag[0] = 1;
                }
                if(flag[0] == 1) {
                    locationDao.updateLocation(a[0], location);
                }
                else {
                    Location insertLocation = new Location(location, a[0]);
                    locationDao.insertLocation(insertLocation);
                }

            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**Fetching the Location**/
    @Override
    public void getLocation(final LoadListLocationCallback callback) {
        Runnable locationRunnable = new Runnable() {
            @Override
            public void run() {
                final List<Location> locations = locationDao.getLocationList();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(locations);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(locationRunnable);
    }

    /**Inserting the Packing Item in DataBase when using Add Item Edit Text**/
    @Override
    public void insertPackingItem(final String pItem, final int quantity, final String status) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Integer> intList = packingDao.getPackingQuantityList(pItem);
                if(intList.size()>0) {
                    packingDao.updatePacking(pItem, quantity, status);
                }
                else {
                    packingDao.insertPacking(new Packing(pItem, quantity, status));
                }
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**Fetching the Packing Item to be taken**/
    @Override
    public void getPackingItemChecked(final LoadListPackingCallback callback) {
        Runnable packingRunnable = new Runnable() {
            @Override
            public void run() {
                final List<Packing> packings = packingDao.getPackingItemChecked("yes");
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(packings);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(packingRunnable);
    }

    /**Fetching the list of Packing Item from which one can be chosen**/
    @Override
    public void getPackingItem(final LoadListPackingCallback callback) {
        Runnable packingRunnable = new Runnable() {
            @Override
            public void run() {
                final List<Packing> packings = packingDao.getPackingItem();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(packings);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(packingRunnable);
    }

    /**Refreshing the status of each packing item to its original state**/
    @Override
    public void refreshPackingItemStatus() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                packingDao.refreshPackingItemStatus("no");
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**Finding the Last Date the Drug was taken**/
    @Override
    public void getLastTaken(final LoadStringCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<String> medicines = userMedicineDao.getLastTaken("yes");
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(medicines.get(medicines.size()-1));
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**Finding the No. of Drugs**/
    @Override
    public void getMedicineCountTaken(final LoadIntegerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final int count = userMedicineDao.getCountTaken("yes");
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(count);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**Finding the Drugs between two dates for updating Adherence in Day Fragment Activity of any selected date**/
    @Override
    public void getCountTakenBetween(final Date s, final Date e, final LoadIntegerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<String> timeStampList = userMedicineDao.getLastTaken("yes");
                int count = 0;String d;
                for (String time: timeStampList) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date curr = Calendar.getInstance().getTime();
                    try {
                        curr = sdf.parse(time);
                    } catch (ParseException e1) {
                        Log.e("AppDbHelper", "Parse Exception during parsing timestamp from database " + time);
                    }
                    long currt=curr.getTime();
                    long endt= e.getTime();

                    Calendar cal =Calendar.getInstance();
                    cal.setTime(s);
                    cal.add(Calendar.MONTH, 1);
                    Date p =cal.getTime();
                    long strt=p.getTime();

                    Log.d("AppDbHelper","Current Long:"+currt);
                    Log.d("AppDbHelper","End Long:"+endt);
                    Log.d("AppDbHelper","Start Long:"+strt);
                    if(currt>=strt && currt<=endt)
                        count++;
                    else if(strt==endt)
                    {
                        count++;
                    }
                }
                final int finalCount = count;
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(finalCount);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);

    }

    @Override
    public void getAlarmData(final LoadAlarmDataCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final AlarmTime time = alarmDao.getAlarmData();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(time);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void insertAlarmData(final AlarmTime time) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                alarmDao.insertAlarmData(time);
            }
        };
        appExecutors.diskIO().execute(runnable);

    }
}
