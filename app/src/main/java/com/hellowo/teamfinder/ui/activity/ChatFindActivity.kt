package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.activity_find_chat.*

class ChatFindActivity : LifecycleActivity() {
    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_chat)
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        initLayout()
        initObserve()
    }

    private fun initLayout() {
        createBtn.setOnClickListener { startActivity(Intent(this, ChatCreateActivity::class.java)) }
        backBtn.setOnClickListener{ finish() }
    }

    private fun initObserve() {
    }
}
