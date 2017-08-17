package com.hellowo.teamfinder.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.utils.makePublicPhotoUrl
import com.hellowo.teamfinder.viewmodel.ChatJoinViewModel
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_join_chat.*

class ChatJoinActivity : LifecycleActivity() {
    lateinit var viewModel: ChatJoinViewModel
    internal var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_chat)
        viewModel = ViewModelProviders.of(this).get(ChatJoinViewModel::class.java)
        viewModel.initChat(intent.getStringExtra(AppConst.EXTRA_CHAT_ID))
        initLayout()
        initObserve()
    }

    private fun initLayout() {
        backBtn.setOnClickListener{ finish() }
        joinBtn.setOnClickListener { if(!(viewModel.loading.value as Boolean)) {
            viewModel.joinChat()
        }}
    }

    private fun initObserve() {
        viewModel.loading.observe(this, Observer { progressBar.visibility = if(it as Boolean) View.VISIBLE else View.GONE })
        viewModel.isJoining.observe(this, Observer { if(it as Boolean) showProgressDialog() else hideProgressDialog() })
        viewModel.chat.observe(this, Observer { it?.let { updateChatUI(it) } })
        viewModel.joined.observe(this, Observer {
            if(it as Boolean) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }

    private fun  updateChatUI(chat: Chat) {
        titleText.text = chat.title
        contentsText.text = chat.description

        Glide.with(this)
                .load(makePublicPhotoUrl(chat.members[0]))
                .bitmapTransform(CropCircleTransformation(this))
                .thumbnail(0.1f)
                .placeholder(R.drawable.default_profile)
                .into(chatImage)
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
