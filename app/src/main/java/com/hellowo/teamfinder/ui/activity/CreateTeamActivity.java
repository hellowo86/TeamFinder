package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityCreateTeamBinding;
import com.hellowo.teamfinder.databinding.ActivitySignInBinding;
import com.hellowo.teamfinder.viewmodel.CreateTeamViewModel;
import com.hellowo.teamfinder.viewmodel.SignInViewModel;

public class CreateTeamActivity extends LifecycleActivity {
    ActivityCreateTeamBinding binding;
    CreateTeamViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_team);
        viewModel = ViewModelProviders.of(this).get(CreateTeamViewModel.class);

        initLayout();
        initObserve();
    }

    private void initLayout() {
        binding.backBtn.setOnClickListener(v->finish());
    }

    private void initObserve() {
    }

}
