package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
    private static final String COUNTER_MILLIS_LEFT = "COUNTER_MILLIS_LEFT" ;
    private static final String GAME_SCORE = "GAME_SCORE";
    private static final String QUES_NO ="QUES_NO" ;
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
    private long millisLeft;
    private  int gameScore;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private long timercount;

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
        initializeGame(savedInstanceState);

    }
    //disable the back button
    @Override
    public void onBackPressed(){
    }

    void initializeGame(Bundle savedInstanceState){
        // below parameters are initialized with their initial value when game starts
        quesNo=0;
        gameScore=0;
        millisLeft=6000;
        // in case the game is coming from saved instance, below parameters are changed to saved values
        if(savedInstanceState!=null) {
            millisLeft=savedInstanceState.getLong(COUNTER_MILLIS_LEFT);
            gameScore=savedInstanceState.getInt(GAME_SCORE);
            quesNo=savedInstanceState.getInt(QUES_NO);
        }
        scoreTv.setText("Score : " + gameScore);
        askQuestion(quesNo,millisLeft);
    }
    void askQuestion(int i,long millisLeft){
        opt1.setBackground(getResources().getDrawable(R.drawable.info_hub_button));
        opt2.setBackground(getResources().getDrawable(R.drawable.info_hub_button));
        opt3.setBackground(getResources().getDrawable(R.drawable.info_hub_button));
        //if quesNo exceeds the questionList size which happens on orientation change, quesNo have to be decremented
        if(i== questionList.size())
            i--;
        questionTv.setText(questionList.get(i).getQuestion());
        opt1.setText(questionList.get(i).getOption1());
        opt2.setText(questionList.get(i).getOption2());
        opt3.setText(questionList.get(i).getOption3());

        opt1.setEnabled(true);
        opt1.setClickable(true);
        opt2.setEnabled(true);
        opt2.setClickable(true);
        opt3.setEnabled(true);
        opt3.setClickable(true);
        //displays the time left for the next question
        timer.setText(""+ millisLeft/1000);
        //checks that quesNo is within questionList size which is possible on orientation change
        if(i != questionList.size()) {
            counter= new RapidFireTimeCounter(millisLeft,1000);
            counter.start();
        }

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

                opt1.setEnabled(false);
                opt1.setClickable(false);
                opt2.setEnabled(false);
                opt2.setClickable(false);
                opt3.setEnabled(false);
                opt3.setClickable(false);

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

               opt1.setEnabled(false);
               opt1.setClickable(false);
               opt2.setEnabled(false);
               opt2.setClickable(false);
               opt3.setEnabled(false);
               opt3.setClickable(false);
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

                opt1.setEnabled(false);
                opt1.setClickable(false);
                opt2.setEnabled(false);
                opt2.setClickable(false);
                opt3.setEnabled(false);
                opt3.setClickable(false);
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
                if(quesNo<questionList.size())
                {
                    quesNo++;

                if (quesNo<questionList.size()){
                    askQuestion(quesNo,6000);
                }
                else{
                    showDialog();
                }
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
                counter = new RapidFireTimeCounter(timercount,1000);
                counter.start();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        doKeepDialog(alertDialog);
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
            timercount=l;
            timer.setText(""+ l/1000);
            millisLeft=l;

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
        doKeepDialog(alertDialog);
    }

    @Override
    protected void onDestroy() {
        counter.cancel();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(COUNTER_MILLIS_LEFT,millisLeft);
        outState.putInt(GAME_SCORE,gameScore);
        outState.putInt(QUES_NO,quesNo);
        super.onSaveInstanceState(outState);
    }
    // attaches the dialog with WindowManager to avoid cancelling on orientation change
    private static void doKeepDialog(Dialog dialog){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }
}
