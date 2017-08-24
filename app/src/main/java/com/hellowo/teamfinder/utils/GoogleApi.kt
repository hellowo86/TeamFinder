package com.hellowo.teamfinder.utils

import android.arch.lifecycle.LifecycleActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places

var googleApiClient: GoogleApiClient? = null

fun getGoogleApiClient(activity: LifecycleActivity): GoogleApiClient? {
    googleApiClient?.let {
        return it
    }
    googleApiClient = GoogleApiClient.Builder(activity)
            .addApi(Places.GEO_DATA_API)
            .addApi(Places.PLACE_DETECTION_API)
            .enableAutoManage(activity) { result ->  }
            .build()
    return googleApiClient
}