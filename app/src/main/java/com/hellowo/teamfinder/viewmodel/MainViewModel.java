package com.hellowo.teamfinder.viewmodel;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.model.User;
import com.hellowo.teamfinder.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    public enum Status{ShowPhotoPicker, UpLoadingPhoto, CompleteUpLoad}
    public ConnectedUserLiveData connectedUserLiveData = ConnectedUserLiveData.get();
    public MutableLiveData<Status> status = new MutableLiveData<>();

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            status.setValue(Status.ShowPhotoPicker);
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {}
    };

    public void checkExternalStoragePermission(View view) {
        new TedPermission(view.getContext())
                .setPermissionListener(permissionlistener)
                //.setRationaleMessage(getString(R.string.ask_storage_permission))
                //.setDeniedMessage(getString(R.string.denied_permission))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public void uploadPhoto(Uri uri) {
        status.setValue(Status.UpLoadingPhoto);
        String filePath = FileUtil.getPath(App.context, uri);
        if(filePath != null) {
            Glide.with(App.context).load(new File(filePath)).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    StorageReference storageRef = FirebaseStorage.getInstance()
                            .getReferenceFromUrl("gs://teamfinder-32133.appspot.com/userPhoto/"
                                    + connectedUserLiveData.getSnapshot().getId() + ".jpg");

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = storageRef.putBytes(data);

                    uploadTask.addOnFailureListener(exception -> {
                        status.setValue(Status.CompleteUpLoad);
                    }).addOnSuccessListener(taskSnapshot -> {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        FirebaseDatabase.getInstance().getReference()
                                .child(User.DB_REF)
                                .child(connectedUserLiveData.getSnapshot().getId())
                                .child(User.KEY_PHOTO_URL)
                                .setValue(downloadUrl, (error, databaseReference)->{
                                    status.setValue(Status.CompleteUpLoad);
                                });
                    });
                }
            });
        }
    }

}
