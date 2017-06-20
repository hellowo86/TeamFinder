package com.hellowo.teamfinder.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.day2life.timeblocks.application.AppScreen;
import com.day2life.timeblocks.util.log.Lo;

/**
 * 움직이는 점선 뷰
 */
public class MovingDashView extends FrameLayout {
    private final static int DASH_WIDTH = AppScreen.dpToPx(6);
    private final static int DASH_INTERVAL = AppScreen.dpToPx(5);
    private final static int LINE_WIDTH = AppScreen.dpToPx(3);
    private final static int DASH_COLOR = Color.parseColor("#6b000000");
    private int moveOffset = 0;
    private int width;
    private int height;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            moveOneFrame();
            invalidate();
            mHandler.sendEmptyMessageDelayed(0, 40); // 25프레임
        }
    };
    private boolean isMoving;

    public MovingDashView(Context context) {
        super(context);
    }

    public MovingDashView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MovingDashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MovingDashView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 1프레임 이동
     */
    private void moveOneFrame(){
        moveOffset += 2;
        if(moveOffset > DASH_WIDTH + DASH_INTERVAL){
            moveOffset = 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        Path path = new Path();
        path.addRect(0, 0, width, height, Path.Direction.CW);
        DashPathEffect dashPath
                = new DashPathEffect(new float[]{DASH_WIDTH, DASH_INTERVAL}, moveOffset);
        paint.setPathEffect(dashPath);
        paint.setStrokeWidth(LINE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(DASH_COLOR);
        canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }

    /**
     * 애니메이션 시작
     */
    public void startMove(){
        isMoving = true;
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 애니메이션 종료
     */
    public void stopMove(){
        isMoving = false;
        mHandler.removeMessages(0);
    }

    public boolean isMoving(){
        return isMoving;
    }
}