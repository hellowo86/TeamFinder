package com.hellowo.teamfinder.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.ui.adapter.MessageListAdapter
import com.hellowo.teamfinder.ui.adapter.MessageListAdapter.AdapterInterface
import com.hellowo.teamfinder.utils.startFadeOutAnimation
import com.hellowo.teamfinder.utils.startScaleHideAnimation
import com.hellowo.teamfinder.utils.startScaleShowAnimation
import com.hellowo.teamfinder.viewmodel.ChatingViewModel
import kotlinx.android.synthetic.main.activity_chating.*
import java.text.DateFormat
import java.util.*

class ChatingActivity : LifecycleActivity() {
    lateinit var viewModel: ChatingViewModel
    lateinit var adapter: MessageListAdapter
    val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
    var floatingDateViewFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)
        viewModel = ViewModelProviders.of(this).get(ChatingViewModel::class.java)
        viewModel.initChat(intent.getStringExtra(AppConst.EXTRA_CHAT_ID))
        initLayout()
        initObserve()
    }

    fun initLayout() {
        adapter = MessageListAdapter(this, viewModel.messageList, viewModel.typingList, object : AdapterInterface{
            override fun onProfileClicked(userId: String) { startUserActivity(userId) }
            override fun onMessageClicked(message: Message) {}
        })
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var deltaY = 0
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                deltaY += dy
                if(layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1) {
                    viewModel.loadMoreMessages()
                }
                setFloatingDateViewText()
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                setFloatingDateView(newState, deltaY)
            }
        })

        messageInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                viewModel.typingText(text)
            }
        })

        menuBtn.setOnClickListener{ drawerLy.openDrawer(chatMenuLy) }
        sendBtn.setOnClickListener { enterMessage() }
        backBtn.setOnClickListener{ finish() }
    }

    fun initObserve() {
        viewModel.loading.observe(this, Observer { progressBar.visibility = if(it as Boolean) View.VISIBLE else View.GONE })
        viewModel.chat.observe(this, Observer { it?.let { updateChatUI(it) } })
        viewModel.messages.observe(this, Observer { adapter.notifyDataSetChanged() })
        viewModel.newMessage.observe(this, Observer {
            adapter.notifyItemInserted(1)
            recyclerView.scrollToPosition(0)
        })
        viewModel.typings.observe(this, Observer { adapter.refreshTypingList() })
    }

    private fun enterMessage() {
        if(!messageInput.text.toString().isNullOrBlank()) {
            viewModel.postMessage(messageInput.text.toString())
            messageInput.setText("")
        }
    }

    private fun updateChatUI(chat: Chat) {
        titleText.text = chat.title
    }

    val floatingDateViewHandler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: android.os.Message?) {
            super.handleMessage(msg)
            if(floatingDateViewFlag) {
                startFadeOutAnimation(floatingDateView, object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        floatingDateView.visibility = View.GONE
                    }
                })
            }
        }
    }

    private fun setFloatingDateView(newState: Int, deltaY: Int) {
        if(newState == 0/*scroll stop*/) {
            floatingDateViewFlag = true
            floatingDateViewHandler.removeMessages(0)
            floatingDateViewHandler.sendEmptyMessageDelayed(0, 1000)
        }else {
            floatingDateViewFlag = false
            if(Math.abs(deltaY) > 10 && floatingDateView.visibility == View.GONE) {
                floatingDateView.visibility = View.VISIBLE
                startScaleShowAnimation(floatingDateView)
            }
        }
    }

    private fun setFloatingDateViewText() {
        val itemPos = layoutManager.findLastVisibleItemPosition()
        if(itemPos > 0) {
            floatingDateText.text = DateFormat.getDateInstance(DateFormat.FULL).format(
                    Date(viewModel.getlastPostionDate(itemPos - 1)))
        }
    }

    private fun startUserActivity(userId: String) {}
}

