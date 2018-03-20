package com.hellowo.teamfinder.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hellowo.teamfinder.R
import android.widget.BaseAdapter
import com.hellowo.teamfinder.utils.log
import kotlinx.android.synthetic.main.fragment_choice.*
import link.fls.swipestack.SwipeStack


class ChoiceFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeStack.adapter = SwipeStackAdapter(listOf("!!"))
        swipeStack.setListener(object : SwipeStack.SwipeStackListener{
            override fun onViewSwipedToLeft(position: Int) {
                log(position.toString())
            }

            override fun onViewSwipedToRight(position: Int) {
            }

            override fun onStackEmpty() {
            }
        })
        swipeStack.setSwipeProgressListener(object : SwipeStack.SwipeProgressListener{
            override fun onSwipeEnd(position: Int) {
            }

            override fun onSwipeStart(position: Int) {
            }

            override fun onSwipeProgress(position: Int, progress: Float) {
                log(progress.toString())
            }

        })
        centerImage.setOnClickListener { rippleView.startRippleAnimation() }
    }

    inner class SwipeStackAdapter(private val mData: List<String>) : BaseAdapter() {

        override fun getCount(): Int {
            return 3
        }

        override fun getItem(position: Int): String {
            return mData[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convertView = layoutInflater.inflate(R.layout.item_card_stack, parent, false)
            return convertView
        }
    }
}
