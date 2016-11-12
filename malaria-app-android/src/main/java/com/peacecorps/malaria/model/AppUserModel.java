package com.peacecorps.malaria.model;

/**
 * Created by yatna on 10/7/16.
 */
public class AppUserModel {
    private String email;
    private String name;
    private int age;
    private String medicineType;
    //construct a user's model
    public AppUserModel getAppUser(String name, String email, int age, String medicineType){
        this.name=name;
        this.email=email;
        this.age=age;
        this.medicineType=medicineType;
        return this;
    }
    //getters
    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    public int getAge(){
        return this.age;
    }
    public String getMedicineType(){
        return this.medicineType;
    }
}
