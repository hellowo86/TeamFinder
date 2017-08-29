package com.hellowo.teamfinder.ui.fragment

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.ui.activity.ChatCreateActivity
import com.hellowo.teamfinder.ui.activity.ChatJoinActivity
import com.hellowo.teamfinder.ui.activity.MainActivity
import com.hellowo.teamfinder.ui.adapter.ChatListAdapter
import com.hellowo.teamfinder.ui.dialog.ChatFilterDialog
import com.hellowo.teamfinder.viewmodel.FindViewModel
import com.hellowo.teamfinder.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_find.*


class FindFragment : LifecycleFragment() {
    lateinit var viewModel: FindViewModel
    lateinit var mainViewModel: MainViewModel
    lateinit var adapter: ChatListAdapter
    var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    var JOIN_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity).get(FindViewModel::class.java)
        mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_find, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChatListAdapter(context, mainViewModel.chatList) {
            val intent = Intent(activity, ChatJoinActivity::class.java)
            intent.putExtra(AppConst.EXTRA_CHAT_ID, it.id)
            startActivityForResult(intent, JOIN_REQUEST_CODE)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        fab.setOnClickListener { startActivity(Intent(activity, ChatCreateActivity::class.java)) }
        filterBtn.setOnClickListener { showChatFilterDialog() }
        listBtn.setOnClickListener { BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED }
        searchBtn.setOnClickListener {
            try {
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(activity)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: GooglePlayServicesRepairableException) {
            } catch (e: GooglePlayServicesNotAvailableException) {
            }
        }

        viewModel.isOnfilter.observe(this, Observer {
            if(it as Boolean) {
                filterBtn.setColorFilter(context.resources.getColor(R.color.colorPrimary))
            }else {
                filterBtn.setColorFilter(context.resources.getColor(R.color.iconTint))
            }
        })
        mainViewModel.address.observe(this, Observer { titleText.text = it })
        mainViewModel.chats.observe(this, Observer {
            listText.text = String.format(getString(R.string.total_chat_count_is), it?.size)
            adapter.notifyDataSetChanged()
        })
        mainViewModel.loading.observe(this, Observer {
            if(it as Boolean) {
                listText.text = getString(R.string.searching)
                progressBar.visibility = View.VISIBLE
                listBtn.visibility = View.GONE
            }else {
                progressBar.visibility = View.GONE
                listBtn.visibility = View.VISIBLE
            }
        })
    }

    private fun showChatFilterDialog() {
        val chatFilterDialog = ChatFilterDialog()
        chatFilterDialog.show(activity.supportFragmentManager, chatFilterDialog.tag)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            val place = PlaceAutocomplete.getPlace(activity, data)
            (activity as MainActivity).setLocation(place)
        }
    }
}
