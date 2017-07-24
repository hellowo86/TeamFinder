package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel
import kotlinx.android.synthetic.main.activity_chat_create.*
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress
import com.hellowo.teamfinder.viewmodel.ChatCreateViewModel.CurrentProgress.*

class ChatCreateActivity : LifecycleActivity() {
    lateinit var viewModel: ChatCreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_create)
        viewModel = ViewModelProviders.of(this).get(ChatCreateViewModel::class.java)
        initLayout()
        initObserve()
    }

    fun initLayout() {
        viewFlipper.inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        viewFlipper.outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
        confirmBtn.setOnClickListener { viewModel.goNextProgress() }
        backBtn.setOnClickListener{ onBackPressed() }
        titleInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.setTitle(s)
            }
        })
        contentsInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.setContents(s)
            }
        })

        titleInput.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(titleInput, InputMethodManager.SHOW_IMPLICIT)
        }, 250)
    }

    fun initObserve() {
        viewModel.currentProgress.observe(this, Observer { updateProgressUI(it) })
        viewModel.checkCanGoNextProgress.observe(this, Observer { updateConfirmBtn(it) })
    }

    private fun updateConfirmBtn(isCanGoNextProgress: Boolean?) {
        if(isCanGoNextProgress as Boolean) {
            confirmBtn.setTextColor(getColor(R.color.colorPrimary))
            confirmBtn.setBackgroundResource(R.drawable.ripple_button)
        }else {
            confirmBtn.setTextColor(getColor(R.color.disableText))
            confirmBtn.setBackgroundResource(R.color.transparent)
        }
    }

    private fun updateProgressUI(progress: CurrentProgress?) {
        viewFlipper.displayedChild = progress?.ordinal ?: 0
        if(viewModel.currentProgress.value == Options) {
            confirmBtn.setText(R.string.confirm)
        }else {
            confirmBtn.setText(R.string.next)
        }
    }

    override fun onBackPressed() {
        when(viewModel.currentProgress.value) {
            Title -> finish()
            else -> viewModel.goPreviousProgress()
        }
    }
}
