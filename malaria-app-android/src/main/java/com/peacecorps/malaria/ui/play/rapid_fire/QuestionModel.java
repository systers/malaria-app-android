package com.peacecorps.malaria.ui.play.rapid_fire;

/**
 * Created by yatna on 10/6/16.
 */
// Used by RapidFirePresenter to prepare list of question, it's options and answers
public class QuestionModel {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private int ans;
    //constructs a question model for Rapid Fire game
    public QuestionModel(String question, String option1, String option2, String option3, int ans){
        this.question=question;
        this.option1=option1;
        this.option2=option2;
        this.option3=option3;
        this.ans=ans;
    }
    //getters
    public String getQuestion(){
        return question;
    }
    public String getOption1(){
        return option1;
    }
    public String getOption2(){
        return option2;
    }
    public String getOption3(){
        return option3;
    }
    public int getAns(){
        return ans;
    }
}
