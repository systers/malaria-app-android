package com.peacecorps.malaria.ui.base;

import android.content.Context;

import com.peacecorps.malaria.data.AppDataManager;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */


public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    private Context context;
    private V mvpView;
    private AppDataManager manager;

    public BasePresenter(AppDataManager manager, Context context) {
        this.context = context;
        this.manager = manager;
    }


    @Override
    public void attachView(V view) {
        mvpView = view;
    }

    @Override
    public void detachView() {
        mvpView = null;
    }

    /*
     * method to check if the view is attached or not
     *
     * @return true if attached
     */
    boolean isViewAttached() {
        return mvpView != null;
    }

    public V getView() {
        return mvpView;
    }

    public AppDataManager getDataManager() {
        return manager;
    }

    public Context getContext() {
        return context;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.onAttach(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }

}
