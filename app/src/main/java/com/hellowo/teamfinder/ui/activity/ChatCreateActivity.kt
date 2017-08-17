package com.hellowo.teamfinder.ui.activity

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
import com.hellowo.teamfinder.ui.dialog.SelectGameDialog
import com.hellowo.teamfinder.ui.dialog.SelectTagDialog
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress.*
import com.volokh.danylo.hashtaghelper.HashTagHelper
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_chat_create.*


class ChatCreateActivity : LifecycleActivity() {
    lateinit var viewModel: ChatCreateViewModel
    lateinit var tagHelper: HashTagHelper
    internal var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_create)
        viewModel = ViewModelProviders.of(this).get(ChatCreateViewModel::class.java)
        initLayout()
        initObserve()
    }

    fun initLayout() {
        Glide.with(this)
                .load(GameData.games[0].backgroundId)
                .bitmapTransform(BlurTransformation(this, 20))
                .into(backgroundImg)

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
        selectTagDialog.setDialogInterface { option ->
            val tag = option.makeTag()
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
}
