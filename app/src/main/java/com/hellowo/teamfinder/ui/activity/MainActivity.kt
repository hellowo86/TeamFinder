package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.AppConst.EXTRA_USER_ID
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MyChatLiveData
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.ui.fragment.ChatListFragment
import com.hellowo.teamfinder.ui.fragment.FindFragment
import com.hellowo.teamfinder.ui.fragment.TeamInfoFragment
import com.hellowo.teamfinder.utils.KEY_CHAT
import com.hellowo.teamfinder.utils.KEY_DT_ENTERED
import com.hellowo.teamfinder.utils.KEY_LAST_CHECK_INDEX
import com.hellowo.teamfinder.utils.KEY_USERS
import com.hellowo.teamfinder.viewmodel.MainViewModel
import com.hellowo.teamfinder.viewmodel.MainViewModel.BottomTab
import com.hellowo.teamfinder.viewmodel.MainViewModel.BottomTab.*
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : LifecycleActivity() {
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initLayout()
        initObserve()
        checkIntentExtra()
    }

    private fun initLayout() {
        profileTab.setOnLongClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            FirebaseAuth.getInstance().signOut()
            return@setOnLongClickListener false
        }
        instantTab.setOnClickListener{viewModel.bottomTab.value = Instant}
        chatTab.setOnClickListener{viewModel.bottomTab.value = Chat}
        clanTab.setOnClickListener{viewModel.bottomTab.value = Clan}
        profileTab.setOnClickListener{viewModel.bottomTab.value = Profile}
    }

    private fun initObserve() {
        MeLiveData.observe(this, Observer { updateUserUI(it) })
        viewModel.bottomTab.observe(this, Observer { moveTab(it) })
    }

    private fun checkIntentExtra() {
        intent.extras?.let {
            if(it.containsKey(AppConst.EXTRA_CHAT_ID)) {
                val chatId = it.getString(AppConst.EXTRA_CHAT_ID)
                viewModel.bottomTab.value = Chat
                FirebaseDatabase.getInstance().reference.child(KEY_USERS).child(MyChatLiveData.currentUserId)
                        .child(KEY_CHAT).child(chatId).child(KEY_DT_ENTERED)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {}
                            override fun onDataChange(p0: DataSnapshot?) {
                                val dtEntered = p0?.value as Long
                                val intent = Intent(this@MainActivity, ChatingActivity::class.java)
                                intent.putExtra(AppConst.EXTRA_CHAT_ID, chatId)
                                intent.putExtra(AppConst.EXTRA_DT_ENTERED, dtEntered)
                                startActivity(intent)
                            }
                        })
            }
        }
    }

    private fun updateUserUI(user: User?) {
        if(user != null) {
            Glide.with(this)
                    .load(if(!TextUtils.isEmpty(user.photoUrl)) user.photoUrl else R.drawable.default_profile)
                    .bitmapTransform(CropCircleTransformation(this))
                    .into(profileTab)
        }
    }

    private fun moveTab(tab: BottomTab?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,
                when (tab) {
                    Instant -> FindFragment()
                    Chat -> ChatListFragment()
                    Clan -> TeamInfoFragment()
                    Profile -> TeamInfoFragment()
                    else -> return
                })
        fragmentTransaction.commit()
    }
}
