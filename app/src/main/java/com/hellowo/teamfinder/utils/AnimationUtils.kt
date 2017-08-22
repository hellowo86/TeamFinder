package com.hellowo.teamfinder.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.widget.ImageView

fun startScaleShowAnimation(view: View) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f).setDuration(250),
            ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f).setDuration(250),
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun startScaleHideAnimation(view: View, adapter: AnimatorListenerAdapter) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f).setDuration(250),
            ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.addListener(adapter)
    animSet.start()
}

fun startFadeInAnimation(view: View) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(1000)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun startFadeOutAnimation(view: View, adapter: AnimatorListenerAdapter) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(1000)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.addListener(adapter)
    animSet.start()
}

fun startFromBottomSlideAppearAnimation(view: View, offset: Float) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "translationY", offset, 0f).setDuration(250),
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun startToBottomSlideDisappearAnimation(view: View, offset: Float) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "translationY", 0f, offset).setDuration(250),
            ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun startToTopDisappearAnimation(view: View, offset: Float) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "translationY", 0f, -offset).setDuration(250),
            ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun startFromTopAppearAnimation(view: View, offset: Float) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "translationY", -offset, 0f).setDuration(250),
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun startFromLeftSlideAppearAnimation(view: View, offset: Float) {
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "translationX", -offset, 0f).setDuration(250),
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun moveToTop(view: View, offset: Float) {
    val current_pos = view.translationY
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "translationY", current_pos, current_pos - offset).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun moveToBottom(view: View, offset: Float) {
    val current_pos = view.translationY
    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "translationY", current_pos, current_pos + offset).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}

fun changeScale(view: ImageView, offset: Float) {
    val current_scale = view.scaleX

    val animSet = AnimatorSet()
    animSet.playTogether(
            ObjectAnimator.ofFloat(view, "scaleX", current_scale, offset).setDuration(250),
            ObjectAnimator.ofFloat(view, "scaleY", current_scale, offset).setDuration(250)
    )
    animSet.interpolator = FastOutSlowInInterpolator()
    animSet.start()
}