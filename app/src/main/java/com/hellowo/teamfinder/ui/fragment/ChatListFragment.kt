package com.hellowo.teamfinder.ui.fragment

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.ChatsLiveData
import com.hellowo.teamfinder.ui.activity.ChatCreateActivity
import com.hellowo.teamfinder.ui.activity.ChatFindActivity
import com.hellowo.teamfinder.ui.activity.ChatingActivity
import com.hellowo.teamfinder.ui.adapter.ChatListAdapter
import com.hellowo.teamfinder.viewmodel.ChatListViewModel
import com.hellowo.teamfinder.viewmodel.FindViewModel
import kotlinx.android.synthetic.main.fragment_chat_list.*

class ChatListFragment : LifecycleFragment() {
    lateinit var viewModel: ChatListViewModel
    lateinit var adapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity).get(ChatListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener { startActivity(Intent(activity, ChatFindActivity::class.java)) }

        adapter = ChatListAdapter(activity, ChatsLiveData.value!!) {
            val intent = Intent(activity, ChatingActivity::class.java)
            intent.putExtra(AppConst.EXTRA_CHAT_ID, it.id)
            intent.putExtra(AppConst.EXTRA_GAME_ID, it.gameId)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        ChatsLiveData.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })
    }
}
