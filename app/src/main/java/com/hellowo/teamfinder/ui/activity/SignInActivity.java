package com.hellowo.teamfinder.ui.activity;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivitySignInBinding;
import com.hellowo.teamfinder.viewmodel.SignInViewModel;

public class SignInActivity extends LifecycleActivity {
    ActivitySignInBinding binding;
    SignInViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        viewModel = ViewModelProviders.of(this).get(SignInViewModel.class);
        initLayout();
        initObserve();
    }

    private void initLayout() {
        binding.backBtn.setOnClickListener(v->finish());
        binding.goSignUpViewBtn.setOnClickListener(v -> goSignUpView());
        binding.signInBtn.setOnClickListener(v -> viewModel.signIn(
                binding.emailEdit.getText().toString(),
                binding.passwordEdit.getText().toString()
        ));
    }

    private void initObserve() {
        viewModel.signInStatus.observe(this, (status)->{
            switch (status) {
                case InvalidEmail:
                    binding.emailEdit.setError(getString(R.string.invalid_email));
                    break;
                case InvalidPassword:
                    binding.passwordEdit.setError(getString(R.string.invalid_password));
                    break;
                case CompleteSignIn:
                    finish();
                    break;
                default:
                    break;
            }
        });

        viewModel.loading.observe(this, aBoolean -> {
            binding.progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            binding.signInBtn.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
        });
    }

    private void goSignUpView(){
        startActivity(new Intent(SignInActivity.this, SingUpActivity.class));
        finish();
    }

}
