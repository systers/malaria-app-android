package com.peacecorps.malaria.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.peacecorps.malaria.reciever.AlarmHandlerClass;

public class AlarmService extends Service {

    /**Setting up Alarm Calls his Alarm Service**/

    AlarmHandlerClass alarmHandlerClass = new AlarmHandlerClass();

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public void onStart(Intent intent, int startId) {

        alarmHandlerClass.setAlarm(AlarmService.this);
    }

    @Override
    public void onCreate() {

        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**Alarm Service Request Made and Alarm Handler will Handle It. **/
        alarmHandlerClass.setAlarm(AlarmService.this);
        return START_STICKY;
    }

}
