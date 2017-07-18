package com.hellowo.teamfinder.ui.fragment

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.TeamsLiveData
import com.hellowo.teamfinder.ui.adapter.TeamListAdapter
import kotlinx.android.synthetic.main.fragment_single_list.*

class TeamListFragment : LifecycleFragment() {
    lateinit var adapter: TeamListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_single_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLy.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorAccent))
        swipeRefreshLy.setOnRefreshListener({TeamsLiveData.get().loadTeams()})

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        adapter = TeamListAdapter(activity, {it})
        recyclerView.setAdapter(adapter)

        TeamsLiveData.get().observe(this, Observer {
            swipeRefreshLy.setRefreshing(false)
            adapter.notifyDataSetChanged()
        })
    }
}
