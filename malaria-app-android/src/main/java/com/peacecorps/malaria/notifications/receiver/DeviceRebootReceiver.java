package com.peacecorps.malaria.notifications.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.peacecorps.malaria.notifications.service.AlarmService;

/**
 * Created by Anamika Tripathi on 9/8/18.
 * Broadcast receiver, starts when the device gets starts.
 * Start your repeating alarm here.
 */
public class DeviceRebootReceiver extends BroadcastReceiver {

    private static final String ALARM_RESTART_ON_DEVICE_REBOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals(ALARM_RESTART_ON_DEVICE_REBOOT)) {
                context.startService(new Intent(context, AlarmService.class));
            }
        }
    }
}
