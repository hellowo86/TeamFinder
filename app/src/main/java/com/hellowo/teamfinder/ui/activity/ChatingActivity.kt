package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.ui.adapter.BasicListAdapter
import com.hellowo.teamfinder.ui.adapter.MessageListAdapter
import com.hellowo.teamfinder.ui.adapter.MessageListAdapter.AdapterInterface
import com.hellowo.teamfinder.viewmodel.ChatingViewModel
import kotlinx.android.synthetic.main.activity_chating.*
import java.text.DateFormat
import java.util.*

class ChatingActivity : LifecycleActivity() {
    lateinit var viewModel: ChatingViewModel
    val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
    lateinit var adapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)
        viewModel = ViewModelProviders.of(this).get(ChatingViewModel::class.java)
        viewModel.initChat(intent.getStringExtra(AppConst.EXTRA_CHAT_ID))
        initLayout()
        initObserve()
    }

    fun initLayout() {
        adapter = MessageListAdapter(this, viewModel.messageList, object : AdapterInterface{
            override fun onProfileClicked(userId: String) {

            }
            override fun onMessageClicked(message: Message) {}
        })
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1) {
                    viewModel.loadMoreMessages()
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == 0) {
                    floatingDateView.visibility = View.GONE
                }else {
                    floatingDateText.text = DateFormat.getDateInstance().format(
                            Date(viewModel.getlastPostionDate(layoutManager.findLastVisibleItemPosition())))
                    floatingDateView.visibility = View.VISIBLE
                }
            }
        })

        sendBtn.setOnClickListener { enterMessage() }
        backBtn.setOnClickListener{ finish() }
    }

    fun initObserve() {
        viewModel.loading.observe(this, Observer {
            progressBar.visibility = if(it as Boolean) View.VISIBLE else View.GONE
        })
        viewModel.chat.observe(this, Observer {
            it?.let {
                titleText.text = it.title
            }
        })
        viewModel.messages.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })
        viewModel.newMessage.observe(this, Observer {
            adapter.notifyItemInserted(viewModel.messageList.indexOf(it))
            recyclerView.scrollToPosition(viewModel.messageList.indexOf(it))
        })
    }

    private fun enterMessage() {
        if(!messageInput.text.toString().isNullOrBlank()) {
            viewModel.postMessage(messageInput.text.toString())
            messageInput.setText("")
        }
    }
}

