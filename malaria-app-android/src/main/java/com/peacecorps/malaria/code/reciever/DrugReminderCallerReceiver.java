package com.peacecorps.malaria.code.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.peacecorps.malaria.notifications.DrugNotificationUtils;

public class DrugReminderCallerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DrugNotificationUtils.startNotificationCheckSnooze(context);
    }
}
