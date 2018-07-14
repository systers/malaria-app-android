package com.peacecorps.malaria.ui.base;

import android.arch.lifecycle.Lifecycle;

public interface MvpPresenter <V extends MvpView> {

    /**
     * called when view is attached to presenter
     * @param view
     */
    void attachView(V view);

    /**
     * called when view is detached from presenter
     */
    void detachView();
}