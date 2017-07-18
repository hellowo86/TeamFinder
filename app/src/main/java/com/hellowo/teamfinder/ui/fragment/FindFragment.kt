package com.hellowo.teamfinder.ui.fragment

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.viewmodel.FindViewModel
import kotlinx.android.synthetic.main.fragment_find.*

class FindFragment : LifecycleFragment() {
    lateinit var viewModel: FindViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity).get(FindViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_find, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = TeamPagerAdapter(activity.supportFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(pos: Int) {}
            override fun onPageScrollStateChanged(i: Int) {}
        })

        teamTab.setOnClickListener({viewPager.setCurrentItem(0, true)})
        instantTab.setOnClickListener({viewPager.setCurrentItem(1, true)})
    }

    internal inner class TeamPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val NUM_ITEMS = 2

        override fun getCount(): Int {
            return NUM_ITEMS
        }

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return TeamListFragment()
                1 -> return TeamInfoFragment()
                else -> return null
            }
        }
    }
}
