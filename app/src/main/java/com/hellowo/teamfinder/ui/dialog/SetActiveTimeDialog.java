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
import com.hellowo.teamfinder.model.Game;
import com.hellowo.teamfinder.ui.adapter.BasicListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetActiveTimeDialog extends BottomSheetDialog {
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
            mainTopTitle.setText(R.string.active_time);

            RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new BasicListAdapter(
                    getContext(),
                    Arrays.asList(getContext().getResources().getStringArray(R.array.active_time_text)),
                    (item, pos)->{
                        if(dialogInterface != null) {
                            int hour = getContext().getResources().getIntArray(R.array.active_time)[pos];
                            long time;
                            if(hour == -1) {
                                time = Long.MAX_VALUE;
                            }else {
                                time = System.currentTimeMillis() + (hour * 1000 * 60 * 60);
                            }

                            dialogInterface.onSetActiveTime(item, time);
                        }
                    }));

            ImageButton backBtn = (ImageButton) contentView.findViewById(R.id.backBtn);
            backBtn.setOnClickListener(v->dismiss());
        }
    }

    public interface DialogInterface{
        void onSetActiveTime(String text, long dtActive);
    }
}
