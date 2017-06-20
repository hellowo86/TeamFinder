package com.hellowo.teamfinder.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Day2Life Android Dev on 2016-08-16.
 * 터치 이벤트가 통과하는것을 켜고 끌수 있음
 */
public class TouchPassFrameLayout extends FrameLayout{
    private boolean passTouch = true;

    public TouchPassFrameLayout(Context context) {
        super(context);
    }

    public TouchPassFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchPassFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return passTouch ? false : super.dispatchTouchEvent(ev);
    }

    public boolean isPassTouch() {
        return passTouch;
    }

    public void setPassTouch(boolean passTouch) {
        this.passTouch = passTouch;
    }
}
