package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.hellowo.teamfinder.AppConst.EXTRA_USER_ID
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MyChatLiveData
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.ui.fragment.ChatListFragment
import com.hellowo.teamfinder.ui.fragment.FindFragment
import com.hellowo.teamfinder.ui.fragment.TeamInfoFragment
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

    private fun startUserAcivity(view: View) {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra(EXTRA_USER_ID, MeLiveData.value?.id)
        startActivity(intent)
    }

    private fun initObserve() {
        MeLiveData.observe(this, Observer { updateUserUI(it) })
        viewModel.bottomTab.observe(this, Observer { moveTab(it) })
    }

    private fun updateUserUI(user: User?) {
        if(user != null) {
            Glide.with(this)
                    .load(if(!TextUtils.isEmpty(user.photoUrl)) user.photoUrl else R.drawable.default_profile)
                    .bitmapTransform(CropCircleTransformation(this))
                    .thumbnail(0.1f)
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
