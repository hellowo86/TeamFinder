package com.hellowo.teamfinder.ui.adapter.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class HorizontalDecoration extends RecyclerView.ItemDecoration {

    private final int horizontalSpaceHeight;

    public HorizontalDecoration(int horizontalSpaceHeight) {
        this.horizontalSpaceHeight = horizontalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) > 0) {
            outRect.left = horizontalSpaceHeight;
        }
    }

}