package com.hellowo.teamfinder.viewmodel;

import android.Manifest;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.model.User;
import com.hellowo.teamfinder.utils.BitmapUtil;
import com.hellowo.teamfinder.utils.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class UserViewModel extends ViewModel {
    public MutableLiveData<User> user = new MutableLiveData<>();
    public MutableLiveData<Boolean> isUploading = new MutableLiveData<>();
    public MutableLiveData<Integer> showToast = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private String userId;

    public void initUser(String userId) {
        this.userId = userId;
        getUser();
    }

    public void getUser() {
        loading.setValue(true);
        FirebaseDatabase.getInstance().getReference().child(User.DB_REF)
                .child(userId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                user.setValue(dataSnapshot.getValue(User.class));
                                loading.setValue(false);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                loading.setValue(false);
                            }
                        });
    }

    public void uploadPhoto(Uri uri) {
        isUploading.setValue(true);
        try {
            String filePath = FileUtil.getPath(App.context, uri);
            Bitmap bitmap = BitmapUtil.makeProfileBitmapFromFile(filePath);

            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://teamfinder-32133.appspot.com/userPhoto/" + userId + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);

            UploadTask uploadTask = storageRef.putStream(bis);

            uploadTask.addOnFailureListener(exception -> {
                bitmap.recycle();
                isUploading.setValue(false);
                showToast.setValue(R.string.failed_upload);
            }).addOnSuccessListener(taskSnapshot -> {
                bitmap.recycle();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                FirebaseDatabase.getInstance().getReference()
                        .child(User.DB_REF)
                        .child(userId)
                        .child(User.KEY_PHOTO_URL)
                        .setValue(downloadUrl.toString(),
                                (error, ref)->{
                                     isUploading.setValue(false);
                                     getUser();
                                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            isUploading.setValue(false);
            showToast.setValue(R.string.failed_upload);
        }
    }
}
