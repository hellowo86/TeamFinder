package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.utils.makePublicPhotoUrl
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.list_item_message.view.*
import kotlinx.android.synthetic.main.list_item_message_typing.view.*
import java.text.DateFormat
import java.util.*


class MessageListAdapter(val context: Context,
                         val messageList: List<Message>,
                         val typingList: List<String>,
                         val adapterInterface: AdapterInterface) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    val currentCal: Calendar = Calendar.getInstance()
    val nextCal: Calendar = Calendar.getInstance()

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if(viewType == 0) {
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_message_typing, parent, false))
    }else {
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_message, parent, false))
    }

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        val v = holder.itemView
        if(position == 0) {
            v.userChipLy.removeAllViews()
            if(typingList.isEmpty()) {
                v.typingIndicator.smoothToHide()
            }else {
                v.typingIndicator.smoothToShow()
                typingList.forEach {
                    val imageView = ImageView(context)
                    imageView.layoutParams = ViewGroup.LayoutParams(30, 30)
                    imageView.setImageResource(R.drawable.default_profile)
                    v.userChipLy.addView(imageView)
                }
            }
        }else {
            val message = getItem(position)
            val nextMessage = getItem(position + 1)

            message?.let {
                v.messageText.text = message.text
                currentCal.timeInMillis = message.dtCreated
                nextCal.timeInMillis = nextMessage?.dtCreated ?: 0

                if(nextMessage != null
                        && message.userId.equals(nextMessage.userId)
                        && message.dtCreated - nextMessage.dtCreated < 1000 * 60) {
                    v.nameLy.visibility = View.GONE
                    v.profileImage.visibility = View.INVISIBLE
                    v.topMargin.visibility = View.GONE
                }else {
                    v.nameLy.visibility = View.VISIBLE
                    v.profileImage.visibility = View.VISIBLE
                    v.topMargin.visibility = View.VISIBLE
                    v.nameText.text = it.userName
                    v.timeText.text = DateFormat.getTimeInstance().format(currentCal.time)

                    Glide.with(context)
                            .load(makePublicPhotoUrl(message.userId))
                            .bitmapTransform(CropCircleTransformation(context))
                            .thumbnail(0.1f)
                            .placeholder(R.drawable.default_profile)
                            .into(v.profileImage)

                    v.profileImage.setOnClickListener{ adapterInterface.onProfileClicked(message.userId!!) }
                }

                if(currentCal.get(Calendar.YEAR) == nextCal.get(Calendar.YEAR)
                        && currentCal.get(Calendar.DAY_OF_YEAR) == nextCal.get(Calendar.DAY_OF_YEAR)) {
                    v.dateDivider.visibility = View.GONE
                }else {
                    v.dateDivider.visibility = View.VISIBLE
                    v.dateDividerText.text = DateFormat.getDateInstance().format(Date(message.dtCreated))
                }

                v.setOnClickListener { adapterInterface.onMessageClicked(message) }
            }
        }
    }

    private fun getItem(position: Int): Message? {
        try{
            return messageList[position - 1]
        }catch (e: Exception) {
            return null
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0) return 0 else return 1
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = messageList.size + 1

    interface AdapterInterface {
        fun onMessageClicked(message: Message)
        fun onProfileClicked(userId: String)
    }

    fun  refreshTypingList() {
        notifyItemChanged(0)
    }
}