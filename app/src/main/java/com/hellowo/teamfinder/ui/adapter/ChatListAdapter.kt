package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Chat
import kotlinx.android.synthetic.main.list_item_chat.view.*

class ChatListAdapter(val context: Context,
                      val mContentsList: List<Chat>,
                      val adapterInterface: (chat: Chat) -> Unit) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
    private val hashTagColor: Int = context.resources.getColor(R.color.primaryText)

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ViewHolder, position: Int) {
        val chat = mContentsList[position]

        holder.itemView.titleText.text = chat.title

        holder.itemView.setOnClickListener { adapterInterface.invoke(chat) }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = mContentsList.size
}