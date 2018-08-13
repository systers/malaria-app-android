package com.peacecorps.malaria.ui.play.myth_vs_fact;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.ui.play.myth_vs_fact.MythFactContract.MythFactMvpView;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anamika Tripathi on 18/7/18.
 */
public class MythFactFragment extends BaseFragment implements MythFactMvpView {

    private Context context;
    private List<String> questions, answers;
    @BindView(R.id.tv_total_score)
    TextView tvUserPoints;
    @BindView(R.id.tv_question)
    TextView tvQuestion;
    @BindView(R.id.btn_next)
    Button nextButton;
    @BindView(R.id.btn_exit)
    Button exitButton;
    @BindView(R.id.chest)
    TextView chest;
    @BindView(R.id.trash)
    TextView trash;
    private int points = 0, i = 0;
    private MythFactPresenter<MythFactFragment> presenter;
    private OnMythFragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myth_fact_game, container, false);
        context = getContext();
        ButterKnife.bind(this, view);
        presenter = new MythFactPresenter<>(InjectionClass.provideDataManager(context), context);
        presenter.attachView(this);
        return view;
    }

    @Override
    protected int getContentResource() {
        return R.layout.myth_fact_game;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set touch listeners
        tvQuestion.setOnTouchListener(new ChoiceTouchListener());
        //set drag listeners
        trash.setOnDragListener(new ChoiceDragListener());
        chest.setOnDragListener(new ChoiceDragListener());
        init();
    }

    @Override
    protected void init() {
        presenter.checkFirstTime();
        //setup questions
        questions = Arrays.asList(getResources().getStringArray(R.array.array_myth_vs_fact_questions));
        //setup answers
        answers = Arrays.asList(getResources().getStringArray(R.array.array_myth_vs_fact_answers));
        //initialize game score
        tvUserPoints.setText(String.valueOf(points));
        //initial question
        tvQuestion.setText(questions.get(i));
        //disable the next Button
        nextButton.setClickable(false);
        nextButton.setBackground(getResources().getDrawable(R.drawable.info_hub_button_grayed));
    }

    @OnClick(R.id.btn_next)
    public void nextButtonListener() {
        i = i + 1;
        if (i < questions.size()) {
            tvQuestion.setText(questions.get(i));
            setUpButton();
        } else {
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
                            // update game score to preferences
                            presenter.updateGameScore(points);
                            dialog.dismiss();
                            listener.goBackToPlayFragment();
                        }
                    });

            AlertDialog dialog = builder.create();
            // display dialog
            dialog.show();
        }
    }

    // set up tvQuestion, trash, chest, next Button, called after question
    private void setUpButton() {
        trash.setText("");
        chest.setText("");
        tvQuestion.setVisibility(View.VISIBLE);
        nextButton.setClickable(false);
        nextButton.setBackground(getResources().getDrawable(R.drawable.info_hub_button_grayed));
        chest.setBackground(getResources().getDrawable(R.drawable.chest));
        trash.setBackground(getResources().getDrawable(R.drawable.trash));
    }

    @OnClick(R.id.btn_exit)
    public void exitButtonListener() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle(getString(R.string.label_exit_game));
        builder.setMessage(getString(R.string.description_sure_exit_game));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss alert dialog, update preferences with game score and restart play fragment
                        presenter.updateGameScore(points);
                        dialog.dismiss();
                        listener.goBackToPlayFragment();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
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
            } else {
                v.performClick();
                return false;
            }
        }
    }

    private class ChoiceDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    //handle the dragged view being dropped over a target view
                    View view = (View) event.getLocalState();
                    //stop displaying the view where it was before it was dragged
                    view.setVisibility(View.INVISIBLE);
                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;
                    //update the text in the target view to reflect the data being dropped
                    switch (dropTarget.getId()) {
                        case R.id.chest:
                            if (answers.get(i).compareTo(getString(R.string.fact)) == 0) {
                                dropTarget.setText(getString(R.string.label_correct));
                                chest.setBackgroundColor(getResources().getColor(R.color.transparent));
                                points++;
                            } else {
                                dropTarget.setText(R.string.wrong);
                                chest.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            break;
                        case R.id.trash:
                            if (answers.get(i).compareTo(getString(R.string.myth)) == 0) {
                                dropTarget.setText(getString(R.string.label_correct));
                                trash.setBackgroundColor(getResources().getColor(R.color.transparent));
                                points++;
                            } else {
                                dropTarget.setText(R.string.wrong);
                                trash.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            break;
                        default:
                            ToastLogSnackBarUtil.showToast(context, "Wrong Drop!");
                    }
                    //enable the next button
                    nextButton.setClickable(true);
                    nextButton.setBackground(getResources().getDrawable(R.drawable.button_background));
                    tvUserPoints.setText(getString(R.string.label_myth_fact_total_score, points));
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

    public interface OnMythFragmentListener {
        void goBackToPlayFragment();
    }

    // Container Activity must implement this interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof OnMythFragmentListener) {
            listener = (OnMythFragmentListener) context;
        } else {
            // Show error Log for debugging & display snackbar to user
            ToastLogSnackBarUtil.showErrorLog(context.toString() + " must implement OnMythFragmentListener");
            if (getActivity() != null) {
                ToastLogSnackBarUtil.showSnackBar(context, getActivity().findViewById(android.R.id.content),
                        "Something went wrong!");
            }
        }
    }

    // setting null values to the listener, presenter
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public void playTapTargetViewer() {
        new TapTargetSequence(getActivity())
                .continueOnCancel(true)
                .targets(
                        TapTarget.forView(tvQuestion, getString(R.string.help_myth_fact_question_title),
                                getString(R.string.help_myth_fact_question_description))
                                .drawShadow(true)
                                .tintTarget(true)
                                .targetRadius(60)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3),
                        TapTarget.forView(chest, getString(R.string.help_myth_fact_chest_title),
                                "")
                                .drawShadow(true)
                                .tintTarget(true)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetRadius(60)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3),
                        TapTarget.forView(trash, getString(R.string.help_myth_fact_bin_title),
                                "")
                                .drawShadow(true)
                                .tintTarget(true)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetRadius(60)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3),
                        TapTarget.forView(tvUserPoints, getString(R.string.help_myth_fact_score_title),
                                "")
                                .drawShadow(true)
                                .tintTarget(true)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetRadius(60)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3),
                        TapTarget.forView(nextButton, getString(R.string.help_myth_fact_next_title),
                                getString(R.string.help_myth_fact_next_description))
                                .drawShadow(true)
                                .tintTarget(true)
                                .titleTextColor(R.color.textColorPrimary)
                                .descriptionTextColor(R.color.white)
                                .targetRadius(60)
                                .targetCircleColor(R.color.colorAccent)
                                .outerCircleAlpha(0.90f).id(3),
                        TapTarget.forView(exitButton, getString(R.string.help_myth_fact_exit_title),
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
                        presenter.getDataManager().setMythFactTarget(true);
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
