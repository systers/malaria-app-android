package com.peacecorps.malaria.ui.play.rapid_fire;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.ui.play.rapid_fire.RapidFireContract.RapidFireMvpView;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anamika Tripathi on 19/7/18.
 */
@SuppressWarnings("NonAtomicOperationOnVolatileField")
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
    // question text view
    @BindView(R.id.tv_rapid_question)
    TextView tvQuestion;
    // displays game score
    @BindView(R.id.tv_score)
    TextView totalScore;
    // timer textView
    @BindView(R.id.tv_timers)
    TextView timerView;
    @BindView(R.id.btn_rapid_fire_exit)
    Button exitButton;

    private RapidFirePresenter<RapidFireFragment> presenter;
    private Context context;
    // keeps count of current question number, volatile (variable getting updated across threads)
    private volatile int quesNo;
    // used for timer
    private volatile long millisLeft;
    // saves game score
    private int gameScore;
    private RapidFireTimeCounter counter;
    private long timerCount;
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
        setGameScore();
        //displays the time left for the next question
        updateTimer(millisLeft / 1000);
        //presenter.prepareQuestionList(quesNo);
    }

    // option one for game listener
    @OnClick(R.id.btn_rapid_option_one)
    public void optionOneListener() {
        // stops counter
        counter.cancel();
        // checks if correct answers for the current quesNO is 1 or not
        if (presenter.getQuestionModel(quesNo).getAns() == 1) {
            // increment game score, set button background to green to display right answers
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
    public void optionTwoListener() {
        // stops counter
        counter.cancel();
        // checks if correct answers for the current quesNO is 2 or not
        if (presenter.getQuestionModel(quesNo).getAns() == 2) {
            // increment game score, set button background to green to display right answers else red background
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
    public void optionThreeListener() {
        // stops counter
        counter.cancel();
        // checks if correct answers for the current quesNO is 3 or not
        if (presenter.getQuestionModel(quesNo).getAns() == 3) {
            // increment game score, set button background to green to display right answers else red background
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
    public void exitGameListener() {
        // stops counter
        counter.cancel();
        // create a dialog with AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle(getString(R.string.label_exit_game));
        builder.setMessage(getString(R.string.description_sure_exit_game));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss alert dialog, update preferences with game score and restart play fragment
                        presenter.updateGameScore(gameScore);
                        listener.goBackToPlayFragment();
                        dialog.dismiss();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss dialog, start counter again
                        dialog.dismiss();
                        counter = new RapidFireTimeCounter(timerCount);
                        counter.start();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    // sets score value by appending to the string
    private void setGameScore() {
        totalScore.setText(getString(R.string.label_rapid_fire_total_score, gameScore));
    }

    @Override
    protected void init() {
        // below parameters are initialized with their initial value when game starts
        quesNo = 0;
        gameScore = 0;
        millisLeft = 6000;

        presenter.checkFirstTime();

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
                        showGameOverDialog();
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
            counter = new RapidFireTimeCounter(millisLeft);
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

    // update timer
    @Override
    public void updateTimer(long miliLeft) {
        timerView.setText(getString(R.string.blank_text, miliLeft / 1000));
    }

    // Dialog shown when game is over
    private void showGameOverDialog() {
        // create a dialog with AlertDialog builder, set appropriate title & message
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle(getString(R.string.label_game_over));
        builder.setMessage(getString(R.string.description_wait_for_new_ques));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss the dialog, update game score in preference, starts play fragment again
                        dialog.dismiss();
                        presenter.updateGameScore(gameScore);
                        listener.goBackToPlayFragment();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
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
    class RapidFireTimeCounter extends CountDownTimer {
        RapidFireTimeCounter(long millisInFuture) {
            super(millisInFuture, 1000);
        }

        @Override
        public void onTick(long l) {
            timerCount = l;
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
            // Show error Log for debugging & display snack bar to user
            ToastLogSnackBarUtil.showErrorLog(context.toString() + " must implement OnRapidFragmentListener");
            if (getActivity() != null) {
                ToastLogSnackBarUtil.showSnackBar(context, getActivity().findViewById(android.R.id.content),
                        "Something went wrong!");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        counter.cancel();
    }

    @Override
    public void playTapTargetViewer() {
        new TapTargetSequence(getActivity())
                .continueOnCancel(true)
                .targets(
                        TapTarget.forView(tvQuestion, getString(R.string.help_rapid_fire_question_title),
                                getString(R.string.help_rapid_fire_question_description))
                                .drawShadow(true)
                                .tintTarget(true)
                                .targetRadius(60)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3),
                        TapTarget.forView(totalScore, getString(R.string.help_rapid_fire_score_title),
                                "")
                                .drawShadow(true)
                                .tintTarget(true)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetRadius(60)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3),
                        TapTarget.forView(timerView, getString(R.string.help_rapid_fire_timer_title),
                                getString(R.string.help_rapid_fire_timer_description))
                                .drawShadow(true)
                                .tintTarget(true)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetRadius(60)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3),
                        TapTarget.forView(exitButton, getString(R.string.help_rapid_fire_exit_title),
                                "")
                                .drawShadow(true)
                                .tintTarget(true)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetRadius(60)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3)

                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        presenter.getDataManager().setRapidFireTarget(true);
                        presenter.prepareQuestionList(quesNo);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // no need currently, compulsory overridden method
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // no need currently, compulsory overridden method
                    }
                }).start();
    }


}
