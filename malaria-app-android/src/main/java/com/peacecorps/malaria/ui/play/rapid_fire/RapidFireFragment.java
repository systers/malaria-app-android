package com.peacecorps.malaria.ui.play.rapid_fire;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.ui.play.rapid_fire.RapidFireContract.RapidFireMvpView;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anamika Tripathi on 19/7/18.
 */
public class RapidFireFragment extends BaseFragment implements RapidFireMvpView {
    // used for saving values in saveInstanceState
    private static final String COUNTER_MILLIS_LEFT = "COUNTER_MILLIS_LEFT";
    private static final String GAME_SCORE = "GAME_SCORE";
    private static final String QUES_NO = "QUES_NO";
    // options for game
    @BindView(R.id.btn_rapid_option_one)
    Button optionOne;
    @BindView(R.id.btn_rapid_option_two)
    Button optionTwo;
    @BindView(R.id.btn_rapid_option_three)
    Button optionThree;
    // question textview
    @BindView(R.id.tv_rapid_question)
    TextView tvQuestion;
    // displays game score
    @BindView(R.id.tv_score)
    TextView totalScore;
    // timer textView
    @BindView(R.id.tv_timers)
    TextView timerView;

    private RapidFirePresenter<RapidFireFragment> presenter;
    private Context context;
    // keeps count of current question number, volatile (variable getting updated across threads)
    private volatile int quesNo;
    // used for timer
    private volatile long millisLeft;
    // saves game score
    private int gameScore;
    private RapidFireTimeCounter counter;
    private long timercount;
    private OnRapidFragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rapid_fire, container, false);
        // butterknife binding
        ButterKnife.bind(this, view);
        context = getContext();
        // instantiating presenter, passing datamanger, view & attaching view to the presenter
        presenter = new RapidFirePresenter<>(InjectionClass.provideDataManager(context), context);
        presenter.attachView(this);
        return view;
    }

    @Override
    protected int getContentResource() {
        return R.layout.rapid_fire;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        if (savedInstanceState != null) {
            // saved values from orientation changes
            restoreFromInstanceState(savedInstanceState);
        } else init();
        setGameScore();
        //displays the time left for the next question
        updateTimer(millisLeft / 1000);
        presenter.prepareQuestionList(quesNo);
    }

    // option one for game listener
    @OnClick(R.id.btn_rapid_option_one)
    public void optionOneListener(View view) {
        // stops counter
        counter.cancel();
        // checks if correct answers for the current quesNO is 1 or not
        if (presenter.getQuestionModel(quesNo).getAns() == 1) {
            // increment gamescrore, set button background to green to display right answers
            gameScore++;
            optionOne.setBackgroundColor(getResources().getColor(R.color.light_green));
        } else {
            optionOne.setBackground(getResources().getDrawable(R.color.colorAccent));
        }

        // disable all buttons till next question is displayed
        enableOptions(false);
        prepNextQues();
    }

    // option two for game listener
    @OnClick(R.id.btn_rapid_option_two)
    public void optionTwoListener(View view) {
        // stops counter
        counter.cancel();
        // checks if correct answers for the current quesNO is 2 or not
        if (presenter.getQuestionModel(quesNo).getAns() == 2) {
            // increment gamescrore, set button background to green to display right answers else red background
            gameScore++;
            optionTwo.setBackgroundColor(getResources().getColor(R.color.light_green));
        } else {
            optionTwo.setBackground(getResources().getDrawable(R.color.colorAccent));
        }
        // disable all buttons till next question is displayed
        enableOptions(false);
        prepNextQues();
    }

    // option three for game listener
    @OnClick(R.id.btn_rapid_option_three)
    public void optionThreeListener(View view) {
        // stops counter
        counter.cancel();
        // checks if correct answers for the current quesNO is 3 or not
        if (presenter.getQuestionModel(quesNo).getAns() == 3) {
            // increment gamescrore, set button background to green to display right answers else red background
            gameScore++;
            optionThree.setBackgroundColor(getResources().getColor(R.color.light_green));
        } else {
            optionThree.setBackground(getResources().getDrawable(R.color.colorAccent));
        }
        // disable all buttons till next question is displayed
        enableOptions(false);
        prepNextQues();

    }

    @OnClick(R.id.btn_rapid_fire_exit)
    public void exitGameListener(View view) {
        // stops counter
        counter.cancel();
        // create a dialog with exit_game_dialog layout
        final Dialog alertDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        alertDialog.setContentView(R.layout.exit_game_dialog);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        alertDialog.setTitle("Game Over!!");
        // positive button listener for dialog
        alertDialog.findViewById(R.id.exitGameDialogButtonOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dismiss dialog, update sharedPrefernces with game score & go back to play fragment
                alertDialog.dismiss();
                presenter.updateGameScore(gameScore);
                listener.goBackToPlayFragment();
            }
        });
        // negative button listener for dialog
        alertDialog.findViewById(R.id.exitGameDialogButtonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dismiss dialog, start counter again
                alertDialog.dismiss();
                counter = new RapidFireTimeCounter(timercount, 1000);
                counter.start();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        doKeepDialog(alertDialog);
    }

    // sets score value by appending to the string
    private void setGameScore() {
        totalScore.setText(getString(R.string.label_rapid_fire_total_score, gameScore));
    }

    // in case the game is coming from saved instance, below parameters are changed to saved values
    private void restoreFromInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            millisLeft = savedInstanceState.getLong(COUNTER_MILLIS_LEFT);
            gameScore = savedInstanceState.getInt(GAME_SCORE);
            quesNo = savedInstanceState.getInt(QUES_NO);
        }
    }

    @Override
    protected void init() {
        // below parameters are initialized with their initial value when game starts
        quesNo = 0;
        gameScore = 0;
        millisLeft = 6000;

    }

    private void prepNextQues() {
        setGameScore();
        final android.os.Handler handler = new android.os.Handler();
        //provide a delay of 1 sec after an option is clicked by the user
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // it can happen due to orientation changes
                if (quesNo < presenter.questionListSize()) {
                    quesNo++;

                    if (quesNo < presenter.questionListSize()) {
                        millisLeft = 6000;
                        prepareQuestionAndOptions();
                    } else {
                        showDialog();
                    }
                }
            }
        }, 1000);
    }

    // setting button background to application theme when next question is set up
    @Override
    public void setButtonBackground() {
        optionOne.setBackground(getResources().getDrawable(R.drawable.button_background));
        optionTwo.setBackground(getResources().getDrawable(R.drawable.button_background));
        optionThree.setBackground(getResources().getDrawable(R.drawable.button_background));
    }

    // receives model from presenter, sets it to the question, options and enables the buttons
    @Override
    public void prepareQuestionAndOptions() {
        setButtonBackground();
        if (quesNo == presenter.questionListSize())
            quesNo--;
        QuestionModel model = presenter.getQuestionModel(quesNo);
        tvQuestion.setText(model.getQuestion());
        optionOne.setText(model.getOption1());
        optionTwo.setText(model.getOption2());
        optionThree.setText(model.getOption3());

        enableOptions(true);
        //checks that quesNo is within questionList size which is possible on orientation change
        if (quesNo != presenter.questionListSize()) {
            counter = new RapidFireTimeCounter(millisLeft, 1000);
            counter.start();
        }
    }

    // enables/display all options val transferred to function
    @Override
    public void enableOptions(boolean val) {
        optionOne.setEnabled(val);
        optionOne.setClickable(val);
        optionTwo.setEnabled(val);
        optionTwo.setClickable(val);
        optionThree.setEnabled(val);
        optionThree.setClickable(val);
    }

    // updat timer
    @Override
    public void updateTimer(long miliLeft) {
        timerView.setText(getString(R.string.blank_text, miliLeft / 1000));
    }

    // Dialog shown when game is over
    public void showDialog() {
        final Dialog alertDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        alertDialog.setContentView(R.layout.game_over_dialog);
        alertDialog.setCancelable(false);

        // Setting Dialog Title
        Button ok = alertDialog.findViewById(R.id.gameOverDialogButtonOK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dismiss the dialog, update game score in prefernces, starts play fragment again
                alertDialog.dismiss();
                presenter.updateGameScore(gameScore);
                listener.goBackToPlayFragment();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        doKeepDialog(alertDialog);
    }

    // attaches the dialog with WindowManager to avoid cancelling on orientation change
    private static void doKeepDialog(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        }
    }

    // saving value in case of orientation changes
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(COUNTER_MILLIS_LEFT, millisLeft);
        outState.putInt(GAME_SCORE, gameScore);
        outState.putInt(QUES_NO, quesNo);
    }

    // cancel counter, set presenter and listener to null
    @Override
    public void onDetach() {
        super.onDetach();
        counter.cancel();
        presenter = null;
        listener = null;
    }

    //Implementing the timer
    public class RapidFireTimeCounter extends CountDownTimer {
        RapidFireTimeCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            timercount = l;
            timerView.setText(getString(R.string.blank_text, l / 1000));
            millisLeft = l;

        }

        @Override
        public void onFinish() {
            counter.cancel();
            prepNextQues();
        }
    }

    public interface OnRapidFragmentListener {
        void goBackToPlayFragment();
    }

    // Container Activity must implement this interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof OnRapidFragmentListener) {
            listener = (OnRapidFragmentListener) context;
        } else {
            ToastLogUtil.showErrorLog(context.toString() + "must implement OnRapidFragmentListener");
        }
    }
}
