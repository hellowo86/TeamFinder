package com.hellowo.teamfinder.ui.dialog

import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.hellowo.teamfinder.R
import com.hellowo.teamfinder.data.SearchableTagData
import com.hellowo.teamfinder.model.HashTag
import com.hellowo.teamfinder.ui.adapter.HashTagListAdapter


class SelectTagDialog : BottomSheetDialog() {
    internal lateinit var dialogInterface: (HashTag) -> Unit
    val tagList: ArrayList<HashTag> = ArrayList()
    lateinit var tagListAdapter: HashTagListAdapter

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
            val emptyContentsView = contentView.findViewById<View>(R.id.emptyContentsView) as LinearLayout
            val cancelBtn = contentView.findViewById<View>(R.id.cancelBtn) as ImageButton
            val searchInput = contentView.findViewById<View>(R.id.searchInput) as EditText
            val progressBar = contentView.findViewById<View>(R.id.progressBar) as ProgressBar

            searchInput.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                    cancelBtn.visibility = if(text.isNotEmpty()) View.VISIBLE else View.GONE
                }
            })
            searchInput.setOnEditorActionListener { _, actionId, _ ->
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    progressBar.visibility = View.VISIBLE
                    SearchableTagData.searchHashTag(searchInput.text.toString().trim(), object: ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {
                            progressBar.visibility = View.GONE
                        }
                        override fun onDataChange(p0: DataSnapshot?) {
                            tagList.clear()
                            p0?.children?.forEach {
                                val hashTag = HashTag()
                                hashTag.name = it.key
                                hashTag.count = it.value.toString().toInt()
                                tagList.add(hashTag)
                            }
                            emptyContentsView.visibility = if(tagList.isNotEmpty()) View.GONE else View.VISIBLE
                            tagListAdapter.notifyDataSetChanged()
                            progressBar.visibility = View.GONE
                        }
                    })
                    searchInput.setText("")
                }
                return@setOnEditorActionListener false
            }

            val layoutManager = LinearLayoutManager(context)
            tagListAdapter = HashTagListAdapter(context!!, tagList) { hashTag ->
                dialogInterface.invoke(hashTag)
                dismiss()
            }
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = tagListAdapter

            cancelBtn.setOnClickListener { searchInput.setText("") }
        }
    }
}
