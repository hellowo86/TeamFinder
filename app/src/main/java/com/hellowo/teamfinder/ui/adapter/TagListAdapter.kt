package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.model.HashTag
import kotlinx.android.synthetic.main.list_item_tag.view.*

class TagListAdapter(val context: Context,
                     val mContentsList: List<HashTag>,
                     val adapterInterface: (HashTag) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_tag, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hashTag = mContentsList[position]
        val v = holder.itemView
        v.tagText.text = hashTag.makeTag()
        v.setOnClickListener { adapterInterface.invoke(hashTag) }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mContentsList.size
    }
}