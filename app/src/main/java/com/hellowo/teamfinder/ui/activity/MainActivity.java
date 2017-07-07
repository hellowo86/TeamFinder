package com.hellowo.teamfinder.ui.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityMainBinding;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.model.User;
import com.hellowo.teamfinder.ui.adapter.TeamListAdapter;
import com.hellowo.teamfinder.utils.ViewUtil;
import com.hellowo.teamfinder.viewmodel.MainViewModel;

import gun0912.tedbottompicker.TedBottomPicker;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.hellowo.teamfinder.AppConst.EXTRA_TEAM_ID;
import static com.hellowo.teamfinder.AppConst.EXTRA_USER_ID;

public class MainActivity extends LifecycleActivity {
    ActivityMainBinding binding;
    MainViewModel viewModel;
    TeamListAdapter teamListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        initLayout();
        initObserve();
    }

    private void initLayout() {
        binding.swipeRefreshLy.setProgressViewOffset(false, 0,
                (int) ViewUtil.dpToPx(this, 100));
        binding.swipeRefreshLy.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent));
        binding.swipeRefreshLy.setOnRefreshListener(() -> viewModel.teamsLiveData.loadTeams());
        binding.menuBtn.setOnClickListener(v->binding.drawerLy.openDrawer(Gravity.LEFT));
        binding.accountPhotoImg.setOnClickListener(this::startUserAcivity);
        binding.fab.setOnClickListener(this::clickFab);
        binding.signOutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            FirebaseAuth.getInstance().signOut();
        });
        initTeamRecyclerView();
    }

    private void initTeamRecyclerView() {
        binding.teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamListAdapter = new TeamListAdapter(this, this::clickTeam);
        binding.teamRecyclerView.setAdapter(teamListAdapter);
    }

    private void clickTeam(Team team) {
        Intent intent = new Intent(this, TeamDetailActivity.class);
        intent.putExtra(EXTRA_TEAM_ID, team.getId());
        startActivity(intent);
    }

    private void startUserAcivity(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(EXTRA_USER_ID, viewModel.connectedUserLiveData.getValue().getId());
        startActivity(intent);
    }

    private void initObserve() {
        viewModel.connectedUserLiveData.observe(this, this::updateUserUI);
        viewModel.viewMode.observe(this, this::updateUI);
        viewModel.teamsLiveData.observe(this, teams -> {
            binding.swipeRefreshLy.setRefreshing(false);
            teamListAdapter.notifyDataSetChanged();
        });
    }

    private void updateUI(MainViewModel.ViewMope viewMope) {
        switch (viewMope){
            case SearchTeam:
                binding.mainTopTitle.setText(R.string.search_team);
                binding.searchTeamLy.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void updateUserUI(User user) {
        Glide.with(this)
                .load(!TextUtils.isEmpty(user.getPhotoUrl()) ? user.getPhotoUrl() : R.drawable.default_profile)
                .bitmapTransform(new CropCircleTransformation(this))
                .thumbnail(0.1f)
                .into(binding.accountPhotoImg);
        binding.accountNameText.setText(user.getNickName());
    }

    private void clickFab(View view) {
        startActivity(new Intent(this, CreateTeamActivity.class));
    }

}
