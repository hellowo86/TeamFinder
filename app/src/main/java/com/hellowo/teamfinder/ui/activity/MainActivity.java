package com.hellowo.teamfinder.ui.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.bumptech.glide.Glide;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityMainBinding;
import com.hellowo.teamfinder.model.User;
import com.hellowo.teamfinder.viewmodel.MainViewModel;

import gun0912.tedbottompicker.TedBottomPicker;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends LifecycleActivity {
    ActivityMainBinding binding;
    MainViewModel viewModel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        binding.myAccountBtn.setOnClickListener(v->binding.drawerLy.openDrawer(Gravity.RIGHT));
        binding.menuBtn.setOnClickListener(v->binding.drawerLy.openDrawer(Gravity.LEFT));
        binding.accountPhotoImg.setOnClickListener(viewModel::checkExternalStoragePermission);

        viewModel.connectedUserLiveData.observe(this, this::updateUserUI);
        viewModel.status.observe(this, this::updateUI);
    }

    private void updateUI(MainViewModel.Status status) {
        Log.d("aaa", status.name());
        switch (status){
            case ShowPhotoPicker:
                showPhotoPicker();
                break;
            case UploadPhoto:
                showProgressDialog();
                break;
            case CompleteUpload:
                hideProgressDialog();
                break;
            default:
                break;
        }
    }

    private void showProgressDialog() {
        hideProgressDialog();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.plz_wait));
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void updateUserUI(User user) {
        if(user != null) {
            if(!TextUtils.isEmpty(user.getPhotoUrl())) {
                Glide.with(this).load(User.PHOTO_URL_PREFIX + user.getPhotoUrl())
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(binding.accountPhotoImg);
            }
        }else {
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    private void showPhotoPicker() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(MainActivity.this)
                .setOnImageSelectedListener(viewModel::uploadPhoto)
                .setMaxCount(100)
                .create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }

}
