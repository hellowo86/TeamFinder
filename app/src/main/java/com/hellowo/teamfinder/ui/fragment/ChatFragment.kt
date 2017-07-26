package com.hellowo.teamfinder.ui.fragment

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.ChatsLiveData
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.ui.activity.ChatCreateActivity
import com.hellowo.teamfinder.ui.adapter.ChatListAdapter
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : LifecycleFragment() {
    lateinit var adapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener { startActivity(Intent(activity, ChatCreateActivity::class.java)) }

        adapter = ChatListAdapter(activity, ChatsLiveData.value!!, { chat ->  Log.d("aaa", chat.toString()) })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        ChatsLiveData.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })
    }
}
