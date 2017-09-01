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
import kotlinx.android.synthetic.main.fragment_event_list.*

class EventListFragment : LifecycleFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
