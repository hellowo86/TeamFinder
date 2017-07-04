package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.databinding.ActivitySplashBinding;
import com.hellowo.teamfinder.databinding.ActivityTeamBinding;
import com.hellowo.teamfinder.viewmodel.SplashViewModel;
import com.hellowo.teamfinder.viewmodel.TeamViewModel;

public class TeamActivity extends LifecycleActivity {
    ActivityTeamBinding binding;
    TeamViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_team);
        viewModel = ViewModelProviders.of(this).get(TeamViewModel.class);
        initLayout();
        initObserve();
    }

    private void initLayout() {

    }

    private void initObserve() {
    }
}
