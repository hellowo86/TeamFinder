package com.hellowo.teamfinder.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.day2life.timeblocks.R;
import com.day2life.timeblocks.application.AppScreen;
import com.day2life.timeblocks.util.log.Lo;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Day2Life Android Dev on 2016-08-16.
 * @author Day2Life Android Dev
 */
public class LeafFallFrameLayout extends FrameLayout{
    private boolean passTouch = true;
    private final static int topMargin = AppScreen.dpToPx(25);
    private final static int leafSize = AppScreen.dpToPx(15);
    private final static int[] LEAVES = new int[]{
            R.drawable.sakura0,
            R.drawable.sakura1
    };

    public LeafFallFrameLayout(Context context) {
        super(context);
    }

    public LeafFallFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeafFallFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int viewId = new Random().nextInt(LEAVES.length);
            Drawable d = getResources().getDrawable(LEAVES[viewId]);
            ImageView leafImageView = new ImageView(getContext());
            leafImageView.setImageDrawable(d);
            addView(leafImageView);

            LayoutParams animationLayout = (LayoutParams) leafImageView.getLayoutParams();
            animationLayout.setMargins(0, -topMargin, 0, 0);
            animationLayout.width = leafSize;
            animationLayout.height = leafSize;

            startAnimation(leafImageView);
        }
    };

    private class AnimTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(0x001);
        }
    }

    Timer mTimer;

    public void start() {
        mTimer = new Timer();
        mTimer.schedule(new AnimTimerTask(), 0, 1000);
    }

    public void stop() {
        if(mTimer != null) {
            mTimer.cancel();
        }
    }

    public void startAnimation(final ImageView leafImageView) {

        leafImageView.setPivotX(leafImageView.getWidth()/2);
        leafImageView.setPivotY(leafImageView.getHeight()/2);

        long delay = new Random().nextInt(2000);

        final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(9000);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setStartDelay(delay);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int width = AppScreen.getCurrentScrrenWidth();
            int height = AppScreen.getCurrentScrrenHeight();
            int startx = new Random().nextInt(width);
            int movex = new Random().nextInt(width) - (width/2);
            int angle = 50 + (int)(Math.random() * 101);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) (animation.getAnimatedValue());
                leafImageView.setRotation(angle*value);
                leafImageView.setTranslationX(startx + (movex)*value);
                leafImageView.setTranslationY((height + topMargin + topMargin)*value);
                if(value == 1f) {
                    removeView(leafImageView);
                }
            }
        });

        animator.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !passTouch && super.dispatchTouchEvent(ev);
    }

    public boolean isPassTouch() {
        return passTouch;
    }

    public void setPassTouch(boolean passTouch) {
        this.passTouch = passTouch;
    }
}
