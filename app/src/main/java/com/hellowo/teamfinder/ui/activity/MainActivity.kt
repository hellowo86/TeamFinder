package com.hellowo.teamfinder.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.CurrentLocation
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.data.MyChatLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.ui.fragment.ChatListFragment
import com.hellowo.teamfinder.ui.fragment.FindFragment
import com.hellowo.teamfinder.ui.fragment.TeamInfoFragment
import com.hellowo.teamfinder.utils.KEY_CHAT
import com.hellowo.teamfinder.utils.KEY_DT_ENTERED
import com.hellowo.teamfinder.utils.KEY_USERS
import com.hellowo.teamfinder.viewmodel.MainViewModel
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_find.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.MutableMap
import kotlin.collections.forEach


class MainActivity : LifecycleActivity(), OnMapReadyCallback {
    lateinit var viewModel: MainViewModel
    lateinit var googleMap: GoogleMap
    val markerMap: MutableMap<String, Chat> = HashMap()
    lateinit var mClusterManager: ClusterManager<com.hellowo.teamfinder.model.Chat>

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
        instantTab.setOnClickListener{ clickTab(it) }
        chatTab.setOnClickListener{ clickTab(it) }
        clanTab.setOnClickListener{ clickTab(it) }
        profileTab.setOnClickListener{ clickTab(it) }
        clickTab(instantTab)
    }

    private fun clickTab(item: View?) {
        instantTab.setBackgroundColor(getColor(R.color.transparent))
        chatTab.setBackgroundColor(getColor(R.color.transparent))
        clanTab.setBackgroundColor(getColor(R.color.transparent))
        profileTab.setBackgroundColor(getColor(R.color.transparent))
        item?.setBackgroundColor(getColor(R.color.backgroundDark))

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,
                when (item) {
                    instantTab -> FindFragment()
                    chatTab -> ChatListFragment()
                    clanTab -> TeamInfoFragment()
                    profileTab -> TeamInfoFragment()
                    else -> return
                })
        fragmentTransaction.commit()
    }

    private fun initObserve() {
        MeLiveData.observe(this, Observer { updateUserUI(it) })
        viewModel.chats.observe(this, Observer { addMarkers(it) })
    }

    private fun setMap() {
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let{
            googleMap = it
            mClusterManager = ClusterManager(this, googleMap)
            mClusterManager.renderer = PersonRenderer()
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(14f))
            googleMap.setOnMarkerClickListener(mClusterManager)
            googleMap.setOnInfoWindowClickListener(mClusterManager)
            googleMap.setOnCameraIdleListener{
                getAddressFromMap()
                viewModel.search(googleMap.projection.visibleRegion)
            }
            mClusterManager.setOnClusterClickListener{ onClusterClick(it) }
            mClusterManager.setOnClusterInfoWindowClickListener{}
            mClusterManager.setOnClusterItemClickListener{ onClusterItemClick(it) }
            mClusterManager.setOnClusterItemInfoWindowClickListener{}
            checkLocationPermission()
        }
    }

    @SuppressLint("SetTextI18n")
    fun getAddressFromMap() {
        val lat = googleMap.projection.visibleRegion.latLngBounds.center.latitude
        val lng = googleMap.projection.visibleRegion.latLngBounds.center.longitude
        Thread{
            try{
                val geo = Geocoder(applicationContext, Locale.getDefault())
                geo.getFromLocation(lat, lng, 1)?.let {
                    if(it.size > 0) {
                        runOnUiThread {
                            titleText.text = "${it[0].locality} ${it[0].subLocality ?: it[0].thoroughfare}"
                        }
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }.start()
    }

    fun setLocation(it: Place) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(it.latLng))
    }

    private fun onClusterItemClick(it: Chat?): Boolean {
        Log.d("aaa", "111")
        return false
    }

    private fun onClusterClick(it: Cluster<Chat>?): Boolean {
        Log.d("aaa", "222")
        return false
    }

    private inner class PersonRenderer : DefaultClusterRenderer<Chat>(applicationContext, googleMap, mClusterManager) {
        private val mIconGenerator = IconGenerator(applicationContext)
        private val mClusterIconGenerator = IconGenerator(applicationContext)

        override fun shouldRenderAsCluster(cluster: Cluster<Chat>?): Boolean {
            return cluster?.size as Int > 1
        }
    }

    private fun addMarkers(markers: ArrayMap<String, com.hellowo.teamfinder.model.Chat>?) {
        markers?.forEach {
            if(markerMap.containsKey(it.key)) {

            }else {
                markerMap.put(it.key, it.value)
                mClusterManager.addItem(it.value)
            }
        }
        mClusterManager.cluster()
    }

    private fun checkIntentExtra() {
        intent.extras?.let {
            if(it.containsKey(AppConst.EXTRA_CHAT_ID)) {
                val chatId = it.getString(AppConst.EXTRA_CHAT_ID)
                clickTab(chatTab)
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

    val permissionlistener = object : PermissionListener {
        @SuppressLint("MissingPermission")
        override fun onPermissionGranted() {
            googleMap.isMyLocationEnabled = true
            CurrentLocation.setCurrentLocation(this@MainActivity) { setLocation(it) }
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
