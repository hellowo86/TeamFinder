package com.hellowo.teamfinder.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.CategoryData
import com.hellowo.teamfinder.ui.dialog.SelectGameDialog
import com.hellowo.teamfinder.ui.dialog.SelectTagDialog
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress.*
import com.volokh.danylo.hashtaghelper.HashTagHelper
import kotlinx.android.synthetic.main.activity_chat_create.*
import android.content.Intent
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.hellowo.teamfinder.data.CurrentLocation
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions




class ChatCreateActivity : LifecycleActivity(), OnMapReadyCallback {
    val PLACE_PICKER_REQUEST = 1
    lateinit var viewModel: ChatCreateViewModel
    lateinit var tagHelper: HashTagHelper
    private var progressDialog: ProgressDialog? = null
    lateinit var googleMap: GoogleMap
    var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_create)
        viewModel = ViewModelProviders.of(this).get(ChatCreateViewModel::class.java)
        initLayout()
        initObserve()
    }

    fun initLayout() {
        setMap()
        setBackground()

        nextBtn.setOnClickListener { goNext() }
        confirmBtn.setOnClickListener { viewModel.confirm(tagHelper.allHashTags) }
        backBtn.setOnClickListener{ onBackPressed() }
        addTagBtn.setOnClickListener { showSelectTagDialog() }

        titleInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.setTitle(s)
            }
        })
        titleInput.setOnEditorActionListener { _, actionId, _ -> when(actionId){
            EditorInfo.IME_ACTION_DONE -> goNext()
            else -> false
        } }

        tagHelper = HashTagHelper.Creator.create(resources.getColor(R.color.colorPrimary), null)
        tagHelper.handle(contentsInput)
        contentsInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.setContents(s)
            }
        })
    }

    private fun setMap() {
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
        selectLocationBtn.setOnClickListener {
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        map?.let{
            googleMap = it
            googleMap.uiSettings.isMapToolbarEnabled = false
            googleMap.setOnMapClickListener {  }
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(16f))
            googleMap.setOnMarkerClickListener { marker -> showSelectGameDialog() }
            CurrentLocation.setCurrentLocation(this) { setMarker(it) }
        }
    }

    private fun setMarker(place: Place) {
        viewModel.chat.lat = place.latLng.latitude
        viewModel.chat.lng = place.latLng.longitude
        viewModel.chat.location = place.name.toString()
        currentMarker?.remove()
        currentMarker = googleMap.addMarker(MarkerOptions()
                .position(place.latLng)
                .title(place.name.toString())
                .icon(BitmapDescriptorFactory.fromResource(CategoryData.icons[viewModel.chat.gameId])))
        currentMarker?.tag = viewModel.chat
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
    }

    private fun setBackground() {
        /*
        Glide.with(this)
                .load(CategoryData.CATEGORIES[0].backgroundId)
                .bitmapTransform(BlurTransformation(this, 20))
                .into(backgroundImg)
                */
    }

    fun initObserve() {
        viewModel.currentProgress.observe(this, Observer { updateProgressUI(it) })
        viewModel.checkCanGoNextProgress.observe(this, Observer { updateConfirmBtn(it) })
        viewModel.loading.observe(this, Observer { if(it as Boolean) showProgressDialog() else hideProgressDialog() })
        viewModel.confirmed.observe(this, Observer { if(it as Boolean) finish() })
        viewModel.gameId.observe(this, Observer { updateGameUI(it) })
    }

    private fun updateGameUI(gameId: Int?) {
        CategoryData.CATEGORIES[gameId as Int].let {
            viewModel.chat.gameId = gameId
            currentMarker?.setIcon(BitmapDescriptorFactory.fromResource(CategoryData.icons[gameId]))
        }
    }

    private fun updateConfirmBtn(isCanGoNextProgress: Boolean?) {
        if(isCanGoNextProgress as Boolean) {
            nextBtn.setTextColor(getColor(R.color.primaryWhiteText))
            nextBtn.setBackgroundResource(R.drawable.ripple_button)
        }else {
            nextBtn.setTextColor(getColor(R.color.disableWhiteText))
            nextBtn.setBackgroundResource(R.color.transparent)
        }
    }

    private fun updateProgressUI(progress: CurrentProgress?) {
        viewFlipper.displayedChild = progress?.ordinal ?: 0
        if(viewModel.currentProgress.value == Finish) {
            nextBtn.setText(R.string.confirm)
        }else {
            nextBtn.setText(R.string.next)
        }
    }

    private fun goNext(): Boolean {
        if(viewModel.checkCanGoNextProgress.value == true) {
            viewFlipper.inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
            viewFlipper.outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
            when(viewModel.currentProgress.value) {
                Finish -> viewModel.confirm(tagHelper.allHashTags)
                Game -> {
                    viewModel.goNextProgress()
                    titleInput.postDelayed({
                        titleInput.requestFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(titleInput, InputMethodManager.SHOW_IMPLICIT) }, 300)
                }
                Contents -> {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(contentsInput.windowToken, 0)
                    viewModel.goNextProgress()
                }
                else -> viewModel.goNextProgress()
            }
        }
        return true
    }

    private fun showSelectGameDialog(): Boolean {
        val selectGameDialog = SelectGameDialog()
        selectGameDialog.setDialogInterface {
            viewModel.selectGame(it)
            selectGameDialog.dismiss()
        }
        selectGameDialog.show(supportFragmentManager, selectGameDialog.tag)
        return true
    }

    private fun showSelectTagDialog() {
        val selectTagDialog = SelectTagDialog()
        selectTagDialog.setDialogInterface{ hashTag ->
            val tag = hashTag.makeTag()
            val startPos = contentsInput.selectionStart
            contentsInput.text.insert(if (startPos < 0) 0 else startPos, tag)
        }
        selectTagDialog.show(supportFragmentManager, selectTagDialog.tag)
    }

    override fun onBackPressed() {
        viewFlipper.inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        viewFlipper.outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
        when(viewModel.currentProgress.value) {
            Game -> finish()
            Title -> {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(titleInput.windowToken, 0)
                viewModel.goPreviousProgress()
            }
            else -> viewModel.goPreviousProgress()
        }
    }

    private fun showProgressDialog() {
        hideProgressDialog()
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage(getString(R.string.plz_wait))
        progressDialog?.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            setMarker(PlacePicker.getPlace(this, data))
        }
    }
}
