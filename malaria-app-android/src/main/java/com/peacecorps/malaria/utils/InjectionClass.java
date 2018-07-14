package com.peacecorps.malaria.utils;

import android.content.Context;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.AppDatabase;
import com.peacecorps.malaria.data.db.AppDbHelper;
import com.peacecorps.malaria.data.prefs.AppPreferencesHelper;


public class InjectionClass {

    private static AppDbHelper provideSqliteHelper(Context context) {
        AppDatabase database = AppDatabase.getAppDatabase(context);
        return AppDbHelper.getInstance(new AppExecutors(), database.appSettingDao(), database.locationDao(), database.packingDao(),
                database.userMedicineDao(), database.alarmDao());
    }

    public static AppDataManager provideDataManager(Context context) {
        return new AppDataManager(provideSqliteHelper(context),
                new AppPreferencesHelper(context, "malairia-pref-file"));
    }


}
