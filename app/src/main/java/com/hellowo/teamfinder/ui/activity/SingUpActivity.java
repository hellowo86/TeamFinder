package com.hellowo.teamfinder.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivitySignUpBinding;
import com.hellowo.teamfinder.viewmodel.SignUpViewModel;

public class SingUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        initLayout();
        initObserve();
    }

    private void initLayout() {
        binding.backBtn.setOnClickListener(v->finish());
        binding.goSignInViewBtn.setOnClickListener(v -> goSignInView());
        binding.signUpBtn.setOnClickListener(v -> signUp());
    }

    private void initObserve() {
        viewModel.getSignUpStatus().observe(this, (status)->{
            switch (status) {
                case InvalidNickName:
                    binding.nameEdit.setError(getString(R.string.invalid_nick_name));
                    break;
                case InvalidEmail:
                    binding.emailEdit.setError(getString(R.string.invalid_email));
                    break;
                case InvalidPassword:
                    binding.passwordEdit.setError(getString(R.string.invalid_password));
                    break;
                case PolicyUncheck:
                    Toast.makeText(App.context, R.string.plz_check_policy, Toast.LENGTH_SHORT).show();
                    break;
                case CompleteSignUp:
                    startActivity(new Intent(SingUpActivity.this, MainActivity.class));
                    finish();
                    break;
                default:
                    break;
            }
        });

        viewModel.getLoading().observe(this, aBoolean -> {
            binding.progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            binding.signUpBtn.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
        });
    }

    private void signUp() {
        viewModel.signUp(
                binding.nameEdit.getText().toString().trim(),
                binding.emailEdit.getText().toString().trim(),
                binding.passwordEdit.getText().toString(),
                binding.policyCheck.isChecked()
        );
    }

    public void goSignInView(){
        startActivity(new Intent(SingUpActivity.this, SignInActivity.class));
        finish();
    }
}
