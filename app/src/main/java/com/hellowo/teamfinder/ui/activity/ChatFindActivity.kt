package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.viewmodel.ChatFindViewModel
import kotlinx.android.synthetic.main.activity_find_chat.*

class ChatFindActivity : LifecycleActivity() {
    lateinit var viewModel: ChatFindViewModel

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
        searchInput.setOnEditorActionListener { _, actionId, _ -> when(actionId){
            EditorInfo.IME_ACTION_SEARCH -> viewModel.search(searchInput.text.toString())
            else -> false
        } }
    }

    private fun initObserve() {
    }
}
