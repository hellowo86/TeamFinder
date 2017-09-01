package com.hellowo.teamfinder.ui.fragment

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.MeLiveData
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.viewmodel.ProfileViewModel
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : LifecycleFragment() {
    lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MeLiveData.observe(this, Observer { updateUserUI(it) })
    }

    private fun updateUserUI(user: User?) {
        user?.let {
            Glide.with(this).load(it.photoUrl).placeholder(R.drawable.default_profile)
                    .bitmapTransform(CropCircleTransformation(context)).into(profileImage)
            nameText.text = it.nickName
            descriptionText.text = it.email
        }
    }
}
