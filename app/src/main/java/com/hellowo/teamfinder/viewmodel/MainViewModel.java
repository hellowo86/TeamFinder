package com.hellowo.teamfinder.viewmodel;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    public enum Status{ShowPhotoPicker, UploadPhoto, CompleteUpload}
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
        status.setValue(Status.UploadPhoto);
        String filePath = FileUtil.getPath(App.context, uri);
        if(filePath != null) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            float widthScale = options.outWidth / 128;
            float heightScale = options.outHeight / 128;
            float scale = widthScale > heightScale ? widthScale : heightScale;

            if(scale >= 10) {
                options.inSampleSize = 10;
            }else if(scale >= 8) {
                options.inSampleSize = 8;
            }else if(scale >= 4) {
                options.inSampleSize = 4;
            }else if(scale >= 2) {
                options.inSampleSize = 2;
            }else {
                options.inSampleSize = 1;
            }

            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://teamfinder-32133.appspot.com/userPhoto/"
                            + connectedUserLiveData.getSnapshot().getId() + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);

            UploadTask uploadTask = storageRef.putStream(bis);

            uploadTask.addOnFailureListener(exception -> {
                status.setValue(Status.CompleteUpload);
            }).addOnSuccessListener(taskSnapshot -> {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                FirebaseDatabase.getInstance().getReference()
                        .child(User.DB_REF)
                        .child(connectedUserLiveData.getSnapshot().getId())
                        .child(User.KEY_PHOTO_URL)
                        .setValue(downloadUrl.getPath(), (error, databaseReference)->{
                            status.setValue(Status.CompleteUpload);
                        });
            });

        }
    }

}
