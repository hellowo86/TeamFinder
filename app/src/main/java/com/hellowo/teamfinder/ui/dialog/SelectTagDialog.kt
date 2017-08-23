package com.hellowo.teamfinder.ui.dialog

import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.HashTagData
import com.hellowo.teamfinder.model.HashTag
import com.hellowo.teamfinder.ui.adapter.TagListAdapter
import com.google.android.flexbox.JustifyContent
import com.google.android.flexbox.FlexDirection
import com.hellowo.teamfinder.App.context
import com.google.android.flexbox.FlexboxLayoutManager



class SelectTagDialog : BottomSheetDialog() {
    internal lateinit var dialogInterface: (HashTag) -> Unit

    fun setDialogInterface(dialogInterface: (HashTag) -> Unit) {
        this.dialogInterface = dialogInterface
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.dialog_bottom_sheet_list, null)
        dialog.setContentView(contentView)

        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        sheetBehavior = layoutParams.behavior as BottomSheetBehavior<*>?
        if (sheetBehavior != null) {
            sheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)

            val mainTopTitle = contentView.findViewById<View>(R.id.mainTopTitle) as TextView
            mainTopTitle.setText(R.string.select_tag)

            val recyclerView = contentView.findViewById<View>(R.id.recyclerView) as RecyclerView
            val layoutManager = FlexboxLayoutManager(context)
            val tagListAdapter = TagListAdapter(context, HashTagData.hashTags) { hashTag ->
                dialogInterface.invoke(hashTag)
                dismiss()
            }
            recyclerView.setHasFixedSize(true)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = tagListAdapter

            val backBtn = contentView.findViewById<View>(R.id.backBtn) as ImageButton
            backBtn.setOnClickListener { dismiss() }
        }
    }
}
