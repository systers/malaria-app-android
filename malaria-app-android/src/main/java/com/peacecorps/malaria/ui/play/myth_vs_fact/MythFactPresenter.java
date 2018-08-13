package com.peacecorps.malaria.ui.play.myth_vs_fact;

import android.content.Context;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.play.myth_vs_fact.MythFactContract.MythFactMvpPresenter;
import com.peacecorps.malaria.ui.play.myth_vs_fact.MythFactContract.MythFactMvpView;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public class MythFactPresenter<V extends MythFactMvpView> extends BasePresenter<V> implements MythFactMvpPresenter<V> {
    MythFactPresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }

    // updates preferences with current game score points
    @Override
    public void updateGameScore(int currPoints) {
        int oldPoints = getDataManager().getGameScore();
        getDataManager().setGameScore(oldPoints + currPoints);
    }

    @Override
    public void checkFirstTime() {
        if(!getDataManager().checkMythFactTarget()) {
            getView().playTapTargetViewer();
        }
    }
}
