package com.lydia.loyouyang.customviewlearning.customview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class BuboView extends View {

    private static final String TAG = "ouyang";

    //bubo colors
    private static final int[] COLORS = {0xFFFF0000, 0xFFFF7F00, 0xFFFFFF00, 0xFF00FF00,
                                        0xFF00FFFF, 0xFF0000FF, 0xFF8B00FF};

    private static final int WATER_COLOR = 0xcccfff;

    //default width, height for view
    private static final int DEFAULT_WIDHT = 600;
    private static final int DEFAULT_HEIGHT = 800;


    private Paint mBuboPaint = new Paint();
    private Paint mWaterPaint = new Paint();

    //bubo center point x, y
    private float mBuboCenterX = 0f;
    private float mBuboCenterY = 0f;
    private float mBuboCenterX2 = 0f;
    private float mBuboCenterY2 = 0f;
    private float mBuboCenterX3 = 0f;
    private float mBuboCenterY3 = 0f;

    private static final float DEFAULT_RADIUS = 50f;

    private float mBuboRadius = DEFAULT_RADIUS;

    //bubo raise height
    private float mRaiseHeight = 0;

    //view width, height
    private int mViewWidth;
    private int mViewHeight;

    //bubo raise height
    private static final float MAX_RAISE_HEIGHT = 1500f;

    private int mBuboCount = -1;

    private ValueAnimator mRaiseHeightAnimator;



    public BuboView(Context context) {
        this(context, null);
    }

    public BuboView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BuboView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mRaiseHeightAnimator = ValueAnimator.ofFloat(0, MAX_RAISE_HEIGHT);
        mRaiseHeightAnimator.setDuration(5000);
        mRaiseHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float currentValue = (float) animator.getAnimatedValue();
                mRaiseHeight = currentValue;
                mBuboRadius = DEFAULT_RADIUS * (1 + currentValue / MAX_RAISE_HEIGHT);
                mBuboCenterX = mViewWidth / 2;
                mBuboCenterY = mViewHeight - currentValue - 2 * DEFAULT_RADIUS;

                mBuboCenterX2 = mViewWidth / 2 - 10 * (float)Math.sqrt(Math.abs(currentValue));
                mBuboCenterY2 = mViewHeight - currentValue - 2 * DEFAULT_RADIUS;

                mBuboCenterX3 = mViewWidth / 2 + 10 * (float)Math.sqrt(Math.abs(currentValue));
                mBuboCenterY3 = mViewHeight - currentValue - 2 * DEFAULT_RADIUS;
                invalidate();
            }
        });

        mRaiseHeightAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mBuboCount++;
                mBuboCount = mBuboCount % COLORS.length;
                mBuboPaint.setColor(COLORS[mBuboCount]);
            }
        });

        mRaiseHeightAnimator.setInterpolator(new AccelerateInterpolator());
        mRaiseHeightAnimator.setRepeatCount(100);
    }

    private void init(){
        mBuboPaint.setStyle(Paint.Style.FILL);
        mBuboPaint.setAntiAlias(true);
        mBuboPaint.setColor(COLORS[0]);

        mWaterPaint.setColor(WATER_COLOR);
        mWaterPaint.setStyle(Paint.Style.STROKE);
        mWaterPaint.setStrokeWidth(20f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(DEFAULT_WIDHT, DEFAULT_HEIGHT);
        }else if (widthMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(DEFAULT_WIDHT, heightSize);
        }else if (heightMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSize, DEFAULT_HEIGHT);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mBuboCenterX = w / 2;
        mBuboCenterY = h - 2 * DEFAULT_RADIUS;
        mBuboCenterX2 = w / 2;
        mBuboCenterY2 = h - 2 * DEFAULT_RADIUS;
        mBuboCenterX3 = w / 2;
        mBuboCenterY3 = h - 2 * DEFAULT_RADIUS;
        mRaiseHeightAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xffcccc);
        drawBubos(canvas);
    }

    private void drawWater(Canvas canvas){

    }
    private void drawBubos(Canvas canvas){
        int i = 0;
        while (i < 10) {
            i++;
            canvas.drawCircle(mBuboCenterX, mBuboCenterY, mBuboRadius, mBuboPaint);
            mBuboPaint.setColor(COLORS[(mBuboCount + 1) % COLORS.length]);
            canvas.drawCircle(mBuboCenterX2, mBuboCenterY2, mBuboRadius, mBuboPaint);

            mBuboPaint.setColor(COLORS[(mBuboCount + 2) % COLORS.length]);
            canvas.drawCircle(mBuboCenterX3, mBuboCenterY3, mBuboRadius, mBuboPaint);

            mBuboCenterX2 -= 200;
            mBuboCenterX3 += 200;

            postInvalidateDelayed(100);
        }
    }
}
