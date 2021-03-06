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
import com.hellowo.teamfinder.ui.adapter.GameListAdapter;

public class SelectGameDialog extends BottomSheetDialog {
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
            mainTopTitle.setText(R.string.select_game);

            RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new GameListAdapter(getContext(), game->{
                if(dialogInterface != null) {
                    dialogInterface.onSelectedGame(game);
                }
            }));

            ImageButton backBtn = (ImageButton) contentView.findViewById(R.id.backBtn);
            backBtn.setOnClickListener(v->dismiss());
        }
    }

    public interface DialogInterface{
        void onSelectedGame(Category category);
    }
}
