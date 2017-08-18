package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Chat
import android.support.v4.util.ArrayMap
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.utils.makeMessageLastTimeText
import com.hellowo.teamfinder.utils.makePublicPhotoUrl
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.list_item_chat.view.*

class MyChatListAdapter(val context: Context,
                        val mContentsList: ArrayMap<String, Chat>,
                        val adapterInterface: (chat: Chat) -> Unit) : RecyclerView.Adapter<MyChatListAdapter.ViewHolder>() {
    private val hashTagColor: Int = context.resources.getColor(R.color.primaryText)

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent, false))

    override fun onBindViewHolder(holder: MyChatListAdapter.ViewHolder, position: Int) {
        val chat = mContentsList.valueAt(position)
        val v = holder.itemView

        v.titleText.text = chat.title
        v.lastMessageText.text = chat.lastMessage ?: ""
        v.lastTimeText.text = if(chat.lastMessageTime > 0) makeMessageLastTimeText(chat.lastMessageTime) else ""

        Glide.with(context)
                .load(makePublicPhotoUrl(chat.king))
                .bitmapTransform(CropCircleTransformation(context))
                .thumbnail(0.1f)
                .placeholder(R.drawable.default_profile)
                .into(v.chatImage)

        v.setOnClickListener { adapterInterface.invoke(chat) }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = mContentsList.size
}