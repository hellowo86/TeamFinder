package com.hellowo.teamfinder.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ActivityUserBinding;
import com.hellowo.teamfinder.model.User;
import com.hellowo.teamfinder.viewmodel.UserViewModel;

import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.hellowo.teamfinder.AppConst.EXTRA_USER_ID;

public class UserActivity extends AppCompatActivity {
    ActivityUserBinding binding;
    UserViewModel viewModel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.initUser(getIntent().getStringExtra(EXTRA_USER_ID));
        initLayout();
        initObserve();
    }

    private void initLayout() {
        binding.profileImage.setOnClickListener(this::checkExternalStoragePermission);
    }

    private void initObserve() {
        viewModel.getUser().observe(this, this::updateUserUI);
        viewModel.isUploading().observe(this, isUploading->{
            if(isUploading) {
                showProgressDialog();
            }else{
                hideProgressDialog();
            }
        });
        viewModel.getShowToast().observe(this, this::showToast);
    }

    private void updateUserUI(User user) {
        Glide.with(this)
                .load(!TextUtils.isEmpty(user.getPhotoUrl()) ? user.getPhotoUrl() : R.drawable.default_profile)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(binding.profileImage);
        binding.nameText.setText(user.getNickName());
    }

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            showPhotoPicker();
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {}
    };

    public void checkExternalStoragePermission(View view) {
        new TedPermission(view.getContext())
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void showPhotoPicker() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(UserActivity.this)
                .setOnImageSelectedListener(uri -> viewModel.uploadPhoto(uri))
                .create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());
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

    private void showToast(Integer stringId) {
        Toast.makeText(App.context, stringId, Toast.LENGTH_SHORT).show();
    }
}
