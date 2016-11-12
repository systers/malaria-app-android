package com.peacecorps.malaria.utils;

import junit.framework.TestCase;

/**
 * Created by yatna on 17/8/16.
 */
public class UtilityMethodsTest extends TestCase {

    public void testValidEmail() throws Exception {
        assertTrue("Email Validation Failed",UtilityMethods.validEmail("yatna@gmail.com"));
        assertFalse("Email Validation Failed", UtilityMethods.validEmail("yatna@gmail"));
        assertFalse("Email Validation Failed", UtilityMethods.validEmail("yatnagmail.com"));
    }
}