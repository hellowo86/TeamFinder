package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.GameData;
import com.hellowo.teamfinder.databinding.ActivityTeamDetailBinding;
import com.hellowo.teamfinder.model.Game;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.ui.adapter.CommentListAdapter;
import com.hellowo.teamfinder.ui.adapter.decoration.VerticalSpaceDecoration;
import com.hellowo.teamfinder.ui.dialog.EnterCommentDialog;
import com.hellowo.teamfinder.ui.fragment.CommentListFragment;
import com.hellowo.teamfinder.ui.fragment.MemberListFragment;
import com.hellowo.teamfinder.ui.fragment.TeamInfoFragment;
import com.hellowo.teamfinder.utils.ViewUtil;
import com.hellowo.teamfinder.viewmodel.TeamDetailViewModel;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

import static com.hellowo.teamfinder.AppConst.EXTRA_TEAM_ID;

public class TeamDetailActivity extends LifecycleActivity {
    ActivityTeamDetailBinding binding;
    TeamDetailViewModel viewModel;
    CommentListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_team_detail);
        viewModel = ViewModelProviders.of(this).get(TeamDetailViewModel.class);
        viewModel.initTeam(getIntent().getStringExtra(EXTRA_TEAM_ID));
        initLayout();
        initObserve();
    }

    private void initLayout() {
        binding.backBtn.setOnClickListener(v->finish());
        binding.progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.enterCommentBtn.setOnClickListener(v->showEnterCommentDialog());
        initCommentRecyclerView();
    }

    private void initCommentRecyclerView() {
        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentListAdapter(
                this,
                viewModel.comments.getValue(),
                userId -> {});
        binding.commentRecyclerView.setAdapter(adapter);
        binding.commentRecyclerView.addItemDecoration(new VerticalSpaceDecoration(this));
    }

    private void initObserve() {
        viewModel.team.observe(this, this::updateUI);
        viewModel.comments.observe(this, teamList->adapter.notifyDataSetChanged());
        viewModel.loading.observe(this, loading -> {binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);});
    }

    private void updateUI(Team team) {
        final Game game = GameData.get().getGame(team.getGameId());
        final Member organizer = team.getOrganizer();

        binding.titleText.setText(team.getTitle());
        binding.nameText.setText(team.getOrganizer().getName());
        //binding.memberCountText.setText(team.makeMemberText());
        //binding.activeTimeText.setText(team.makeActiveTimeText());
        //binding.commentCountText.setText(String.valueOf(team.getCommentCount()));

        HashTagHelper tagHelper = HashTagHelper.Creator.create(getColor(R.color.primaryText), null);
        tagHelper.handle(binding.subText);

        if(!TextUtils.isEmpty(team.getDescription())) {
            binding.subText.setVisibility(View.VISIBLE);
            binding.subText.setText(team.getDescription());
        }else {
            binding.subText.setVisibility(View.GONE);
        }
/*
        Glide.with(this)
                .load(game.getIconId())
                .into(binding.gameIconImg);
        binding.gameText.setText(game.getTitle());
*/
        Glide.with(this)
                .load(!TextUtils.isEmpty(organizer.getUserId()) ? organizer.getPhotoUrl() : R.drawable.default_profile)
                .bitmapTransform(new CropCircleTransformation(this))
                .thumbnail(0.1f)
                .placeholder(R.drawable.default_profile)
                .into(binding.profileImage);
    }

    private void showEnterCommentDialog() {
        final EnterCommentDialog dialog = new EnterCommentDialog();
        dialog.setDialogInterface(text -> {
            viewModel.postComment(text);
            dialog.dismiss();
        });
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }
}
