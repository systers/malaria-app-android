package com.peacecorps.malaria.ui.info_hub;

import com.peacecorps.malaria.ui.base.MvpPresenter;
import com.peacecorps.malaria.ui.base.MvpView;

/**
 * Created by Anamika Tripathi on 17/7/18.
 */

public interface InfoHubContract {
    interface InfoHubMvpView extends MvpView {

    }

    interface InfoHubMvpPresenter<V extends InfoHubMvpView> extends MvpPresenter<V> {

    }
}
