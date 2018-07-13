package com.peacecorps.malaria.ui.play.rapid_fire;

import android.content.Context;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.play.rapid_fire.RapidFireContract.RapidFireMvpPresenter;
import com.peacecorps.malaria.ui.play.rapid_fire.RapidFireContract.RapidFireMvpView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anamika Tripathi on 19/7/18.
 */
public class RapidFirePresenter<V extends RapidFireMvpView> extends BasePresenter<V> implements RapidFireMvpPresenter<V> {
    // array list for questions & respective answer
    private List<QuestionModel> questionList;

    RapidFirePresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }

    // preparing question, answer in beginning of test.
    @Override
    public void prepareQuestionList(int num) {
        questionList=new ArrayList<QuestionModel>();
        //adding questions
        questionList.add(new QuestionModel("Melfoquine should be taken ", "Daily", "Weekly", "Monthly", 2));
        questionList.add(new QuestionModel("Malaria is caused by ", "Virus", "Bacteria", "Protozoa",3));
        questionList.add(new QuestionModel("Doxycycline should be taken ", "Daily", "Weekly", "Monthly", 1));
        questionList.add(new QuestionModel("Malaria is transmitted through _____ mosquito", "Female Aedes", "Female Anopheles", "Male Aedes", 2));
        questionList.add(new QuestionModel("Malarone should be taken ", "Daily", "Weekly", "Monthly", 1));
        getView().prepareQuestionAndOptions();
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
    public int questionListSize() {
        return questionList.size();
    }

    // returns ith row i.e model of question list
    @Override
    public QuestionModel getQuestionModel(int i) {
        return questionList.get(i);
    }

    // updates preferences with current game score points
    @Override
    public void updateGameScore(int currPoints) {
        int oldPoints = getDataManager().getGameScore();
        getDataManager().setGameScore(oldPoints + currPoints);
    }
}
