package com.hellowo.teamfinder.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PagingControlableViewPager extends ViewPager {
    private boolean isPagingEnabled;

    public PagingControlableViewPager(Context context) {
        super(context);
        this.isPagingEnabled = true;
    }

    public PagingControlableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isPagingEnabled = true;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isPagingEnabled) {
            try{
                return super.onInterceptTouchEvent(event);
            }catch (Exception e){}
        }
        return false;
    }
    
    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
}