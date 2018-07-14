package com.peacecorps.malaria.ui.home_screen;

import android.content.Context;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BasePresenter;

public class HomePresenter<V extends HomeContract.IHomeView> extends BasePresenter<V> implements HomeContract.IHomePresenter {

    public HomePresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }
}