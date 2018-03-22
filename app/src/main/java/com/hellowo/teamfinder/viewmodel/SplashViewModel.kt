package com.hellowo.teamfinder.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.fcm.FirebaseInstanceIDService
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.utils.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class SplashViewModel : ViewModel() {
    var loading = MutableLiveData<Boolean>()
    var successLogin = MutableLiveData<Boolean>()


    fun signIn(email: String, password: String) {
        loading.value = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener({ task ->
                    loading.value = false
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
                    } else {
                        FirebaseInstanceIDService.sendRegistrationToServer()
                        successLogin.value = true
                    }
                })
    }

    fun signUp(user: User, password: String, uri: Uri) {
        loading.value = true
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email!!, password)
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
                        initUserProfile(it.result.user, user, uri)
                    }
                })
    }

    private fun initUserProfile(auth: FirebaseUser, user: User, uri: Uri) {
        user.id = auth.uid
        try {
            val filePath = FileUtil.getPath(App.context, uri)
            val bitmap = BitmapUtil.makeProfileBitmapFromFile(filePath)
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://teamfinder-32133.appspot.com/userPhoto/${user.id}.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val data = baos.toByteArray()
            val bis = ByteArrayInputStream(data)
            val uploadTask = storageRef.putStream(bis)

            uploadTask.addOnFailureListener { exception ->
                bitmap.recycle()
            }.addOnSuccessListener { taskSnapshot ->
                bitmap.recycle()
                FirebaseDatabase.getInstance().reference
                        .child(KEY_USERS)
                        .child(user.id)
                        .setValue(user) { error, databaseReference ->
                            FirebaseInstanceIDService.sendRegistrationToServer()
                            loading.value = false
                            successLogin.value = true
                        }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            loading.value = false
        }
    }
}
