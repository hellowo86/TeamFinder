package com.hellowo.teamfinder.ui.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Chat
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.ui.adapter.ChatMemberListAdapter
import com.hellowo.teamfinder.ui.adapter.MessageListAdapter
import com.hellowo.teamfinder.ui.adapter.MessageListAdapter.AdapterInterface
import com.hellowo.teamfinder.utils.startFadeInAnimation
import com.hellowo.teamfinder.utils.startFadeOutAnimation
import com.hellowo.teamfinder.viewmodel.ChatingViewModel
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_chating.*
import java.text.DateFormat
import java.util.*

class ChatingActivity : LifecycleActivity() {
    lateinit var viewModel: ChatingViewModel
    lateinit var adapter: MessageListAdapter
    lateinit var memberAdapter: ChatMemberListAdapter
    val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
    var floatingDateViewFlag = false
    internal var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chating)
        viewModel = ViewModelProviders.of(this).get(ChatingViewModel::class.java)
        viewModel.initChat(intent.getStringExtra(AppConst.EXTRA_CHAT_ID), intent.getLongExtra(AppConst.EXTRA_DT_ENTERED, 0))
        initLayout()
        initListViews()
        initObserve()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loginChat()
    }

    override fun onStop() {
        super.onStop()
        viewModel.logoutChat()
    }

    fun initLayout() {
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
        outBtn.setOnClickListener{ showOutChatAlert() }
        sendImageBtn.setOnClickListener { checkExternalStoragePermission() }
    }

    fun initListViews() {
        adapter = MessageListAdapter(this, viewModel.currentChat, viewModel.memberMap,
                viewModel.messageList, viewModel.typingList, object : AdapterInterface{
            override fun onProfileClicked(userId: String) { startUserActivity(userId) }
            override fun onMessageClicked(message: Message) {}
            override fun onPhotoClicked(photoUrl: String) { startPhotoActivity(photoUrl) }
        })
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1) {
                    viewModel.loadMoreMessages()
                }
                setFloatingDateViewText()
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                setFloatingDateView(newState)
            }
        })

        memberAdapter = ChatMemberListAdapter(this, viewModel.memberMap) {
            chatMember ->
        }
        memberRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        memberRecyclerView.adapter = memberAdapter
    }

    fun initObserve() {
        viewModel.isReady.observe(this, Observer { progressBar.visibility = if(it as Boolean) View.GONE else View.VISIBLE })
        viewModel.chat.observe(this, Observer { it?.let { updateChatUI(it) } })
        viewModel.messages.observe(this, Observer { adapter.notifyDataSetChanged() })
        viewModel.newMessage.observe(this, Observer {
            adapter.notifyItemInserted(1)
            recyclerView.scrollToPosition(0)
        })
        viewModel.members.observe(this, Observer {
            adapter.notifyDataSetChanged()
            memberAdapter.notifyDataSetChanged()
        })
        viewModel.typings.observe(this, Observer { adapter.refreshTypingList() })
        viewModel.outOfChat.observe(this, Observer {
            if(it as Boolean) finish()
        })
        viewModel.isUploading.observe(this, Observer { if(it as Boolean) showProgressDialog() else hideProgressDialog() })
    }

    private fun enterMessage() {
        if(!messageInput.text.toString().isNullOrBlank()) {
            viewModel.postMessage(messageInput.text.toString(), 0)
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

    private fun setFloatingDateView(newState: Int) {
        if(newState == 0/*scroll stop*/) {
            floatingDateViewFlag = true
            floatingDateViewHandler.removeMessages(0)
            floatingDateViewHandler.sendEmptyMessageDelayed(0, 1000)
        }else {
            floatingDateViewFlag = false
            if(floatingDateView.visibility == View.GONE) {
                floatingDateView.visibility = View.VISIBLE
                startFadeInAnimation(floatingDateView)
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

    private fun showOutChatAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.out_of_chat)
        builder.setCancelable(true)
        builder.setMessage(R.string.out_of_chat_sub)
        builder.setPositiveButton(R.string.ok) { _,_ -> viewModel.outOfChat() }
        builder.setNegativeButton(R.string.cancel, null)
        builder.show()
    }

    private val permissionlistener = object : PermissionListener {
        override fun onPermissionGranted() { showPhotoPicker() }
        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {}
    }

    fun checkExternalStoragePermission() {
        TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
    }

    private fun showPhotoPicker() {
        val bottomSheetDialogFragment = TedBottomPicker.Builder(this)
                .setOnImageSelectedListener { uri -> viewModel.sendPhotoMessage(uri) }
                .setMaxCount(100)
                .create()
        bottomSheetDialogFragment.show(supportFragmentManager)
    }

    private fun showProgressDialog() {
        hideProgressDialog()
        progressDialog = ProgressDialog(this)
        progressDialog?.let {
            it.setTitle(getString(R.string.plz_wait))
            it.setMessage(getString(R.string.uploading_image))
            it.setCancelable(false)
            it.show()
        }
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun startUserActivity(userId: String) {}

    private fun startPhotoActivity(photoUrl: String) {
        val intent = Intent(this, PhotoActivity::class.java)
        intent.putExtra(AppConst.EXTRA_PHOTO_URL, photoUrl)
        startActivity(intent)
    }
}

