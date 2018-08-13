package com.peacecorps.malaria.ui.user_profile.badge_screen;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.ui.base.BaseFragment;
import com.peacecorps.malaria.utils.InjectionClass;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.peacecorps.malaria.ui.user_profile.badge_screen.BadgeScreenContract.*;

/**
 * Created by Anamika Tripathi on 24/7/18.
 */
public class BadgeScreenFragment extends BaseFragment implements BadgeMvpView {

    private Dialog achievementDialog;
    private Context context;

    private TextView badgeText;
    private ImageView badgeImage;
    @BindView(R.id.btn_achievement_medicine)
    TextView achievementCatTv;
    private BadgeScreenPresenter<BadgeScreenFragment> presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.badge_room, container, false);
        ButterKnife.bind(this, view);
        context = getContext();
        presenter = new BadgeScreenPresenter<>(InjectionClass.provideDataManager(context), context);
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    protected int getContentResource() {
        return R.layout.badge_room;
    }

    @Override
    protected void init() {
        setUpDialog();
    }

    //open dialog to display the current badge
    private void setUpDialog() {
        achievementDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        achievementDialog.setContentView(R.layout.badge_room_dialog);
        badgeText = achievementDialog.findViewById(R.id.badge_text);
        badgeImage = achievementDialog.findViewById(R.id.badge_image);
        achievementCatTv = achievementDialog.findViewById(R.id.achievement_category);
    }

    @OnClick(R.id.btn_share_button)
    public void shareButtonListener(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                presenter.getUserName() + " unlocked a new badge 'Champ'!! Score : " + presenter.getUserScore());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share Via..."));
    }

    @OnClick(R.id.btn_achievement_medicine)
    public void achievementMedicineListener(View view) {
        presenter.selectUserBadge();
    }

    @OnClick(R.id.btn_achievement_qa)
    public void achievementQAListener(View view) {
        presenter.selectGameBadge();
    }

    @Override
    public void startCategoryMedicineDialog(Drawable badgeDrawable) {
        achievementCatTv.setText(R.string.heading_achievement_one);
        badgeText.setText(R.string.label_first_achievement);
        badgeImage.setImageDrawable(badgeDrawable);
        achievementDialog.show();
    }

    @Override
    public void startCategoryQADialog(Drawable badgeDrawable) {
        achievementCatTv.setText(R.string.heading_achievement_two);
        badgeImage.setImageDrawable(badgeDrawable);
        badgeText.setText(R.string.label_achievement_two);
        achievementDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }
}
