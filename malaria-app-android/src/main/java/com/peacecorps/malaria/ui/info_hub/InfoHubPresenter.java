package com.peacecorps.malaria.ui.info_hub;

import android.content.Context;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.info_hub.InfoHubContract.InfoHubMvpPresenter;
import com.peacecorps.malaria.ui.info_hub.InfoHubContract.InfoHubMvpView;

/**
 * Created by Anamika Tripathi on 17/7/18.
 */
public class InfoHubPresenter<V extends InfoHubMvpView> extends BasePresenter<V> implements InfoHubMvpPresenter<V> {

    InfoHubPresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }
}
