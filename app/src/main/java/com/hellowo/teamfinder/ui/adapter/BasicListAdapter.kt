package com.hellowo.teamfinder.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_basic.view.*

import com.hellowo.teamfinder.R


class BasicListAdapter(private val context: Context, private val mContentsList: List<String>,
                       private val adapterInterface: AdapterInterface) : RecyclerView.Adapter<BasicListAdapter.ViewHolder>() {

    inner class ViewHolder(container: View) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_basic, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val role = mContentsList[position]
        val v = holder.itemView
        v.titleText.text = role
        v.imageView.visibility = View.GONE
        v.setOnClickListener { adapterInterface.onItemClicked(role, position) }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mContentsList.size
    }

    interface AdapterInterface {
        fun onItemClicked(item: String, position: Int)
    }
}