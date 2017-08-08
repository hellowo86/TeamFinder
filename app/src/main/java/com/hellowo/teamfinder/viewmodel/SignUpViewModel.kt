package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.fcm.FirebaseInstanceIDService
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.utils.FirebaseUtils
import com.hellowo.teamfinder.utils.StringUtil

import java.util.ArrayList

class SignUpViewModel : ViewModel() {
    enum class SignUpStatus {
        InvalidNickName, InvalidEmail, InvalidPassword, PolicyUncheck, CompleteSignUp
    }

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var signUpStatus = MutableLiveData<SignUpStatus>()
    var loading = MutableLiveData<Boolean>()

    fun signUp(nickName: String, email: String, password: String, policyCheck: Boolean) {
        if (nickName.length < 2 || nickName.length > 10) {
            signUpStatus.setValue(SignUpStatus.InvalidNickName)
        } else if (!StringUtil.isEmailValid(email)) {
            signUpStatus.setValue(SignUpStatus.InvalidEmail)
        } else if (password.length < 8) {
            signUpStatus.setValue(SignUpStatus.InvalidPassword)
        } else if (!policyCheck) {
            signUpStatus.setValue(SignUpStatus.PolicyUncheck)
        } else {
            loading.value = true
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener({
                        if (!it.isSuccessful) {
                            try{
                                throw it.exception!!
                            } catch (e: FirebaseAuthUserCollisionException){
                                Toast.makeText(App.context, R.string.existed_email, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(App.context, R.string.signup_failed, Toast.LENGTH_SHORT).show()
                            }
                            loading.value = false
                        }else {
                            initUserProfile(it.result.user, nickName)
                        }
                    })
        }
    }

    private fun initUserProfile(user: FirebaseUser, nickName: String) {
        val me = User(
                user.uid,
                nickName,
                user.email,
                null,
                0,
                System.currentTimeMillis())

        FirebaseDatabase.getInstance().reference
                .child(FirebaseUtils.KEY_USERS)
                .child(me.id!!)
                .setValue(me) { error, databaseReference ->
                    FirebaseInstanceIDService.sendRegistrationToServer()
                    signUpStatus.value = SignUpStatus.CompleteSignUp
                    loading.setValue(false)
                }
    }
}
