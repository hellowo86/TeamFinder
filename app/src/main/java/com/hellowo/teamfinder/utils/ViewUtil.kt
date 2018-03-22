package com.hellowo.teamfinder.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object ViewUtil {

    fun runCallbackAfterViewDrawed(view: View, callback: Runnable) {
        view.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        callback.run()
                    }
                })
    }

    fun dpToPx(c: Context, dp: Float): Float {
        val density = c.resources.displayMetrics.density
        return dp * density
    }

    fun hideKeyPad(activity: Activity, editText: EditText) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun disableParentsClip(view: View) {
        var view = view
        while (view.parent != null && view.parent is ViewGroup) {
            val viewGroup = view.parent as ViewGroup
            viewGroup.clipChildren = false
            viewGroup.clipToPadding = false
            view = viewGroup
        }
    }

    fun enableParentsClip(view: View) {
        var view = view
        while (view.parent != null && view.parent is ViewGroup) {
            val viewGroup = view.parent as ViewGroup
            viewGroup.clipChildren = true
            viewGroup.clipToPadding = true
            view = viewGroup
        }
    }

    // Custom method to toggle visibility of views
    fun toggleVisibility(vararg views: View) {
        // Loop through the views
        for (v in views) {
            if (v.visibility == View.VISIBLE) {
                v.visibility = View.INVISIBLE
            } else {
                v.visibility = View.VISIBLE
            }
        }
    }
}
