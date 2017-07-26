package com.hellowo.teamfinder.ui.fragment

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.TeamsLiveData
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.ui.activity.TeamDetailActivity
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
                resources.getColor(R.color.colorPrimary),
                resources.getColor(R.color.colorPrimaryDark),
                resources.getColor(R.color.colorAccent))
        swipeRefreshLy.setOnRefreshListener({ TeamsLiveData.loadTeams() })

        adapter = TeamListAdapter(activity, { clickTeam(it) })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        TeamsLiveData.observe(this, Observer {
            swipeRefreshLy.isRefreshing = false
            adapter.notifyDataSetChanged()
        })
    }

    fun clickTeam(team: Team) {
        val intent = Intent(activity, TeamDetailActivity::class.java)
        intent.putExtra(AppConst.EXTRA_TEAM_ID, team.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
    }
}
