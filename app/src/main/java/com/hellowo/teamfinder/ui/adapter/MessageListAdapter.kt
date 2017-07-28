package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Message
import kotlinx.android.synthetic.main.list_item_message.view.*

class MessageListAdapter(val context: Context,
                         val mContentsList: List<Message>,
                         val adapterInterface: AdapterInterface) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        val message = mContentsList[position]

        holder.itemView.messageText.text = message.text

        holder.itemView.setOnClickListener { adapterInterface.onMessageClicked(message) }
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = mContentsList.size

    interface AdapterInterface {
        fun onMessageClicked(message: Message)
        fun onProfileClicked(userId: String)
    }
}