package com.peacecorps.malaria.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.activities.TripIndicatorFragmentActivity;

/**
 * Created by yatna on 10/4/16.
 */
public class TripAppWidgetProvider extends AppWidgetProvider {

    SharedPreferences preferences;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        preferences = context.getSharedPreferences("WidgetReminder", Context.MODE_PRIVATE);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, TripIndicatorFragmentActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            views.setOnClickPendingIntent(R.id.button_edit, pendingIntent);
            //display details about the upcoming trip
            views.setTextViewText(R.id.textView_info,preferences.getString("view_upcoming_reminder", " No upcoming reminder has been set"));

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}
