package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.User;
import com.hellowo.teamfinder.utils.StringUtil;

public class SignUpViewModel extends ViewModel {
    public enum Status{InvalidNickName, InvalidEmail, InvalidPassword, PolicyUncheck, CompleteSignUp}
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    public MutableLiveData<Status> status = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public SignUpViewModel() {
        super();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(String nickName, String email, String password, boolean policyCheck){
        if(nickName.trim().length() < 2) {
            status.setValue(Status.InvalidNickName);
        }else if(!StringUtil.isEmailValid(email.trim())){
            status.setValue(Status.InvalidEmail);
        }else if(password.length() < 8){
            status.setValue(Status.InvalidPassword);
        }else if(!policyCheck){
            status.setValue(Status.PolicyUncheck);
        }else{
            loading.setValue(true);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e){
                                Toast.makeText(App.context, R.string.existed_email,
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(App.context, R.string.signup_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                            loading.setValue(false);
                        }else {
                            initUserProfile(task.getResult().getUser(), nickName);
                        }
                    });
        }
    }

    private void initUserProfile(FirebaseUser user, String nickName) {
        User me = new User();
        me.setId(user.getUid());
        me.setEmail(user.getEmail());
        me.setNickName(nickName);

        mDatabase.child(User.DB_REF).child(me.getId()).setValue(me, (error, databaseReference)->{
            status.setValue(Status.CompleteSignUp);
            loading.setValue(false);
        });
    }
}
