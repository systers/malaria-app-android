package com.peacecorps.malaria.data.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.peacecorps.malaria.data.db.dao.*;
import com.peacecorps.malaria.data.db.entities.*;
import com.peacecorps.malaria.utils.AppExecutors;
import com.peacecorps.malaria.utils.CalendarFunction;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.peacecorps.malaria.utils.CalendarFunction.getDateObject;
import static com.peacecorps.malaria.utils.CalendarFunction.getHumanDateFormat;

public class AppDbHelper implements DbHelper {

    private static volatile AppDbHelper sInstance;

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
    public static AppDbHelper getInstance(@NonNull AppExecutors appExecutors, @NonNull AppSettingDao appSettingDao, @NonNull LocationDao locationDao,
                                          @NonNull PackingDao packingDao, @NonNull UserMedicineDao userMedicineDao, @NonNull AlarmDao alarmDao) {
        if (sInstance == null) {
            synchronized ((AppDbHelper.class)) {
                if (sInstance == null) {
                    sInstance = new AppDbHelper(appExecutors, appSettingDao, locationDao, packingDao, userMedicineDao, alarmDao);
                }
            }
        }
        return sInstance;
    }


    public static void clearInstance() {
        sInstance = null;
    }


    /**
     * Method to Update the Progress Bars
     **/
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

    /**
     * Method to Update the User Selection of Medicine and it's Status of whether Medicine was taken or not.
     * Used in Alert Dialog to Directly update the current Status
     * Used in Home Screen Fragment for updating the current status through tick marks
     **/
    @Override
    public void setUserMedicineSelection(String drug, String choice, Date date, String status, Double percentage) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String ts;
        int month = cal.get(Calendar.MONTH) + 1 ;
        String monthStr;
        if ((cal.get(Calendar.MONTH)) < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = "" + month;
        }
        if ((cal.get(Calendar.DATE)) >= 10) {
            ts = "" + cal.get(Calendar.YEAR) + "/" + monthStr + "/" + cal.get(Calendar.DATE);
        } else {
            ts = "" + cal.get(Calendar.YEAR) + "/" + monthStr + "/0" + cal.get(Calendar.DATE);
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
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        int w = c.get(Calendar.DAY_OF_WEEK);
        final AppSetting appSetting = new AppSetting(drug, choice, w, date, "true");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String value = appSettingDao.checkFirstInstall();
                if (value.compareTo("true") == 0) {
                    appSettingDao.deleteFirstRow();
                    appSettingDao.insertAppSettings(appSetting);
                } else {
                    appSettingDao.insertAppSettings(appSetting);
                }
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * Getting Medication Data of Each Day in Day Fragment Activity
     **/
    @Override
    public void getMedicationData(final int date, final int month, final int year, final LoadStringCallback callback) {
        final StringBuffer buffer = new StringBuffer();
        Runnable medicationRunnable = new Runnable() {
            @Override
            public void run() {
                List<UserMedicine> userMedicines = userMedicineDao.getMedicationData("daily", month, year);
                for (UserMedicine medicine : userMedicines) {
                    int d = medicine.getDate();
                    String ch = medicine.getStatus();
                    if (d == date) {
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

    /**
     * Method to Modify the entry of Each Day
     **/
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
    public void insertOrUpdateMissedMedicationEntry(final String drug, final String ch, final int date, final int month, final int year, final double percentage) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String ts;
                // calculation of timestamp
                if (date >= 10) {
                    ts = "" + year + "-" + month + "-" + date;
                } else {
                    ts = "" + year + "-" + month + "-0" + date;
                }
                int flag = 0;
                List<String> statusList = userMedicineDao.getStatusListByDateMonthYear(date, month, year);
                if (statusList.size() > 0)
                    flag = 1;

                if (flag == 0) {
                    UserMedicine userMedicine = new UserMedicine(drug, ch, month, year, date, "", percentage, ts);
                    userMedicineDao.setUserMedicineSelection(userMedicine);

                    List<Integer> dateList = userMedicineDao.getDateListByMonthYear(month, year);
                    int count = 1, p, lim, ft = 0;
                    for (int i : dateList) {
                        p = i;
                        count++;
                        if (count == 1) {
                            ft = p;
                        } else if (count == 2) {
                            lim = p - ft;
                            for (int j = 1; j < lim; j++) {
                                if ((date + j) >= 10) {
                                    ts = "" + year + "-" + month + "-" + (date + j);
                                } else {
                                    ts = "" + year + "-" + month + "-0" + (date + j);
                                }
                                userMedicineDao.setUserMedicineSelection(new UserMedicine(drug, ch, month, year, date + j, "", percentage, ts));
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
                        if (status.equalsIgnoreCase("yes")) {
                            callback.onDataLoaded(0);
                        } else if (status.equalsIgnoreCase("no")) {
                            callback.onDataLoaded(1);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);

    }

    /**
     * Getting the oldest registered entry of Pill
     **/
    @Override
    public void getFirstTimeByTimeStamp(final LoadLongCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String timeStamp = userMedicineDao.getFirstTimeTimeStamp();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date comp_date = Calendar.getInstance().getTime();
                try {
                    comp_date = sdf.parse(timeStamp);
                } catch (Exception e) {
                    ToastLogSnackBarUtil.showErrorLog("AppDbHelper: Exception in parsing date " + timeStamp);
                }
                final Calendar cal = Calendar.getInstance();
                cal.setTime(comp_date);
                final long firstRunTime = cal.getTimeInMillis();
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

    /**
     * Getting the Status of Each Day, like whether the Medicine was taken or not.
     * Usages in Alert Dialog Fragment for getting the status of pill for setting up Reminder
     * Usages in Day Fragment Activity for getting the previous status of day before updating it as not taken.
     **/
    @Override
    public void getDailyStatus(final int date, final int month, final int year, final LoadStringCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String status = userMedicineDao.getDailyStatus(date, month, year);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (status != null) {
                            callback.onDataLoaded(status);
                        } else {
                            callback.onDataLoaded("miss");
                        }
                    }
                });

            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * From the Last Time Pill was Taken it Calculates the maximum weeks in a row medication was taken
     * Need at Home Screen, First Analytic Scrren, Second Analytic Scrren, Day Fragment Screen
     * Main Activity for updating the dosesInArow as it changes according to the status we enter.
     **/
    @Override
    public void getDosesInaRowWeekly(final LoadIntegerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<UserMedicine> userMedicines = userMedicineDao.getDosesInaRow();
                int dosesInaRow = 1;
                int aMonth;
                int pMonth;
                Date ado, pdo;
                int pPara;
                long aPara;
                int numDays;
                String ats;
                String pts;
                if (userMedicines != null) {
                    ats = userMedicines.get(0).getTimeStamp();
                    aMonth = userMedicines.get(0).getMonth() + 1;
                    ats = getHumanDateFormat(ats, aMonth);
                    ado = getDateObject(ats);

                    int size = userMedicines.size() - 1;
                    for (int i = 1; i <= size; i++) {
                        pts = userMedicines.get(i).getTimeStamp();
                        pMonth = userMedicines.get(i).getMonth() + 1;
                        pts = getHumanDateFormat(pts, pMonth);
                        pdo = getDateObject(pts);
                        numDays = CalendarFunction.getDayofWeek(pdo);
                        pPara = 7 - numDays + 7;
                        aPara = CalendarFunction.getNumberOfDays(pdo, ado);
                        if (aPara <= pPara) {
                            dosesInaRow++;
                        } else {
                            break;
                        }
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

    @Override
    public void getDosesInaRowDaily(final LoadIntegerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<UserMedicine> userMedicines = userMedicineDao.getDosesInaRow();
                int dosesInaRow = 0;
                int prevDate = 0;
                int currDate;
                int currDateMonth;
                int prevDateMonth = 0;
                int currDateYear;

                String ts;
                /**One Iteration is done before entering the while loop for updating the previous and current date**/
                if (userMedicines.size() > 0) {
                    ts = userMedicines.get(0).getTimeStamp();
                    currDate = userMedicines.get(0).getDate();
                    ToastLogSnackBarUtil.showDebugLog("curr date 1->" + ts);

                    if (userMedicines.get(0).getStatus() != null && (userMedicines.get(0).getStatus().compareTo("yes") == 0)) {
                        prevDate = userMedicines.get(0).getDate();
                        prevDateMonth = userMedicines.get(0).getMonth();
                        if (Math.abs(currDate - prevDate) <= 1)
                            dosesInaRow++;
                    }

                    /**Since Previous and Current Date our Updated,
                     * Now backwards scan is done till we receive consecutive previous and current date **/
                    for (UserMedicine medicine : userMedicines) {
                        currDate = medicine.getDate();
                        currDateMonth = medicine.getMonth();
                        currDateYear = medicine.getYear();
                        ts = medicine.getTimeStamp();
                        ToastLogSnackBarUtil.showDebugLog("curr date -> " + ts);

                        int parameter = Math.abs(currDate - prevDate);
                        if (medicine.getStatus() != null) {
                            if (currDateMonth == prevDateMonth) {
                                if (medicine.getStatus().compareTo("yes") == 0 && parameter == 1) {
                                    dosesInaRow++;
                                } else
                                    break;
                            } else {
                                parameter = Math.abs(currDate - prevDate) %
                                        (CalendarFunction.getNumberOfDaysInMonth(currDateMonth, currDateYear) - 1);
                                if (medicine.getStatus().compareTo("yes") == 0 && parameter <= 1) {
                                    dosesInaRow++;
                                } else
                                    break;
                            }
                        }
                        prevDate = currDate;
                        prevDateMonth = currDateMonth;
                        ToastLogSnackBarUtil.showDebugLog("Doses in Row-> " + dosesInaRow);
                    }
                }
                ToastLogSnackBarUtil.showDebugLog("Final doses in row-> " + dosesInaRow);
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
                packingDao.deleteFullPackingList();
                userMedicineDao.deleteTableRows();
            }
        };
        appExecutors.diskIO().execute(deleteRunnable);
    }

    /**
     * Inserting the location for maintaining Location History
     **/
    @Override
    public void insertLocation(final String location) {
        final int[] a = {0};
        final int[] flag = {0};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Location> locations = locationDao.getLocationListByLocation(location);
                for (Location l : locations) {
                    a[0] = l.getTime();
                    a[0]++;
                    flag[0] = 1;
                }
                if (flag[0] == 1) {
                    locationDao.updateLocation(a[0], location);
                } else {
                    Location insertLocation = new Location(location, a[0]);
                    locationDao.insertLocation(insertLocation);
                }

            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * Fetching the Location
     **/
    @Override
    public void getLocation(final loadListStringCallBack callback) {
        Runnable locationRunnable = new Runnable() {
            @Override
            public void run() {
                final List<String> locations = locationDao.getLocationList();
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

    /**
     * Inserting the Packing Item in DataBase when using Add Item Edit Text
     **/
    @Override
    public void insertPackingItem(final String pItem, final int quantity, final boolean status) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Integer> intList = packingDao.getPackingQuantityList(pItem);
                if (intList.size() > 0) {
                    packingDao.updatePacking(pItem, quantity, status);
                } else {
                    packingDao.insertPacking(new Packing(pItem, quantity, status));
                }
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * Fetching the Packing Item to be taken
     **/
    @Override
    public void getPackingItemChecked(final LoadListPackingCallback callback) {
        Runnable packingRunnable = new Runnable() {
            @Override
            public void run() {
                final List<Packing> packings = packingDao.getPackingItemChecked(true);
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

    /**
     * Fetching the list of Packing Item from which one can be chosen
     **/
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

    /**
     * Refreshing the status of each packing item to its original state
     **/
    @Override
    public void refreshPackingItemStatus() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                packingDao.refreshPackingItemStatus(true);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * @param id : deletes packing row where id is the row number
     */
    @Override
    public void deletePackingById(final int id) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                packingDao.deletePackingById(id);
                ToastLogSnackBarUtil.showDebugLog("deleting sqlite row " + id);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * @param status   : defines the packing status, defined by checkbox status
     * @param position : defines the row position in table
     */
    @Override
    public void updatePackingStatus(final boolean status, final int position) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ToastLogSnackBarUtil.showDebugLog("update pos status " + position);
                packingDao.updatePackingStatus(status, position);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * @param callback : passes integer/size of the packing list using onDataLoaded(size)
     */
    @Override
    public void getPackingListSize(final LoadIntegerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final int size = packingDao.getPackingListSize();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(size);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateMedicinePacking(final String name, final int quantity) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                packingDao.updateMedicinePacking(name, quantity, false);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getPackedMedDetails(final LoadPackingCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Packing packing = packingDao.getPackedMedicine();
                if (packing != null) {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataLoaded(packing);
                        }
                    });
                } else {
                    ToastLogSnackBarUtil.showErrorLog("AppDbHelper/getPackedMedDetails: No packing exist");
                }

            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * Finding the Last Date the Drug was taken
     **/
    @Override
    public void getLastTaken(final LoadStringCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<String> medicines = userMedicineDao.getLastTaken("yes");
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (medicines.size() > 0)
                            callback.onDataLoaded(medicines.get(medicines.size() - 1));
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    /**
     * Finding the No. of Drugs
     **/
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

    /**
     * Finding the Drugs between two dates for updating Adherence in Day Fragment Activity of any selected date
     **/
    @Override
    public void getCountTakenBetween(final Date s, final Date e, final LoadIntegerCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<String> timeStampList = userMedicineDao.getLastTaken("yes");
                int count = 0;
                for (String time : timeStampList) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date curr = Calendar.getInstance().getTime();
                    try {
                        curr = sdf.parse(time);
                    } catch (ParseException e1) {
                        ToastLogSnackBarUtil.showErrorLog("AppDbHelper/getCountTakenBetween: Parse Exception " + time);
                    }
                    long currt = curr.getTime();
                    long endt = e.getTime();

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(s);
                    cal.add(Calendar.MONTH, 1);
                    Date p = cal.getTime();
                    long strt = p.getTime();

                    Log.d("AppDbHelper", "Current Long:" + currt);
                    Log.d("AppDbHelper", "End Long:" + endt);
                    Log.d("AppDbHelper", "Start Long:" + strt);
                    if (currt >= strt && currt <= endt) {
                        count++;
                    } else if (strt == endt) {
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

    @Override
    public void updateAlarmTime(final int hour, final int min) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                alarmDao.updateTime(hour, min);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
