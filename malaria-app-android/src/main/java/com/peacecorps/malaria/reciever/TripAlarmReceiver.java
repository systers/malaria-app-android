package com.peacecorps.malaria.reciever;

/**
 * Created by Ankita on 8/8/2015.
 */
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.peacecorps.malaria.services.TripAlarmService;
import com.peacecorps.malaria.activities.TripIndicatorFragmentActivity;

/**
 * On receiving the calarm call from Alarm Service,
 * it sets the parameter for ring alarm
 */

public class TripAlarmReceiver extends WakefulBroadcastReceiver {

    public static Ringtone ringtone;
    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        TripIndicatorFragmentActivity inst = TripIndicatorFragmentActivity.instance();

        PowerManager powerManager = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        SharedPreferences prefs = context.getSharedPreferences("ringtone", 0);
        String restoredText = prefs.getString("toneUri", null);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        // if shared preference is not empty then the user selected tone will be played
        if(restoredText!=null)
        {
            alarmUri= Uri.parse(restoredText);
        }

        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();


        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                TripAlarmService.class.getName());


        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        wakeLock.release();

        Log.d("TripAlarmReceiver","Set the service");

    }
}