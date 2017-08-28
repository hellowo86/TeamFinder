package com.hellowo.teamfinder.ui.dialog

import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.HashTagData
import com.hellowo.teamfinder.model.HashTag
import com.hellowo.teamfinder.ui.adapter.TagListAdapter


class ChatFilterDialog : BottomSheetDialog() {
    internal lateinit var dialogInterface: (HashTag) -> Unit

    fun setDialogInterface(dialogInterface: (HashTag) -> Unit) {
        this.dialogInterface = dialogInterface
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.dialog_search_tag, null)
        dialog.setContentView(contentView)

        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        sheetBehavior = layoutParams.behavior as BottomSheetBehavior<*>?
        if (sheetBehavior != null) {
            sheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)

            val recyclerView = contentView.findViewById<View>(R.id.recyclerView) as RecyclerView
            val cancelBtn = contentView.findViewById<View>(R.id.cancelBtn) as ImageButton

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

            cancelBtn.setOnClickListener {  }
        }
    }
}
