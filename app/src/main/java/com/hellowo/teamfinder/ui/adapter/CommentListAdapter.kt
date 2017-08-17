package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Comment
import com.hellowo.teamfinder.model.User
import com.hellowo.teamfinder.utils.*
import kotlinx.android.synthetic.main.list_item_comment.view.*

import jp.wasabeef.glide.transformations.CropCircleTransformation


class CommentListAdapter(private val context: Context, private val mContentsList: List<Comment>,
                         private val adapterInterface: AdapterInterface) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {

    inner class ViewHolder(var container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = mContentsList[position]
        val v = holder.itemView

        val ss = SpannableString(comment.userName + " : " + comment.text)
        ss.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.primaryText)),
                0, comment.userName!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(StyleSpan(android.graphics.Typeface.BOLD),
                0, comment.userName!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        v.meesageText.text = ss
        v.activeTimeText.text = makeActiveTimeText(comment.dtCreated)

        Glide.with(context)
                .load(makePublicPhotoUrl(comment.userId))
                .bitmapTransform(CropCircleTransformation(context))
                .thumbnail(0.1f)
                .placeholder(R.drawable.default_profile)
                .into(v.profileImage)

        v.profileImage.setOnClickListener { comment.userId?.let { id -> adapterInterface.onUserClicked(id) } }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mContentsList.size
    }

    interface AdapterInterface {
        fun onUserClicked(userId: String)
    }
}