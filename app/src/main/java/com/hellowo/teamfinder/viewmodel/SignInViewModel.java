package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.hellowo.teamfinder.App;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.fcm.FirebaseInstanceIDService;
import com.hellowo.teamfinder.utils.StringUtil;

public class SignInViewModel extends ViewModel {
    public enum SignInStatus {InvalidEmail, InvalidPassword, CompleteSignIn}
    private FirebaseAuth mAuth;
    public MutableLiveData<SignInStatus> signInStatus = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public SignInViewModel() {
        super();
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(String email, String password){
        if(!StringUtil.isEmailValid(email.trim())){
            signInStatus.setValue(SignInStatus.InvalidEmail);
        }else if(password.length() < 8){
            signInStatus.setValue(SignInStatus.InvalidPassword);
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
                            FirebaseInstanceIDService.Companion.sendRegistrationToServer();
                            signInStatus.setValue(SignInStatus.CompleteSignIn);
                            loading.setValue(false);
                        }
                    });
        }
    }
}
