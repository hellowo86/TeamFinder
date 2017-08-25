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
import com.hellowo.teamfinder.model.Category;
import com.hellowo.teamfinder.ui.adapter.BasicListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectRoleDialog extends BottomSheetDialog {
    DialogInterface dialogInterface;
    Category category;

    public void setDialogInterface(DialogInterface dialogInterface) {
        this.dialogInterface = dialogInterface;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_bottom_sheet_list, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        sheetBehavior = (BottomSheetBehavior)layoutParams.getBehavior();
        if (sheetBehavior != null && category != null) {
            sheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);

            TextView mainTopTitle = (TextView) contentView.findViewById(R.id.mainTopTitle);
            mainTopTitle.setText(R.string.select_role);

            RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            List<String> items = new ArrayList<>();
            items.add(getContext().getString(R.string.free_role));
            items.addAll(category.getRoles());

            recyclerView.setAdapter(new BasicListAdapter(getContext(), items, (item, pos)->{
                if(dialogInterface != null) {
                    dialogInterface.onSelectedRole(item);
                }
            }));

            ImageButton backBtn = (ImageButton) contentView.findViewById(R.id.backBtn);
            backBtn.setOnClickListener(v->dismiss());
        }
    }

    public interface DialogInterface{
        void onSelectedRole(String role);
    }
}
