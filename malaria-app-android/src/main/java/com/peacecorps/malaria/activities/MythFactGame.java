package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peacecorps.malaria.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yatna on 4/6/16.
 */
public class MythFactGame extends Activity {
    private TextView tvUserCoins,tvQuestion,trash,chest;
    private int userCoins=0, i;
    private List<String> questions=null;
    private List<String> answers=null;
    private String emptyString="";
    private Button nxtButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myth_fact_game);
        tvUserCoins=(TextView)findViewById(R.id.userCoins);
        tvQuestion=(TextView)findViewById(R.id.question);
        trash=(TextView)findViewById(R.id.trash);
        chest=(TextView)findViewById(R.id.chest);
        nxtButton=(Button)findViewById(R.id.nextBtn);
        //set touch listeners
        tvQuestion.setOnTouchListener(new ChoiceTouchListener());
        //set drag listeners
        trash.setOnDragListener(new ChoiceDragListener());
        chest.setOnDragListener(new ChoiceDragListener());
        initializeGame();
    }

    //disable the back button
    @Override
    public void onBackPressed(){
    }

    private void initializeGame() {
        //setup questions
        questions= new ArrayList<String>(Arrays.asList("Once you get malaria you will be immune from it for the rest of your life",
                "Malaria is a life-threatening disease", "You don’t have to worry about malaria in the dry season",
                "Nearly half of the world's population is at risk of malaria", "There is a vaccine for malaria"));
        //setup answers
        answers = new ArrayList<String>(Arrays.asList("myth","fact","myth","fact","myth"));
        //initialize game score
        tvUserCoins.setText("" + userCoins);
        //initial question
        tvQuestion.setText(questions.get(i));
        //disable the next Button
        nxtButton.setClickable(false);
        nxtButton.setBackground(getResources().getDrawable(R.drawable.info_hub_button_grayed));

    }
    //implementing the 'Next' button
    public void nextClick(View view) {
        i=i+1;
        if(i<questions.size()){
            tvQuestion.setText(questions.get(i));
            trash.setText(emptyString);
            chest.setText(emptyString);
            tvQuestion.setVisibility(View.VISIBLE);
            nxtButton.setClickable(false);
            nxtButton.setBackground(getResources().getDrawable(R.drawable.info_hub_button_grayed));
            chest.setBackground(getResources().getDrawable(R.drawable.chest));
            trash.setBackground(getResources().getDrawable(R.drawable.trash));
        }
        else{
            final Dialog alertDialog = new Dialog(MythFactGame.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
            alertDialog.setContentView(R.layout.game_over_dialog);
            alertDialog.setCancelable(false);

            // Setting Dialog Title
            alertDialog.setTitle("Game Over!!");
            Button ok=(Button)alertDialog.findViewById(R.id.gameOverDialogButtonOK);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MythFactGame.this.finish();
                }
            });

            addGameScoreToMainScore();
            // Showing Alert Message
            alertDialog.show();
        }
    }
    //Implementing the exit game button
    public void exitGame(View view) {
        final Dialog alertDialog = new Dialog(MythFactGame.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
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
                MythFactGame.this.finish();
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
    //add current game's score to the main score
    void addGameScoreToMainScore(){
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sharedPreferences.edit();
        int score=sharedPreferences.getInt("gameScore",0);
        score=score+userCoins;
        editor.putInt("gameScore",score);
        editor.commit();

    }

    private class ChoiceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //setup drag
                ClipData data = ClipData.newPlainText("", "");
                //create shadow while being dragged
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                //start dragging the item touched
                v.startDrag(data, shadowBuilder, v, 0);
                return true;
            }
            else {
                return false;
            }
        }
    }

    private class ChoiceDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:
                    //handle the dragged view being dropped over a drop view
                    //handle the dragged view being dropped over a target view
                    View view = (View) event.getLocalState();
                    //stop displaying the view where it was before it was dragged
                    view.setVisibility(View.INVISIBLE);
                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;
                    //view being dragged and dropped
                    TextView dropped = (TextView) view;
                    //update the text in the target view to reflect the data being dropped
                    switch(dropTarget.getId()){
                        case R.id.chest:
                            Log.d("mythFact:chest", answers.get(i) + "");
                            if(answers.get(i).compareTo("fact")==0) {
                                dropTarget.setText("Correct \n +1");
                                chest.setBackgroundColor(getResources().getColor(R.color.transparent));
                                userCoins++;
                            }
                            else {
                                dropTarget.setText("Wrong");
                                chest.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            break;
                        case R.id.trash:
                            Log.d("mythFact:trash", answers.get(i) + "");
                            if(answers.get(i).compareTo("myth")==0) {
                                dropTarget.setText("Correct \n +1");
                                trash.setBackgroundColor(getResources().getColor(R.color.transparent));
                                userCoins++;
                            }
                            else {
                                dropTarget.setText("Wrong");
                                trash.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            break;
                    }
                    //enable the next button
                    nxtButton.setClickable(true);
                    nxtButton.setBackground(getResources().getDrawable(R.drawable.info_hub_button));
                    tvUserCoins.setText(""+ userCoins);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
