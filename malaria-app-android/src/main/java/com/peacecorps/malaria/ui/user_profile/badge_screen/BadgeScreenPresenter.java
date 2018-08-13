package com.peacecorps.malaria.ui.user_profile.badge_screen;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.user_profile.badge_screen.BadgeScreenContract.BadgeMvpPresenter;
import com.peacecorps.malaria.ui.user_profile.badge_screen.BadgeScreenContract.BadgeMvpView;

/**
 * Created by Anamika Tripathi on 24/7/18.
 */
public class BadgeScreenPresenter<V extends BadgeMvpView> extends BasePresenter<V> implements BadgeMvpPresenter<V> {
    BadgeScreenPresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }

    @Override
    public void attachView(V view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public int getUserScore() {
        return getDataManager().getUserScore();
    }

    @Override
    public String getUserName() {
        return getDataManager().getUserName();
    }

    //get badge based on medication  score
    @Override
    public void selectUserBadge() {
        Drawable badgeDrawable;
        int userScore = getDataManager().getUserScore();
        //set badge according to score
        if(userScore<2){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_b1);
        } else if(userScore<4){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_b3);
        }else if(userScore<6){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_b5);
        }else if(userScore<7){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_b6);
        }else if(userScore<8){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_newbie);
        }else {
            badgeDrawable = getContext().getResources().getDrawable(R.drawable.y_hat);
        }
        // start the category medication dialog by setting drawable
        getView().startCategoryMedicineDialog(badgeDrawable);
    }

    //get badge based on game score
    @Override
    public void selectGameBadge() {
        int gameScore = getDataManager().getGameScore();
        Drawable badgeDrawable;
        //set badge according to score
        if(gameScore<2){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_b1);
        } else if(gameScore<4){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_b3);
        }else if(gameScore<6){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_b5);
        }else if(gameScore<8){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_b6);
        }else if(gameScore<10){
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_newbie);
        }else{
            badgeDrawable=getContext().getResources().getDrawable(R.drawable.y_hat);
        }
        getView().startCategoryQADialog(badgeDrawable);
    }
}
