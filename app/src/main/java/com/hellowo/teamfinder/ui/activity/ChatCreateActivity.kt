package com.hellowo.teamfinder.ui.activity

import android.Manifest
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
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.GameData
import com.hellowo.teamfinder.model.HashTag
import com.hellowo.teamfinder.ui.dialog.SelectGameDialog
import com.hellowo.teamfinder.ui.dialog.SelectTagDialog
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress.*
import com.volokh.danylo.hashtaghelper.HashTagHelper
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_chat_create.*
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import android.os.AsyncTask.execute
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hellowo.teamfinder.data.LocationLiveData
import com.hellowo.teamfinder.utils.getGoogleApiClient
import java.util.*


class ChatCreateActivity : LifecycleActivity(), OnMapReadyCallback {
    val PLACE_PICKER_REQUEST = 1
    lateinit var viewModel: ChatCreateViewModel
    lateinit var tagHelper: HashTagHelper
    private var progressDialog: ProgressDialog? = null
    lateinit var googleMap: GoogleMap

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

        gameSelectBtn.setOnClickListener{ showSelectGameDialog() }
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

    private fun setBackground() {
        Glide.with(this)
                .load(GameData.games[0].backgroundId)
                .bitmapTransform(BlurTransformation(this, 20))
                .into(backgroundImg)
    }

    fun initObserve() {
        viewModel.currentProgress.observe(this, Observer { updateProgressUI(it) })
        viewModel.checkCanGoNextProgress.observe(this, Observer { updateConfirmBtn(it) })
        viewModel.loading.observe(this, Observer { if(it as Boolean) showProgressDialog() else hideProgressDialog() })
        viewModel.confirmed.observe(this, Observer { if(it as Boolean) finish() })
        viewModel.gameId.observe(this, Observer { updateGameUI(it) })
    }

    private fun updateGameUI(gameId: Int?) {
        GameData.games[gameId as Int].let {
            gameTitleText.text = it.title
            gameIconImg.setImageResource(it.iconId)
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

    private fun setMap() {
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let{
            googleMap = it
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(14f))
            getGoogleApiClient(this)?.let { client -> LocationLiveData.loadCurrentLocation(client) }
            LocationLiveData.observe(this, Observer { googleMap.moveCamera(CameraUpdateFactory.newLatLng(it)) })
        }
    }

    private fun showSelectGameDialog() {
        val selectGameDialog = SelectGameDialog()
        selectGameDialog.setDialogInterface {
            viewModel.selectGame(it)
            selectGameDialog.dismiss()
        }
        selectGameDialog.show(supportFragmentManager, selectGameDialog.tag)
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
            val place = PlacePicker.getPlace(this, data)
            Toast.makeText(this, String.format("Place: %s", place.getName()), Toast.LENGTH_LONG).show();
        }
    }
}
