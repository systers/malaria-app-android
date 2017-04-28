package com.peacecorps.malaria.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.peacecorps.malaria.notification.DrugReminderNotification;

public class DrugReminderCallerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new DrugReminderNotification(context);
    }
}
