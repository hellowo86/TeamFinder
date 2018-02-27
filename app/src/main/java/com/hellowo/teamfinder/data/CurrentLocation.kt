package com.hellowo.teamfinder.data

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places

object CurrentLocation {
    @SuppressLint("MissingPermission")
    fun setCurrentLocation(activity: AppCompatActivity, callback: (Place) -> Unit) {
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