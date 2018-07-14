package com.peacecorps.malaria.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.peacecorps.malaria.data.db.dao.*;
import com.peacecorps.malaria.data.db.entities.*;

@Database(entities = {AppSetting.class, Location.class, Packing.class, UserMedicine.class, AlarmTime.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase INSTANCE;
    private static final Object sLock = new Object();

    public abstract AppSettingDao appSettingDao();
    public abstract LocationDao locationDao();
    public abstract PackingDao packingDao();
    public abstract UserMedicineDao userMedicineDao();
    public abstract AlarmDao alarmDao();

    public static AppDatabase getAppDatabase(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "malaria-database")
                                .fallbackToDestructiveMigration()
                                .build();
            }
            return INSTANCE;
        }

    }



    public static void destroyInstance() {
        INSTANCE = null;
    }
}
