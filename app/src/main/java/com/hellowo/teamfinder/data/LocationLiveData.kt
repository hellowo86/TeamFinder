package com.hellowo.teamfinder.data

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.model.HashTag
import com.hellowo.teamfinder.utils.KEY_CHAT

object LocationLiveData : LiveData<LatLng>() {
    @SuppressLint("MissingPermission")
    fun loadCurrentLocation(googleApiClient: GoogleApiClient) {
        Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null)
                .setResultCallback { likelyPlaces ->
                    for (placeLikelihood in likelyPlaces) {
                        value = placeLikelihood.place.latLng
                        break
                    }
                    likelyPlaces.release()
                }
    }

}