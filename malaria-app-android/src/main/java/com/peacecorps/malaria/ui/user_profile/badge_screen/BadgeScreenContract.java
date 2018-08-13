package com.peacecorps.malaria.ui.user_profile.badge_screen;

import android.graphics.drawable.Drawable;

import com.peacecorps.malaria.ui.base.MvpPresenter;
import com.peacecorps.malaria.ui.base.MvpView;

/**
 * Created by Anamika Tripathi on 25/7/18.
 */
public interface BadgeScreenContract {
    interface BadgeMvpView extends MvpView{
        void startCategoryMedicineDialog(Drawable badgeDrawable);
        void startCategoryQADialog(Drawable badgeDrawable);
    }
    interface BadgeMvpPresenter<V extends BadgeMvpView> extends MvpPresenter<V> {
        int getUserScore();
        String getUserName();
        void selectUserBadge();
        void selectGameBadge();
    }
}
