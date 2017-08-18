package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v4.util.ArrayMap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.ChatMember
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.list_item_chat_member.view.*

class ChatMemberListAdapter(val context: Context,
                        val mContentsList: ArrayMap<String, ChatMember>,
                        val adapterInterface: (chatMember: ChatMember) -> Unit) : RecyclerView.Adapter<ChatMemberListAdapter.ViewHolder>() {

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_member, parent, false))

    override fun onBindViewHolder(holder: ChatMemberListAdapter.ViewHolder, position: Int) {
        val chatMember = mContentsList.valueAt(position)
        val v = holder.itemView

        v.nameText.text = chatMember.name

        Glide.with(context)
                .load(chatMember.photoUrl)
                .bitmapTransform(CropCircleTransformation(context))
                .placeholder(R.drawable.default_profile)
                .into(v.profileImage)

        v.setOnClickListener { adapterInterface.invoke(chatMember) }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = mContentsList.size
}