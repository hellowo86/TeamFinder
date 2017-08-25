package com.hellowo.teamfinder.ui.fragment

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.ui.activity.ChatCreateActivity
import com.hellowo.teamfinder.ui.activity.MainActivity
import com.hellowo.teamfinder.viewmodel.FindViewModel
import com.hellowo.teamfinder.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_find.*


class FindFragment : LifecycleFragment() {
    lateinit var viewModel: FindViewModel
    lateinit var mainViewModel: MainViewModel
    var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity).get(FindViewModel::class.java)
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_find, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createBtn.setOnClickListener { startActivity(Intent(activity, ChatCreateActivity::class.java)) }
        searchBtn.setOnClickListener {
            try {
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(activity)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: GooglePlayServicesRepairableException) {
            } catch (e: GooglePlayServicesNotAvailableException) {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            val place = PlaceAutocomplete.getPlace(activity, data)
            (activity as MainActivity).googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
        }
    }
}
