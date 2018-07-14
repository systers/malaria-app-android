package com.peacecorps.malaria.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends  AppCompatActivity implements MvpView {
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void init();

    @Override
    public boolean isNetworkConnected() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Todo detach views
    }
}
