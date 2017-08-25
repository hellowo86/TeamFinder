package com.hellowo.teamfinder.data

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.model.HashTag
import com.hellowo.teamfinder.utils.KEY_CHAT

object CurrentLocation {
    @SuppressLint("MissingPermission")
    fun setCurrentLocation(activity: LifecycleActivity, callback: (Place) -> Unit) {
        val googleApiClient = GoogleApiClient.Builder(activity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(activity) { result ->  }
                .build()
        Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null)
                .setResultCallback { likelyPlaces ->
                    for (placeLikelihood in likelyPlaces) {
                        callback.invoke(placeLikelihood.place)
                        break
                    }
                    likelyPlaces.release()
                }
    }
}