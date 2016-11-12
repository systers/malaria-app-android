package com.peacecorps.malaria.model;

import junit.framework.TestCase;

/**
 * Created by yatna on 17/8/16.
 */
public class AppUserModelTest extends TestCase {
    AppUserModel appUserModel;
    public void setUp() throws Exception {
        super.setUp();
        appUserModel= new AppUserModel();

    }

    public void testGetName() throws Exception {
        assertEquals("Name getter Invalid","person1",appUserModel.getAppUser("person1","person@gmail.com",21,"Malarone").getName());
    }

    public void testGetEmail() throws Exception {
        assertEquals("Email getter Invalid","person@gmail.com",appUserModel.getAppUser("person1","person@gmail.com",21,"Malarone").getEmail());
    }

    public void testGetAge() throws Exception {
        assertEquals("Age getter Invalid",21,appUserModel.getAppUser("person1","person@gmail.com",21,"Malarone").getAge());
    }

    public void testGetMedicineType() throws Exception {
        assertEquals("Medicine getter Invalid","Malarone",appUserModel.getAppUser("person1","person@gmail.com",21,"Malarone").getMedicineType());
    }
}