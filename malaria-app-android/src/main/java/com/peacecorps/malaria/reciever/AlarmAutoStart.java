package com.peacecorps.malaria.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.peacecorps.malaria.services.AlarmService;
import com.peacecorps.malaria.R;

public class AlarmAutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(R.string.alarm_auto_start_boot_completed_intent_check)) {
            context.startService(new Intent(context, AlarmService.class));
        }
    }

}
