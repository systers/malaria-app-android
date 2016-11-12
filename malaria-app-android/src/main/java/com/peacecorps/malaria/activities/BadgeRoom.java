package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peacecorps.malaria.R;

/**
 * Created by yatna on 30/5/16.
 */
public class BadgeRoom extends Activity {
    private ImageView badge;
    private SharedPreferences sharedPreferences;
    private int userScore;
    private int gameScore;
    private TextView achievementCatTv;
    private Button shareButton;
    private LinearLayout achievementCategory1;
    private LinearLayout achievementCategory2;
    private LinearLayout achievementCategory3;
    private TextView badgeText;
    private Dialog achievementDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badge_room);
        shareButton=(Button)findViewById(R.id.shareButton);
        achievementCategory1=(LinearLayout)findViewById(R.id.cat1_layout);
        achievementCategory2=(LinearLayout)findViewById(R.id.cat2_layout);
        achievementCategory3=(LinearLayout)findViewById(R.id.cat3_layout);
        getScores();
        setUpDialog();
        shareButton.setOnClickListener(shareButtonOnClickListener());
        
        achievementCategory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                achievementCatTv.setText("Medication Punctuality Rewards");
                badge.setImageDrawable(displayUserBadge());
                badgeText.setText("Rookie");
                achievementDialog.show();
            }
        });
        achievementCategory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                achievementCatTv.setText("Malaria Information Rewards");
                badge.setImageDrawable(displayGameBadge());
                badgeText.setText("Mr-Know-It-All");
                achievementDialog.show();
            }
        });

    }
    //get user's score from shared preferences
    private void getScores(){
        //get user's score and game score
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        userScore=sharedPreferences.getInt("userScore", 0);
        gameScore=sharedPreferences.getInt("gameScore",0);
        Log.d("check", "user score displayed");
    }
    //open dialog to display the current badge
    private void setUpDialog(){
        achievementDialog=new Dialog(BadgeRoom.this,android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        achievementDialog.setContentView(R.layout.badge_room_dialog);
        badge=(ImageView)achievementDialog.findViewById(R.id.userBadge);
        badgeText=(TextView)achievementDialog.findViewById(R.id.badge_text);
        achievementCatTv=(TextView)achievementDialog.findViewById(R.id.achievement_category);

    }

    //get badge based on medication  score
    private Drawable displayUserBadge() {
        Drawable badgeDrawable= null;
        //set badge according to score
        if(userScore<2){
            badgeDrawable=getResources().getDrawable(R.drawable.y_b1);
        } else if(userScore<4){
            badgeDrawable=getResources().getDrawable(R.drawable.y_b3);
        }else if(userScore<6){
            badgeDrawable=getResources().getDrawable(R.drawable.y_b5);
        }else if(userScore<7){
            badgeDrawable=getResources().getDrawable(R.drawable.y_b6);
        }else if(userScore<8){
            badgeDrawable=getResources().getDrawable(R.drawable.y_newbie);
        }else {
            badgeDrawable = getResources().getDrawable(R.drawable.y_hat);
        }
        return badgeDrawable;
    }

    //get badge based on game score
    private Drawable displayGameBadge() {
        Drawable badgeDrawable= null;
        //set badge according to score
        if(gameScore<2){
            badgeDrawable=getResources().getDrawable(R.drawable.y_b1);
        } else if(gameScore<4){
            badgeDrawable=getResources().getDrawable(R.drawable.y_b3);
        }else if(gameScore<6){
            badgeDrawable=getResources().getDrawable(R.drawable.y_b5);
        }else if(gameScore<8){
            badgeDrawable=getResources().getDrawable(R.drawable.y_b6);
        }else if(gameScore<10){
            badgeDrawable=getResources().getDrawable(R.drawable.y_newbie);
        }else{
            badgeDrawable=getResources().getDrawable(R.drawable.y_hat);
        }
       return badgeDrawable;
    }
    //sharing achievements on social media button
    private View.OnClickListener shareButtonOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Yatna unlocked a new badge 'Champ'!! Score : " +userScore);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share Via..."));
            }
        };
    }
}
