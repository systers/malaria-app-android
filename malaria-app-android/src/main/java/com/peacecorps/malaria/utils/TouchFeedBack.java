package com.peacecorps.malaria.utils;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Murad (free4murad) on 2/7/17.
 */

public class TouchFeedBack {
    public static void touchFeedBack(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            v.getBackground().setAlpha(128);
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            v.getBackground().setAlpha(255);
        }
    }
}



