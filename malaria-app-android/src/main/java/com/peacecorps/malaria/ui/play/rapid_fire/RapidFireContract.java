package com.peacecorps.malaria.ui.play.rapid_fire;

import com.peacecorps.malaria.ui.base.MvpPresenter;
import com.peacecorps.malaria.ui.base.MvpView;

/**
 * Created by Anamika Tripathi on 19/7/18.
 */
public interface RapidFireContract {
    interface RapidFireMvpView extends MvpView {
        void setButtonBackground();
        void prepareQuestionAndOptions();
        void enableOptions(boolean val);
        void updateTimer(long miliLeft);
    }

    interface RapidFireMvpPresenter<V extends RapidFireMvpView> extends MvpPresenter<V> {
        void prepareQuestionList(int num);
        int questionListSize();
        QuestionModel getQuestionModel(int i);
        void updateGameScore(int currPoints);
    }
}
