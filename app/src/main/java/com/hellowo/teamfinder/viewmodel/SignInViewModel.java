package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.utils.StringUtil;

public class SignInViewModel extends ViewModel {
    public enum Status{InvalidEmail, InvalidPassword, CompleteSignIn}
    private FirebaseAuth mAuth;
    public MutableLiveData<Status> status = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public SignInViewModel() {
        super();
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(String email, String password){
        if(!StringUtil.isEmailValid(email.trim())){
            status.setValue(Status.InvalidEmail);
        }else if(password.length() < 8){
            status.setValue(Status.InvalidPassword);
        }else{
            loading.setValue(true);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e){
                                Toast.makeText(App.context, R.string.not_existed_user,
                                        Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                Toast.makeText(App.context, R.string.incorrect_password,
                                        Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(App.context, R.string.signup_failed,
                                        Toast.LENGTH_SHORT).show();
                            }
                            loading.setValue(false);
                        }else {
                            status.setValue(Status.CompleteSignIn);
                            loading.setValue(false);
                        }
                    });
        }
    }
}
