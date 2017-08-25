package com.hellowo.teamfinder.ui.activity

import android.Manifest
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MyChatLiveData
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.ui.fragment.ChatListFragment
import com.hellowo.teamfinder.ui.fragment.FindFragment
import com.hellowo.teamfinder.ui.fragment.TeamInfoFragment
import com.hellowo.teamfinder.utils.KEY_CHAT
import com.hellowo.teamfinder.utils.KEY_DT_ENTERED
import com.hellowo.teamfinder.utils.KEY_USERS
import com.hellowo.teamfinder.viewmodel.MainViewModel
import com.hellowo.teamfinder.viewmodel.MainViewModel.BottomTab
import com.hellowo.teamfinder.viewmodel.MainViewModel.BottomTab.*
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hellowo.teamfinder.data.CategoryData
import com.hellowo.teamfinder.data.CurrentLocation
import java.util.ArrayList


class MainActivity : LifecycleActivity(), OnMapReadyCallback {
    lateinit var viewModel: MainViewModel
    lateinit var googleMap: GoogleMap
    val markerMap: MutableMap<String, Marker> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initLayout()
        initObserve()
        checkIntentExtra()
    }

    private fun initLayout() {
        setMap()
        profileTab.setOnLongClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            FirebaseAuth.getInstance().signOut()
            return@setOnLongClickListener false
        }
        instantTab.setOnClickListener{viewModel.bottomTab.value = Instant}
        chatTab.setOnClickListener{viewModel.bottomTab.value = Chat}
        clanTab.setOnClickListener{viewModel.bottomTab.value = Clan}
        profileTab.setOnClickListener{viewModel.bottomTab.value = Profile}
    }

    private fun setMap() {
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let{
            googleMap = it
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(14f))
            googleMap.setOnCameraIdleListener {
                viewModel.search(googleMap.projection.visibleRegion)
            }
            checkLocationPermission()
        }
    }
    /* 마커 기준으로 맵 옮기기
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    for (Marker marker : markers) {
        builder.include(marker.getPosition());
    }
    LatLngBounds bounds = builder.build();

    int padding = 0; // offset from edges of the map in pixels
    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

    googleMap.moveCamera(cu);
     */

    private fun initObserve() {
        MeLiveData.observe(this, Observer { updateUserUI(it) })
        viewModel.bottomTab.observe(this, Observer { moveTab(it) })
        viewModel.location.observe(this, Observer { googleMap.moveCamera(CameraUpdateFactory.newLatLng(it)) })
        viewModel.chats.observe(this, Observer { addMarkers(it) })
    }

    private fun addMarkers(markers: ArrayMap<String, com.hellowo.teamfinder.model.Chat>?) {
        markers?.forEach {
            if(markerMap.containsKey(it.key)) {

            }else {
                val currentMarker = googleMap.addMarker(MarkerOptions()
                        .position(LatLng(it.value.lat, it.value.lng))
                        .title(it.value.title)
                        .snippet(it.value.description)
                        .icon(BitmapDescriptorFactory.fromResource(CategoryData.gameIconIds[it.value.gameId])))
                currentMarker?.tag = it.value
                markerMap.put(it.key, currentMarker)
            }
        }
    }

    private fun checkIntentExtra() {
        intent.extras?.let {
            if(it.containsKey(AppConst.EXTRA_CHAT_ID)) {
                val chatId = it.getString(AppConst.EXTRA_CHAT_ID)
                viewModel.bottomTab.value = Chat
                FirebaseDatabase.getInstance().reference.child(KEY_USERS).child(MyChatLiveData.currentUserId)
                        .child(KEY_CHAT).child(chatId).child(KEY_DT_ENTERED)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {}
                            override fun onDataChange(p0: DataSnapshot?) {
                                val dtEntered = p0?.value as Long
                                val intent = Intent(this@MainActivity, ChatingActivity::class.java)
                                intent.putExtra(AppConst.EXTRA_CHAT_ID, chatId)
                                intent.putExtra(AppConst.EXTRA_DT_ENTERED, dtEntered)
                                startActivity(intent)
                            }
                        })
            }
        }
    }

    private fun updateUserUI(user: User?) {
        if(user != null) {
            Glide.with(this)
                    .load(if(!TextUtils.isEmpty(user.photoUrl)) user.photoUrl else R.drawable.default_profile)
                    .bitmapTransform(CropCircleTransformation(this))
                    .into(profileTab)
        }
    }

    private fun moveTab(tab: BottomTab?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,
                when (tab) {
                    Instant -> FindFragment()
                    Chat -> ChatListFragment()
                    Clan -> TeamInfoFragment()
                    Profile -> TeamInfoFragment()
                    else -> return
                })
        fragmentTransaction.commit()
    }

    val permissionlistener = object : PermissionListener {
        override fun onPermissionGranted() {
            if(viewModel.location.value == null) {
                CurrentLocation.setCurrentLocation(this@MainActivity) { viewModel.location.value = it.latLng }
            }
        }
        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {}
    }

    fun checkLocationPermission() {
        TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()
    }

}
