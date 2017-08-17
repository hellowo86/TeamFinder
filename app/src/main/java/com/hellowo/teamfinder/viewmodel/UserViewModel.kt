package com.hellowo.teamfinder.viewmodel

import android.Manifest
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.net.Uri
import android.view.View

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hellowo.teamfinder.App
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.utils.BitmapUtil
import com.hellowo.teamfinder.utils.FileUtil
import com.hellowo.teamfinder.utils.KEY_PHOTO_URL
import com.hellowo.teamfinder.utils.KEY_USERS

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class UserViewModel : ViewModel() {
    var user = MutableLiveData<User>()
    var isUploading = MutableLiveData<Boolean>()
    var showToast = MutableLiveData<Int>()
    var loading = MutableLiveData<Boolean>()
    private var userId: String? = null

    fun initUser(userId: String) {
        this.userId = userId
        loadUser()
    }

    fun loadUser() {
        loading.setValue(true)
        FirebaseDatabase.getInstance().reference.child(KEY_USERS)
                .child(userId!!)
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                user.setValue(dataSnapshot.getValue(User::class.java))
                                loading.setValue(false)
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                loading.setValue(false)
                            }
                        })
    }

    fun uploadPhoto(uri: Uri) {
        isUploading.setValue(true)
        try {
            val filePath = FileUtil.getPath(App.context, uri)
            val bitmap = BitmapUtil.makeProfileBitmapFromFile(filePath)

            val storageRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://teamfinder-32133.appspot.com/userPhoto/$userId.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val bis = ByteArrayInputStream(data)

            val uploadTask = storageRef.putStream(bis)

            uploadTask.addOnFailureListener { exception ->
                bitmap.recycle()
                isUploading.setValue(false)
                showToast.setValue(R.string.failed_upload)
            }.addOnSuccessListener { taskSnapshot ->
                bitmap.recycle()
                val downloadUrl = taskSnapshot.downloadUrl
                FirebaseDatabase.getInstance().reference
                        .child(KEY_USERS)
                        .child(userId!!)
                        .child(KEY_PHOTO_URL)
                        .setValue(downloadUrl!!.toString()) { error, ref ->
                            isUploading.setValue(false)
                            loadUser()
                        }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isUploading.setValue(false)
            showToast.setValue(R.string.failed_upload)
        }

    }
}
