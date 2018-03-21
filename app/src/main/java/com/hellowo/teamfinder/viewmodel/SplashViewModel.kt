package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.fcm.FirebaseInstanceIDService
import com.hellowo.teamfinder.utils.StringUtil

class SplashViewModel : ViewModel() {
    enum class SignInStatus {
        InvalidEmail, InvalidPassword
    }
    var signInStatus = MutableLiveData<SignInStatus>()
    var loading = MutableLiveData<Boolean>()


    fun signIn(email: String, password: String) {
        if (!StringUtil.isEmailValid(email.trim())) {
            signInStatus.setValue(SignInStatus.InvalidEmail)
        } else if (password.length < 8) {
            signInStatus.setValue(SignInStatus.InvalidPassword)
        } else {
            loading.value = true
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener({ task ->
                        if (!task.isSuccessful) {
                            try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthInvalidUserException) {
                                Toast.makeText(App.context, R.string.not_existed_user, Toast.LENGTH_SHORT).show()
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(App.context, R.string.incorrect_password, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(App.context, R.string.signup_failed, Toast.LENGTH_SHORT).show()
                            }
                            loading.setValue(false)
                        } else {
                            FirebaseInstanceIDService.sendRegistrationToServer()
                        }
                    })
        }
    }
}
