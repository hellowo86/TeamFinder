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
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityMainBinding;
import com.hellowo.teamfinder.model.User;
import com.hellowo.teamfinder.utils.ViewUtil;
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

        initLayout();
        initObserve();
    }

    private void initLayout() {
        binding.swipeRefreshLy.setProgressViewOffset(false, 0,
                (int) ViewUtil.dpToPx(this, 72));

        binding.menuBtn.setOnClickListener(v->binding.drawerLy.openDrawer(Gravity.LEFT));
        binding.accountPhotoImg.setOnClickListener(viewModel::checkExternalStoragePermission);
        binding.fab.setOnClickListener(this::clickFab);
    }

    private void initObserve() {
        viewModel.connectedUserLiveData.observe(this, this::updateUserUI);
        viewModel.status.observe(this, this::updateUI);
    }

    private void updateUI(MainViewModel.Status status) {
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
            case FailedUpload:
                Toast.makeText(App.context, R.string.failed_upload, Toast.LENGTH_SHORT).show();
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
                Glide.with(this).load(user.getPhotoUrl())
                        .bitmapTransform(new CropCircleTransformation(this))
                        .thumbnail(0.1f)
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

    private void clickFab(View view) {
        startActivity(new Intent(this, CreateTeamActivity.class));
    }

}
