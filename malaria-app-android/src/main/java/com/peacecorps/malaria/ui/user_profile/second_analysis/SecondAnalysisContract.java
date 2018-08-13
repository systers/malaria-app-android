package com.peacecorps.malaria.ui.user_profile.second_analysis;

import com.peacecorps.malaria.ui.base.MvpPresenter;
import com.peacecorps.malaria.ui.base.MvpView;

/**
 * Created by Anamika Tripathi on 7/8/18.
 */
public interface SecondAnalysisContract {
    interface SecondAnalysisMvpView extends MvpView {
        void startRecyclerView();
    }

    interface SecondAnalysisMvpPresenter<V extends SecondAnalysisMvpView> extends MvpPresenter<V> {
        void getDataForProgressBar();

    }
}
