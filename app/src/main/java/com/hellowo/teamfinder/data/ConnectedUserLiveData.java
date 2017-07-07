package com.hellowo.teamfinder.data;

import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hellowo.teamfinder.model.User;

import java.io.File;

public class ConnectedUserLiveData extends LiveData<User> {
    private static ConnectedUserLiveData sInstance;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ValueEventListener valueEventListener;
    private DatabaseReference mDatabase;

    @MainThread
    public static ConnectedUserLiveData get() {
        if (sInstance == null) {
            sInstance = new ConnectedUserLiveData();
        }
        return sInstance;
    }

    private ConnectedUserLiveData() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = (firebaseAuth) -> {
            removeValueEventListener();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                setValueEventListener(user.getUid());
            }else{
                setValue(null);
            }
        };
    }

    private void setValueEventListener(String userId) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setValue(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabase.child(User.DB_REF).child(userId).addValueEventListener(valueEventListener);
    }

    @Override
    protected void onActive() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onInactive() {
        removeValueEventListener();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void removeValueEventListener() {
        if(valueEventListener != null) {
            mDatabase.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
    }
}