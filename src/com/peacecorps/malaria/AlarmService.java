package com.peacecorps.malaria;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService extends Service {

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

		alarmHandlerClass.setAlarm(AlarmService.this);
		return START_STICKY;
	}

}