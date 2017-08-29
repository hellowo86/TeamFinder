package com.hellowo.teamfinder.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hellowo.teamfinder.R


class ChatFilterDialog : BottomSheetDialog() {

    fun setDialogInterface() {}

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.dialog_chat_filter, null)
        dialog.setContentView(contentView)

        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        sheetBehavior = layoutParams.behavior as BottomSheetBehavior<*>?
        if (sheetBehavior != null) {
            sheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)

            val recyclerView = contentView.findViewById<View>(R.id.recyclerView) as RecyclerView
            val cancelBtn = contentView.findViewById<View>(R.id.cancelBtn) as ImageButton

            val layoutManager = FlexboxLayoutManager(context)
            recyclerView.setHasFixedSize(true)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            recyclerView.layoutManager = layoutManager

            cancelBtn.setOnClickListener {  }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

    }
}
