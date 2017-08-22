package com.hellowo.teamfinder.ui.fragment

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MyChatLiveData
import com.hellowo.teamfinder.ui.activity.ChatFindActivity
import com.hellowo.teamfinder.ui.activity.ChatingActivity
import com.hellowo.teamfinder.ui.adapter.MyChatListAdapter
import com.hellowo.teamfinder.viewmodel.ChatListViewModel
import kotlinx.android.synthetic.main.fragment_chat_list.*

class ChatListFragment : LifecycleFragment() {
    lateinit var viewModel: ChatListViewModel
    lateinit var adapterMy: MyChatListAdapter

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

        adapterMy = MyChatListAdapter(activity, MyChatLiveData.value!!) {
            val intent = Intent(activity, ChatingActivity::class.java)
            intent.putExtra(AppConst.EXTRA_CHAT_ID, it.id)
            intent.putExtra(AppConst.EXTRA_DT_ENTERED, it.dtEntered)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapterMy

        MyChatLiveData.observe(this, Observer { adapterMy.notifyDataSetChanged() })
        MyChatLiveData.loading.observe(this, Observer { progressBar.visibility = if(it as Boolean) View.VISIBLE else View.GONE })
    }
}
