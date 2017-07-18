package com.hellowo.teamfinder.ui.activity

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.AppConst.EXTRA_TEAM_ID
import com.hellowo.teamfinder.AppConst.EXTRA_USER_ID
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.ConnectedUserLiveData
import com.hellowo.teamfinder.model.Team
import com.hellowo.teamfinder.model.User
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

    private fun initLayout() {/*
        binding.accountPhotoImg.setOnClickListener(this::startUserAcivity);

        binding.signOutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            FirebaseAuth.getInstance().signOut();
        });

        initTeamRecyclerView(); */
        findTab.setOnClickListener{viewModel.bottomTab.value = Find}
        teamTab.setOnClickListener{viewModel.bottomTab.value = Team}
        profileTab.setOnClickListener{viewModel.bottomTab.value = Profile}
    }

    private fun clickTeam(team: Team) {
        val intent = Intent(this, TeamDetailActivity::class.java)
        intent.putExtra(EXTRA_TEAM_ID, team.id)
        startActivity(intent)
    }

    private fun startUserAcivity(view: View) {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra(EXTRA_USER_ID, ConnectedUserLiveData.value?.id)
        startActivity(intent)
    }

    private fun initObserve() {
        ConnectedUserLiveData.observe(this,
                Observer { updateUserUI(it) })
        viewModel.bottomTab.observe(this,
                Observer { updateUI(it) })
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

    private fun updateUI(viewMope: BottomTab?) {
        val fragment: LifecycleFragment

        when (viewMope) {
            Find -> fragment = FindFragment()
            Team -> fragment = TeamInfoFragment()
            Profile -> fragment = TeamInfoFragment()
            else -> return
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    private fun clickFab(view: View) {
        startActivity(Intent(this, CreateTeamActivity::class.java))
    }
}
