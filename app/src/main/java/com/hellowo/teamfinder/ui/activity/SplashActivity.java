package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.MeLiveData;
import com.hellowo.teamfinder.databinding.ActivitySplashBinding;
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
        MeLiveData.INSTANCE.observe(this, user -> {
            if(user != null) {
                MeLiveData.INSTANCE.removeObservers(this);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }else{
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            }
        });
    }
}
