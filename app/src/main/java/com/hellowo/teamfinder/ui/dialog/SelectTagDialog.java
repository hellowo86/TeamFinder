package com.hellowo.teamfinder.ui.dialog;

import android.app.Dialog;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.HashTagData;
import com.hellowo.teamfinder.model.HashTag;
import com.hellowo.teamfinder.ui.adapter.TagListAdapter;

public class SelectTagDialog extends BottomSheetDialog {
    DialogInterface dialogInterface;

    public void setDialogInterface(DialogInterface dialogInterface) {
        this.dialogInterface = dialogInterface;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_bottom_sheet_list, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        sheetBehavior = (BottomSheetBehavior)layoutParams.getBehavior();
        if (sheetBehavior != null) {
            sheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);

            TextView mainTopTitle = (TextView) contentView.findViewById(R.id.mainTopTitle);
            mainTopTitle.setText(R.string.select_tag);

            RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            TagListAdapter tagListAdapter = new TagListAdapter(
                    getContext(),
                    false,
                    HashTagData.get().getHashTags(),
                    hashTag -> {
                        if(dialogInterface != null) {
                            dialogInterface.onSelectItem(hashTag);
                            dismiss();
                        }
                    });
            recyclerView.setAdapter(tagListAdapter);

            ImageButton backBtn = (ImageButton) contentView.findViewById(R.id.backBtn);
            backBtn.setOnClickListener(v->dismiss());
        }
    }

    public interface DialogInterface{
        void onSelectItem(HashTag hashTag);
    }
}
