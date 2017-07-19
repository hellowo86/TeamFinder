package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.GameData;
import com.hellowo.teamfinder.databinding.ActivityTeamDetailBinding;
import com.hellowo.teamfinder.model.Game;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.ui.adapter.CommentListAdapter;
import com.hellowo.teamfinder.ui.adapter.MemberListAdapter;
import com.hellowo.teamfinder.ui.adapter.RolesAdapter;
import com.hellowo.teamfinder.ui.adapter.decoration.HorizontalDecoration;
import com.hellowo.teamfinder.ui.adapter.decoration.VerticalSpaceDecoration;
import com.hellowo.teamfinder.ui.dialog.EnterCommentDialog;
import com.hellowo.teamfinder.utils.ViewUtil;
import com.hellowo.teamfinder.viewmodel.TeamDetailViewModel;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import static com.hellowo.teamfinder.AppConst.EXTRA_TEAM_ID;

public class TeamDetailActivity extends LifecycleActivity {
    ActivityTeamDetailBinding binding;
    TeamDetailViewModel viewModel;
    MemberListAdapter memberListAdapter;
    RolesAdapter rolesAdapter;
    CommentListAdapter commentListAdapter;

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
        initMemberRecyclerView();
        initRoleRecyclerView();
        initCommentRecyclerView();
    }

    private void initMemberRecyclerView() {
        binding.memberRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        memberListAdapter = new MemberListAdapter(
                this,
                false,
                viewModel.members.getValue(),
                R.layout.list_item_member_horizontal,
                new MemberListAdapter.AdapterInterface() {
                    @Override
                    public void onDeleteClicked(Member member) {}
                    @Override
                    public void onItemClicked(Member member) {}
                });
        binding.memberRecyclerView.setAdapter(memberListAdapter);
        binding.memberRecyclerView.addItemDecoration(
                new HorizontalDecoration((int) ViewUtil.dpToPx(this, 20)));
    }

    private void initRoleRecyclerView() {
        binding.rolesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rolesAdapter = new RolesAdapter(
                this,
                false,
                viewModel.roles.getValue(),
                new RolesAdapter.AdapterInterface() {
                    @Override
                    public void onChangedRoleCount(String role, int count) {}
                    @Override
                    public void onClickedRole(String role, int count) {}
                });
        binding.rolesRecyclerView.setAdapter(rolesAdapter);
    }

    private void initCommentRecyclerView() {
        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentListAdapter = new CommentListAdapter(
                this,
                viewModel.comments.getValue(),
                userId -> {});
        binding.commentRecyclerView.setAdapter(commentListAdapter);
        binding.commentRecyclerView.addItemDecoration(new VerticalSpaceDecoration(this));
    }

    private void initObserve() {
        viewModel.team.observe(this, this::updateUI);
        viewModel.members.observe(this, members -> memberListAdapter.notifyDataSetChanged());
        viewModel.roles.observe(this, roles -> rolesAdapter.refresh(roles));
        viewModel.comments.observe(this, teamList-> commentListAdapter.notifyDataSetChanged());
        viewModel.loading.observe(this, loading -> binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));
    }

    private void updateUI(Team team) {
        final Game game = GameData.INSTANCE.getGames().get(team.getGameId());
        final Member organizer = team.getOrganizer();

        binding.contentsText.setText(team.getTitle());
        //binding.memberCountText.setText(team.makeMemberText());
        //binding.activeTimeText.setText(team.makeActiveTimeText());
        //binding.commentCountText.setText(String.valueOf(team.getCommentCount()));

        HashTagHelper tagHelper = HashTagHelper.Creator.create(getColor(R.color.primaryText), null);
        tagHelper.handle(binding.contentsText);

        Glide.with(this)
                .load(game.getIconId())
                .into(binding.gameIconImg);
        binding.gameTitleText.setText(game.getTitle());

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
