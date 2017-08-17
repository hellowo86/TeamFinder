package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.ui.adapter.ChatListAdapter
import com.hellowo.teamfinder.viewmodel.ChatFindViewModel
import kotlinx.android.synthetic.main.activity_find_chat.*

class ChatFindActivity : LifecycleActivity() {
    lateinit var viewModel: ChatFindViewModel
    lateinit var adapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_chat)
        viewModel = ViewModelProviders.of(this).get(ChatFindViewModel::class.java)
        initLayout()
        initObserve()
    }

    private fun initLayout() {
        createBtn.setOnClickListener { startActivity(Intent(this, ChatCreateActivity::class.java)) }
        backBtn.setOnClickListener{ finish() }
        searchInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                hintLy.visibility = if(text.isNotEmpty()) View.GONE else View.VISIBLE
            }
        })
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                swipeRefreshLy.isRefreshing = true
                viewModel.search(searchInput.text.toString().trim())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        adapter = ChatListAdapter(this, viewModel.chatList) {
            val intent = Intent(this, ChatJoinActivity::class.java)
            intent.putExtra(AppConst.EXTRA_CHAT_ID, it.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        swipeRefreshLy.setOnRefreshListener{
            viewModel.search(searchInput.text.toString().trim())
        }
    }

    private fun initObserve() {
        viewModel.chats.observe(this, Observer {
            swipeRefreshLy.isRefreshing = false
            adapter.notifyDataSetChanged() })
    }
}
