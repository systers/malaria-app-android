package com.peacecorps.malaria.ui.play.myth_vs_fact;

import com.peacecorps.malaria.ui.base.MvpPresenter;
import com.peacecorps.malaria.ui.base.MvpView;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public interface MythFactContract {
    interface MythFactMvpView extends MvpView {

    }

    interface MythFactMvpPresenter <V extends MythFactMvpView> extends MvpPresenter<V> {
        void updateGameScore(int currPoints);
    }
}
