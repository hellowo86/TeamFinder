package com.hellowo.teamfinder.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hellowo.teamfinder.model.User;
import com.hellowo.teamfinder.viewmodel.SignUpViewModel;

public class Me extends LiveData<User> {
    private static Me sInstance;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private User me;

    @MainThread
    public static Me get() {
        if (sInstance == null) {
            sInstance = new Me();
        }
        return sInstance;
    }

    private Me() {
        this.mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        me = new User();
        mAuthListener = (firebaseAuth) -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                me.setId(user.getUid());
                me.setEmail(user.getEmail());
                setValue(me);
            }
        };
    }

    @Override
    protected void onActive() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onInactive() {
        mAuth.removeAuthStateListener(mAuthListener);
    }
}