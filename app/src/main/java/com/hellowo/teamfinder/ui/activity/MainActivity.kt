package com.hellowo.teamfinder.ui.activity

import android.support.v7.app.AppCompatActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.data.MyChatLiveData
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.ui.fragment.*
import com.hellowo.teamfinder.utils.KEY_CHAT
import com.hellowo.teamfinder.utils.KEY_DT_ENTERED
import com.hellowo.teamfinder.utils.KEY_USERS
import com.hellowo.teamfinder.utils.makePublicPhotoUrl
import com.hellowo.teamfinder.viewmodel.MainViewModel
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    private var clickedTab: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initLayout()
        initObserve()
        checkIntentExtra()
    }

    private fun initLayout() {
        communityTab.setOnLongClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
            return@setOnLongClickListener false
        }
        homeTab.setOnClickListener{ clickTab(homeTabImg) }
        matchingTab.setOnClickListener{ clickTab(matchingTabImg) }
        chatTab.setOnClickListener{ clickTab(chatTabImg) }
        communityTab.setOnClickListener{ clickTab(communityTabImg) }
        profileTab.setOnClickListener{ clickTab(profileImg) }
        clickTab(homeTabImg)
    }

    private fun clickTab(item: ImageView) {
        if(item != clickedTab) {
            if(clickedTab != profileImg) clickedTab?.setColorFilter(resources.getColor(R.color.disableText))
            if(item != profileImg) item.setColorFilter(resources.getColor(R.color.colorPrimary))
            clickedTab = item
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,
                when (item) {
                    homeTabImg -> ChoiceFragment()
                    matchingTabImg -> ChatListFragment()
                    chatTabImg -> ChatListFragment()
                    communityTabImg -> ChatListFragment()
                    profileImg -> ProfileFragment()
                    else -> return
                })
        fragmentTransaction.commit()
    }

    private fun initObserve() {
        MeLiveData.observe(this, Observer { updateUserUI(it) })
        viewModel.chats.observe(this, Observer {  })
    }

    private fun checkIntentExtra() {
        intent.extras?.let {
            if(it.containsKey(AppConst.EXTRA_CHAT_ID)) {
                val chatId = it.getString(AppConst.EXTRA_CHAT_ID)
                clickTab(chatTabImg)
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
        user?.let {
            Glide.with(this).load(makePublicPhotoUrl(user.id)).placeholder(R.drawable.default_profile)
                    .bitmapTransform(CropCircleTransformation(this)).into(profileImg)
        }
    }
}
