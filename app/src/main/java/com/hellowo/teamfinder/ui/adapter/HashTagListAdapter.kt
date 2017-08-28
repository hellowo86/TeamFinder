package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_basic.view.*

import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.HashTag


class HashTagListAdapter(private val context: Context, private val mContentsList: List<HashTag>,
                       private val adapterInterface: (HashTag) -> Unit) : RecyclerView.Adapter<HashTagListAdapter.ViewHolder>() {

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_basic, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = mContentsList[position]
        val v = holder.itemView
        v.titleText.text = tag.name
        v.subText.visibility = View.VISIBLE
        v.subText.text = String.format(context.getString(R.string.hashtag_count), tag.count.toString())
        v.imageView.setImageResource(R.drawable.hashtag)
        v.setOnClickListener { adapterInterface.invoke(tag) }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mContentsList.size
    }
}