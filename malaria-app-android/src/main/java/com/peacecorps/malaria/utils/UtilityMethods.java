package com.peacecorps.malaria.utils;

/**
 * Created by yatna on 16/8/16.
 */
public class UtilityMethods {

    //check if email is valid
    public static boolean validEmail(String mail) {
        boolean  r=android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
        return r;
    }
}
