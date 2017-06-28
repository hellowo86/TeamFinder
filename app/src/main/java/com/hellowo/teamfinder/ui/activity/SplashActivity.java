package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.databinding.ActivitySignInBinding;
import com.hellowo.teamfinder.databinding.ActivitySplashBinding;
import com.hellowo.teamfinder.viewmodel.SignInViewModel;
import com.hellowo.teamfinder.viewmodel.SplashViewModel;

public class SplashActivity extends LifecycleActivity {
    ActivitySplashBinding binding;
    SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        viewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        initLayout();
        initObserve();
    }

    private void initLayout() {}

    private void initObserve() {
        ConnectedUserLiveData.get().observe(this, user -> {
            if(user != null) {
                ConnectedUserLiveData.get().removeObservers(this);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }else{
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            }
        });
    }
}
