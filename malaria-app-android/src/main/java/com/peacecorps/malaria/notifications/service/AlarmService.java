package com.peacecorps.malaria.notifications.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.peacecorps.malaria.notifications.receiver.AlarmHandlerClass;

/**
 * setting up alarm using the service
 */
public class AlarmService extends Service {

    private AlarmHandlerClass alarmHandlerClass = new AlarmHandlerClass();

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

    /**
     * alarm service request made
     * alarm handler will start alarm
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmHandlerClass.setAlarm(AlarmService.this);
        return START_STICKY;
    }

}
