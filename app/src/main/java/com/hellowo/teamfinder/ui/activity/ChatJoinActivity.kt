package com.hellowo.teamfinder.ui.activity

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
        viewModel.chat.observe(this, Observer { it?.let { updateChatUI(it) } })
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
}
