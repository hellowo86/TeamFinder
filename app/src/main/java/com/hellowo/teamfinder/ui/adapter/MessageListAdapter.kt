package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.Message
import com.hellowo.teamfinder.utils.FirebaseUtils
import com.hellowo.teamfinder.utils.makeActiveTimeText
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.list_item_message.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MessageListAdapter(val context: Context,
                         val mContentsList: List<Message>,
                         val adapterInterface: AdapterInterface) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)
    val currentCal = Calendar.getInstance()
    val nextCal = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_message, parent, false))

    override fun onBindViewHolder(holder: MessageListAdapter.ViewHolder, position: Int) {
        val message = mContentsList[position]
        val v = holder.itemView

        v.messageText.text = message.text
        currentCal.timeInMillis = message.dtCreated
        nextCal.timeInMillis = if (position != mContentsList.lastIndex) mContentsList[position + 1].dtCreated else 0

        if(position != mContentsList.lastIndex
                && message.userId.equals(mContentsList[position + 1].userId)
                && message.dtCreated - mContentsList[position + 1].dtCreated < 1000 * 60) {
            v.nameLy.visibility = View.GONE
            v.profileImage.visibility = View.INVISIBLE
            v.topMargin.visibility = View.GONE
        }else {
            v.nameLy.visibility = View.VISIBLE
            v.profileImage.visibility = View.VISIBLE
            v.topMargin.visibility = View.VISIBLE
            v.nameText.text = message.userName
            v.timeText.text = DateFormat.getTimeInstance().format(currentCal.time)

            Glide.with(context)
                    .load(FirebaseUtils.makePublicPhotoUrl(message.userId))
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

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = mContentsList.size

    interface AdapterInterface {
        fun onMessageClicked(message: Message)
        fun onProfileClicked(userId: String)
    }
}