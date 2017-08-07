package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.ui.adapter.BasicListAdapter
import com.hellowo.teamfinder.ui.adapter.MessageListAdapter
import com.hellowo.teamfinder.ui.adapter.MessageListAdapter.AdapterInterface
import com.hellowo.teamfinder.viewmodel.ChatingViewModel
import kotlinx.android.synthetic.main.activity_chating.*

class ChatingActivity : LifecycleActivity() {
    lateinit var viewModel: ChatingViewModel
    lateinit var adapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)
        viewModel = ViewModelProviders.of(this).get(ChatingViewModel::class.java)
        initLayout()
        initObserve()
    }

    fun initLayout() {
        adapter = MessageListAdapter(this, viewModel.messageList, object : AdapterInterface{
            override fun onProfileClicked(userId: String) {
            }
            override fun onMessageClicked(message: Message) {
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        recyclerView.adapter = adapter

        sendBtn.setOnClickListener { enterMessage() }
    }

    fun initObserve() {

    }

    private fun enterMessage() {
        viewModel.postMessage(messageInput.getText().toString())
        messageInput.setText("")
    }
}
