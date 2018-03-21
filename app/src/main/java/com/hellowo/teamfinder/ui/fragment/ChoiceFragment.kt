package com.hellowo.teamfinder.ui.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.transition.Explode
import android.support.transition.Fade
import android.support.transition.Slide
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.TranslateAnimation
import com.hellowo.teamfinder.R
import android.widget.BaseAdapter
import com.hellowo.teamfinder.utils.ViewUtil
import com.hellowo.teamfinder.utils.log
import com.hellowo.teamfinder.utils.setScale
import com.hellowo.teamfinder.viewmodel.ChoiceViewModel
import kotlinx.android.synthetic.main.fragment_choice.*
import link.fls.swipestack.SwipeStack


class ChoiceFragment : Fragment() {
    lateinit var viewModel: ChoiceViewModel
    lateinit var adapter: SwipeStackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ChoiceViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
        setObserver()
        viewModel.loadInterestMeList()
    }

    private fun setLayout() {
        adapter = SwipeStackAdapter(listOf("!!"))
        swipeStack.adapter = adapter
        swipeStack.setListener(object : SwipeStack.SwipeStackListener{
            override fun onViewSwipedToLeft(position: Int) {
                log("left"+position.toString())
            }

            override fun onViewSwipedToRight(position: Int) {
            }

            override fun onStackEmpty() {
            }
        })
        swipeStack.setSwipeProgressListener(object : SwipeStack.SwipeProgressListener{
            override fun onSwipeEnd(position: Int) {
                setScale(choiceYesBtn, 1f)
                setScale(choiceNoBtn, 1f)
            }
            override fun onSwipeStart(position: Int) {}
            override fun onSwipeProgress(position: Int, progress: Float) {
                log(progress.toString())
                if(progress > 0) {
                    choiceYesBtn.scaleX = 1f + Math.abs(progress)
                    choiceYesBtn.scaleY = 1f + Math.abs(progress)
                }else {
                    choiceNoBtn.scaleX = 1f + Math.abs(progress)
                    choiceNoBtn.scaleY = 1f + Math.abs(progress)
                }
            }
        })

        startBtn.setOnClickListener {
            rippleView.visibility = View.VISIBLE
            rippleView.startRippleAnimation()
            val anim = TranslateAnimation(0f, 0f, 0f, -15f)
            anim.duration = 500
            anim.repeatCount = Animation.INFINITE
            anim.repeatMode = Animation.REVERSE
            centerImage.startAnimation(anim)
        }

    }

    private fun setObserver() {
        viewModel.interestMeList.observe(this, Observer { it?.let { if(it.size > 0) showChoiceView() } })
        viewModel.loading.observe(this, Observer { progressBar.visibility = if(it as Boolean) View.VISIBLE else View.GONE })
    }

    private fun showChoiceView() {
        val trasition = Slide()
        trasition.slideEdge = Gravity.BOTTOM
        trasition.duration = 500
        trasition.interpolator = FastOutSlowInInterpolator()
        TransitionManager.beginDelayedTransition(choiceLy, trasition)
        ViewUtil.toggleVisibility(choiceLy, swipeStack, choiceNoBtn, choiceYesBtn)
    }

    /*
        TransitionManager.beginDelayedTransition(coordinator_layout, makeAutoTransition())
         val explode = Explode()
            explode.setDuration(500)
            explode.setInterpolator(AnticipateOvershootInterpolator())
            return explode
    */
    inner class SwipeStackAdapter(private val mData: List<String>) : BaseAdapter() {

        override fun getCount(): Int {
            return 10
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
