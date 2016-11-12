package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.model.RapidFireQuestionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yatna on 9/6/16.
 */
public class RapidFireGame extends Activity{
    private Button opt1;
    private Button opt2;
    private Button opt3;
    private TextView questionTv;
    private  TextView scoreTv;
    private TextView timer;
    private Toast result;
    private List<RapidFireQuestionModel> questionList;
    private RapidFireTimeCounter counter;
    private String resultString;
    private int quesNo;
    private  int gameScore;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rapid_fire);
        opt1 = (Button) findViewById(R.id.button1);
        opt2 = (Button) findViewById(R.id.button2);
        opt3 = (Button) findViewById(R.id.button3);
        questionTv= (TextView)findViewById(R.id.txtQuestion);
        scoreTv=(TextView)findViewById(R.id.score);
        timer=(TextView)findViewById(R.id.timers);
        questionList=new ArrayList<RapidFireQuestionModel>();
        opt1.setOnClickListener(optionOneClick());
        opt2.setOnClickListener(optionTwoClick());
        opt3.setOnClickListener(optionThreeClick());
        //adding questions
        questionList.add(new RapidFireQuestionModel("Melfoquine should be taken ", "Daily", "Weekly", "Monthly", 2));
        questionList.add(new RapidFireQuestionModel("Malaria is caused by ", "Virus", "Bacteria", "Protozoa",3));
        questionList.add(new RapidFireQuestionModel("Doxycycline should be taken ", "Daily", "Weekly", "Monthly", 1));
        questionList.add(new RapidFireQuestionModel("Malaria is transmitted through _____ mosquito", "Female Aedes", "Female Anopheles", "Male Aedes", 2));
        questionList.add(new RapidFireQuestionModel("Malarone should be taken ", "Daily", "Weekly", "Monthly", 1));
        initializeGame();

    }
    //disable the back button
    @Override
    public void onBackPressed(){
    }

    void initializeGame(){
        quesNo=0;
        gameScore=0;
        scoreTv.setText("Score : " + gameScore);
        askQuestion(quesNo);
    }
    void askQuestion(int i){
        opt1.setBackground(getResources().getDrawable(R.drawable.info_hub_button));
        opt2.setBackground(getResources().getDrawable(R.drawable.info_hub_button));
        opt3.setBackground(getResources().getDrawable(R.drawable.info_hub_button));
        questionTv.setText(questionList.get(i).getQuestion());
        opt1.setText(questionList.get(i).getOption1());
        opt2.setText(questionList.get(i).getOption2());
        opt3.setText(questionList.get(i).getOption3());
        //display time as 5 secs
        timer.setText("5");
        counter= new RapidFireTimeCounter(6000,1000);
        counter.start();
    }
    public View.OnClickListener  optionOneClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter.cancel();
                if(questionList.get(quesNo).getAns()==1){
                    gameScore++;
                    resultString="Correct !";
                    opt1.setBackgroundColor(getResources().getColor(R.color.light_green));
                }
                else{
                    resultString="Wrong ";
                    opt1.setBackground(getResources().getDrawable(R.drawable.info_hub_button_grayed));
                }

                //Toast.makeText(RapidFireGame.this,resultString,Toast.LENGTH_SHORT).show();
                prepNextQues();

            }
        };
    }
    public View.OnClickListener optionTwoClick() {
       return new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               counter.cancel();
               if(questionList.get(quesNo).getAns()==2){
                   gameScore++;
                   resultString="Correct !";
                   opt2.setBackgroundColor(getResources().getColor(R.color.light_green));
               }
               else{
                   resultString="Wrong ";
                   opt2.setBackground(getResources().getDrawable(R.drawable.info_hub_button_grayed));
               }
               //Toast.makeText(RapidFireGame.this,resultString,Toast.LENGTH_SHORT).show();
               prepNextQues();
           }
       };
    }
    public View.OnClickListener optionThreeClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter.cancel();
                if(questionList.get(quesNo).getAns()==3){
                    gameScore++;
                    resultString="Correct !";
                    opt3.setBackgroundColor(getResources().getColor(R.color.light_green));
                }
                else{
                    resultString="Wrong ";
                    opt3.setBackground(getResources().getDrawable(R.drawable.info_hub_button_grayed));
                }
                //Toast.makeText(RapidFireGame.this,resultString,Toast.LENGTH_SHORT).show();
                prepNextQues();

            }
        };
    }
    void prepNextQues(){
        scoreTv.setText("Score : " + gameScore);
        final android.os.Handler handler =new android.os.Handler();
        //provide a delay of 1 sec after an option is clicked by the user
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                quesNo++;
                if (quesNo<questionList.size()){
                    askQuestion(quesNo);
                }
                else{
                    showDialog();
                }
            }
        },1000);
    }
    //Implementing the exit game button
    public void exitGame(View view) {
        counter.cancel();
        final Dialog alertDialog = new Dialog(RapidFireGame.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        alertDialog.setContentView(R.layout.exit_game_dialog);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        alertDialog.setTitle("Game Over!!");
        Button ok=(Button)alertDialog.findViewById(R.id.exitGameDialogButtonOK);
        Button cancel=(Button)alertDialog.findViewById(R.id.exitGameDialogButtonCancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGameScoreToMainScore();
                RapidFireGame.this.finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    //add current game's score to the user score
    void addGameScoreToMainScore(){
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        int score=sharedPreferences.getInt("gameScore",0);
        score=score+gameScore;
        editor.putInt("gameScore", score);
        editor.commit();

    }
    //Implementing the timer
    public class RapidFireTimeCounter extends CountDownTimer {
        public RapidFireTimeCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            timer.setText(""+l/1000);
        }

        @Override
        public void onFinish() {
            counter.cancel();
            prepNextQues();
        }


    }

    public void showDialog(){
        final Dialog alertDialog = new Dialog(RapidFireGame.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        alertDialog.setContentView(R.layout.game_over_dialog);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        alertDialog.setTitle("Game Over!!");
        Button ok=(Button)alertDialog.findViewById(R.id.gameOverDialogButtonOK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGameScoreToMainScore();
                RapidFireGame.this.finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
