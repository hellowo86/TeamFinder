package com.hellowo.teamfinder.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.AppConst
import com.hellowo.teamfinder.R
import kotlinx.android.synthetic.main.activity_photo.*
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.lang.Exception


class PhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        initLayout()
        initObserve()
    }

    private fun initLayout() {
        progressBar.visibility = View.VISIBLE
        backBtn.setOnClickListener{ finish() }
    }

    private fun initObserve() {
        Glide.with(this).load(intent.getStringExtra(AppConst.EXTRA_PHOTO_URL))
                .listener(object : RequestListener<String, GlideDrawable> {
                    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?,
                                             isFirstResource: Boolean): Boolean = false
                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?,
                                                 isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        photoView.setImageDrawable(resource)
                        return true
                    }}).into(photoView)
    }
}