package com.lydia.loyouyang.customviewlearning.customview.dropIndicator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.lydia.loyouyang.customviewlearning.R;

/**
 * Created by Ulez on 2016/11/24.
 * Email：lcy1532110757@gmail.com
 */
public class DropIndicator extends ViewGroup {
    private int circleColor;
    private int clickColor;
    private Paint mClickPaint;
    private Paint mTouchedBongPaint;
    private int duration;
    private Paint mPaintCircle;
    private Paint mPaint;
    private Path mPath = new Path();
    private float ratio = 50;
    private final double c = 0.552284749831;
    private final int r = 1;
    private int mWidth;
    private int mHeight;
    private float startX;
    private int startY;
    private float totalOff;
    private float distance;
    private int currOff;
    private float mCurrentTime;
    private int tabNum = 0;
    private XPoint p2, p4;
    private YPoint p1, p3;
    private float mc;
    private float mRadius;
    private int[] roundColors = new int[4];
    private float div;
    private float scale = 0.8f;

    private int currentPos = 0;
    private int toPos = -1;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private int viewPagerState;
    private String TAG = "DropIndicator";
    private boolean animating;
    private boolean bonging = false;

    private Rect[] childrenRects;

    public DropIndicator(Context context) {
        this(context, null);
    }

    public DropIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DropIndicator);
        roundColors[0] = typedArray.getColor(R.styleable.DropIndicator_color1, Color.parseColor("#B04285F4"));
        roundColors[1] = typedArray.getColor(R.styleable.DropIndicator_color2, Color.parseColor("#B0EA4335"));
        roundColors[2] = typedArray.getColor(R.styleable.DropIndicator_color3, Color.parseColor("#B0FBBC05"));
        roundColors[3] = typedArray.getColor(R.styleable.DropIndicator_color4, Color.parseColor("#B034A853"));
        clickColor = typedArray.getColor(R.styleable.DropIndicator_click_color, Color.WHITE);
        circleColor = typedArray.getColor(R.styleable.DropIndicator_circle_color, Color.GRAY);
        mRadius = typedArray.getDimension(R.styleable.DropIndicator_radius, 50);
        duration = typedArray.getInteger(R.styleable.DropIndicator_duration, 1000);
        scale = typedArray.getFloat(R.styleable.DropIndicator_scale, 0.8f);
        typedArray.recycle();
        f = new float[2][3];
        result = new float[3];
        ratio = mRadius;
        mc = (float) (c * ratio);
        mPaintCircle = new Paint();
        mPaintCircle.setColor(circleColor);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStrokeWidth(3);

        mClickPaint = new Paint();
        mClickPaint.setColor(Color.GRAY);
        mClickPaint.setStyle(Paint.Style.STROKE);
        mClickPaint.setAntiAlias(true);
        mClickPaint.setStrokeWidth(10f);

        mTouchedBongPaint = new Paint();
        mTouchedBongPaint.setAntiAlias(true);

        mPaint = new Paint();
        startColor = roundColors[0];
        mPaint.setColor(startColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);

        childrenRects = new Rect[roundColors.length];
        for (int i = 0; i < childrenRects.length; i++) {
            Rect item = new Rect();
            childrenRects[i] = item;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        div = (mWidth - 2 * tabNum * mRadius) / (tabNum + 1);
        startX = div + mRadius;
        startY = mHeight / 2;
        totalOff = (tabNum - 1) * (2 * mRadius + div) - mRadius;

        if (currentPos == 0) {
            mRadius = r * ratio;
            mc = (float) (c * ratio);
            p1 = new YPoint(0, mRadius, mc);
            p3 = new YPoint(0, -mRadius, mc);
            p2 = new XPoint(mRadius, 0, mc);
            p4 = new XPoint(-mRadius, 0, mc);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int toPos = getChildIndexByTouchPoint(x, y);
        if (toPos != -1) {
            if (animator != null)
                animator.cancel();
            if (toPos != currentPos) {
                startAniTo(currentPos, toPos);
            }
        }
        return super.onTouchEvent(event);
    }


    ValueAnimator animator;

    private boolean startAniTo(int currentPos, int toPos) {
        this.currentPos = currentPos;
        this.toPos = toPos;
        if (currentPos == toPos)
            return true;
        startColor = roundColors[(this.currentPos) % 4];
        endColor = roundColors[(toPos) % 4];
        resetP();

        startX = div + mRadius + (this.currentPos) * (div + 2 * mRadius);
        distance = (toPos - this.currentPos) * (2 * mRadius + div) + (toPos > currentPos ? -mRadius : mRadius);

        if (animator == null) {
            animator = ValueAnimator.ofFloat(0, 1.0f);
            animator.setDuration(duration);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentTime = (float) animation.getAnimatedValue();
                    float fraction = animation.getAnimatedFraction();
                    bonging = fraction < 0.5;
                    invalidate();
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animating = true;
                    bonging = true;
                    setTouchAble(!animating);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    goo();
                    animating = false;
                    bonging = false;
                    setTouchAble(!animating);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    goo();
                    animating = false;
                    bonging = false;
                    setTouchAble(!animating);
                    mCurrentTime = 1;
                    invalidate();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        animator.start();
        if (mViewPager != null) {
            mViewPager.setCurrentItem(toPos);
        }

        return true;
    }

    private void setTouchAble(boolean touchAble) {
        if (mViewPager instanceof Touchable)
            ((Touchable) mViewPager).setTouchable(touchAble);
    }

    private void resetP() {
        p1.setY(mRadius);
        p1.setX(0);
        p1.setMc(mc);

        p3.setY(-mRadius);
        p3.setX(0);
        p3.setMc(mc);

        p2.setY(0);
        p2.setX(mRadius);
        p2.setMc(mc);

        p4.setY(0);
        p4.setX(-mRadius);
        p4.setMc(mc);
    }


    private void goo() {
        currentPos = toPos;
    }

    private int startColor;
    private int endColor;
    boolean direction = true;


    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawDropIndicator(canvas);
        drawTouchBong(canvas);
        super.dispatchDraw(canvas);
    }

    private void drawDropIndicator(Canvas canvas){
        canvas.save();
        mPath.reset();
        tabNum = getChildCount();
        for (int i = 0; i < tabNum; i++) {
            if (!(bonging && i == toPos)) {
                mPaintCircle.setColor(roundColors[i]);
                canvas.drawCircle(div + mRadius + i * (div + 2 * mRadius), startY, mRadius, mPaintCircle);
            }
        }
        if (mCurrentTime == 0) {
            resetP();
            canvas.drawCircle(div + mRadius + (currentPos) * (div + 2 * mRadius), startY, 0, mClickPaint);
            mPaint.setColor(startColor);
            canvas.translate(startX, startY);
            if (toPos > currentPos) {
                p2.setX(mRadius);
            } else {
                p4.setX(-mRadius);
            }
        }

        if (mCurrentTime > 0 && mCurrentTime <= 0.2) {
            direction = toPos > currentPos;
            canvas.translate(startX, startY);
            if (toPos > currentPos) {
                p2.setX(mRadius + 2 * 5 * mCurrentTime * mRadius / 2);
            } else {
                p4.setX(-mRadius - 2 * 5 * mCurrentTime * mRadius / 2);
            }
        } else if (mCurrentTime > 0.2 && mCurrentTime <= 0.5) {
            canvas.translate(startX + (mCurrentTime - 0.2f) * distance / 0.7f, startY);
            if (toPos > currentPos) {
                p2.setX(2 * mRadius);
                p1.setX(0.5f * mRadius * (mCurrentTime - 0.2f) / 0.3f);
                p3.setX(0.5f * mRadius * (mCurrentTime - 0.2f) / 0.3f);
                p2.setMc(mc + (mCurrentTime - 0.2f) * mc / 4 / 0.3f);
                p4.setMc(mc + (mCurrentTime - 0.2f) * mc / 4 / 0.3f);
            } else {
                p4.setX(-2 * mRadius);
                p1.setX(-0.5f * mRadius * (mCurrentTime - 0.2f) / 0.3f);
                p3.setX(-0.5f * mRadius * (mCurrentTime - 0.2f) / 0.3f);
                p2.setMc(mc + (mCurrentTime - 0.2f) * mc / 4 / 0.3f);
                p4.setMc(mc + (mCurrentTime - 0.2f) * mc / 4 / 0.3f);
            }
        } else if (mCurrentTime > 0.5 && mCurrentTime <= 0.8) {
            canvas.translate(startX + (mCurrentTime - 0.2f) * distance / 0.7f, startY);
            if (toPos > currentPos) {
                p1.setX(0.5f * mRadius + 0.5f * mRadius * (mCurrentTime - 0.5f) / 0.3f);
                p3.setX(0.5f * mRadius + 0.5f * mRadius * (mCurrentTime - 0.5f) / 0.3f);
                p2.setMc(1.25f * mc - 0.25f * mc * (mCurrentTime - 0.5f) / 0.3f);
                p4.setMc(1.25f * mc - 0.25f * mc * (mCurrentTime - 0.5f) / 0.3f);
            } else {
                p1.setX(-0.5f * mRadius - 0.5f * mRadius * (mCurrentTime - 0.5f) / 0.3f);
                p3.setX(-0.5f * mRadius - 0.5f * mRadius * (mCurrentTime - 0.5f) / 0.3f);
                p2.setMc(1.25f * mc - 0.25f * mc * (mCurrentTime - 0.5f) / 0.3f);
                p4.setMc(1.25f * mc - 0.25f * mc * (mCurrentTime - 0.5f) / 0.3f);
            }
        } else if (mCurrentTime > 0.8 && mCurrentTime <= 0.9) {
            p2.setMc(mc);
            p4.setMc(mc);
            canvas.translate(startX + (mCurrentTime - 0.2f) * distance / 0.7f, startY);
            if (toPos > currentPos) {
                p4.setX(-mRadius + 1.6f * mRadius * (mCurrentTime - 0.8f) / 0.1f);
            } else {
                p2.setX(mRadius - 1.6f * mRadius * (mCurrentTime - 0.8f) / 0.1f);
            }
        } else if (mCurrentTime > 0.9 && mCurrentTime < 1) {
            if (toPos > currentPos) {
                p1.setX(mRadius);
                p3.setX(mRadius);
                canvas.translate(startX + distance, startY);
                p4.setX(0.6f * mRadius - 0.6f * mRadius * (mCurrentTime - 0.9f) / 0.1f);
            } else {
                p1.setX(-mRadius);
                p3.setX(-mRadius);
                canvas.translate(startX + distance, startY);
                p2.setX(-0.6f * mRadius + 0.6f * mRadius * (mCurrentTime - 0.9f) / 0.1f);
            }
        }
        if (mCurrentTime == 1) {
            lastCurrentTime = 0;
            mPaint.setColor(endColor);
            if (direction) {
                p1.setX(mRadius);
                p3.setX(mRadius);
                canvas.translate(startX + distance, startY);
                p4.setX(0);
            } else {
                p1.setX(-mRadius);
                p3.setX(-mRadius);
                canvas.translate(startX + distance, startY);
                p2.setX(0);
            }
            currentPos = toPos;
            resetP();
            if (direction)
                canvas.translate(mRadius, 0);
            else
                canvas.translate(-mRadius, 0);
        }
        mPath.moveTo(p1.x, p1.y);
        mPath.cubicTo(p1.right.x, p1.right.y, p2.bottom.x, p2.bottom.y, p2.x, p2.y);
        mPath.cubicTo(p2.top.x, p2.top.y, p3.right.x, p3.right.y, p3.x, p3.y);
        mPath.cubicTo(p3.left.x, p3.left.y, p4.top.x, p4.top.y, p4.x, p4.y);
        mPath.cubicTo(p4.bottom.x, p4.bottom.y, p1.left.x, p1.left.y, p1.x, p1.y);
        if (mCurrentTime > 0 && mCurrentTime < 1)
            mPaint.setColor(getCurrentColor(mCurrentTime, startColor, endColor));
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    //绘制点击特效
    private void drawTouchBong(Canvas canvas) {
        if (bonging) {
            canvas.save();
            canvas.translate(div + mRadius + toPos * (div + 2 * mRadius), startY);
            float percent;
            if (mCurrentTime > 0 && mCurrentTime <= 0.15) {
                percent = mCurrentTime / 0.15f;
                drawRing(canvas, roundColors[toPos], percent);
            } else if (mCurrentTime > 0.15 && mCurrentTime < 0.3) {
                percent = (mCurrentTime - 0.15f) / 0.1f;
                drawDot(canvas, roundColors[toPos], percent);
            } else {
                percent = (mCurrentTime - 0.3f) / 0.2f;
                drawDotDisappear(canvas, roundColors[toPos], percent);
            }
            canvas.restore();
        }
    }

    //绘制圆环
    private void drawRing(Canvas canvas, int color, float percent) {
        mTouchedBongPaint.setColor(color);
        mTouchedBongPaint.setAntiAlias(true);
        mTouchedBongPaint.setStyle(Paint.Style.STROKE);
        mTouchedBongPaint.setStrokeWidth(0.4f * mRadius * (1f - percent));
        float offsetR = 0.2f * mRadius * percent;
        canvas.drawArc(-mRadius - offsetR, -mRadius - offsetR, mRadius + offsetR, mRadius + offsetR, 0, 360, false, mTouchedBongPaint);
    }

    private float rDotL;
    private float rDotS;
    private float dotRL;
    private float dotRS;

    private static final int[] dotColors = {0xffdaa9fa, 0xfff2bf4b, 0xffe3bca6, 0xff329aed,
            0xffb1eb99, 0xff67c9ad, 0xffde6bac};

    //绘制圆点
    private void drawDot(Canvas canvas, int color, float percent) {
        mTouchedBongPaint.setColor(color);
        mTouchedBongPaint.setAntiAlias(true);
        mTouchedBongPaint.setStyle(Paint.Style.FILL);
        double angleA = 0;
        double angleB = -Math.PI / 10;
        dotRS = mRadius / 7;
        dotRL = mRadius / 5;

        rDotS = 1.3f * mRadius + 0.4f * percent;
        rDotL = 1.3f * mRadius + 0.8f * percent;

        for (int i = 0; i < 7; i++) {
            mTouchedBongPaint.setColor(dotColors[i]);
            canvas.drawCircle((float) (rDotS * Math.sin(angleA)), (float) (rDotS * Math.cos(angleA)),
                    dotRS, mTouchedBongPaint);
            angleA += 2 * Math.PI / 7;
            canvas.drawCircle((float) (rDotL * Math.sin(angleB)), (float) (rDotL * Math.cos(angleB)),
                    dotRL, mTouchedBongPaint);
            angleB += 2 * Math.PI / 7;
        }

    }

    //绘制圆点消失
    private void drawDotDisappear(Canvas canvas, int color, float percent) {
        mTouchedBongPaint.setColor(color);
        mTouchedBongPaint.setAntiAlias(true);
        mTouchedBongPaint.setStyle(Paint.Style.FILL);
        double angleA = 0;
        double angleB = -Math.PI / 10;
        dotRS *= 1 - percent;
        mTouchedBongPaint.setAlpha((int) (255 * (1 - percent)));
        for (int i = 0; i < 7; i++) {
            mTouchedBongPaint.setColor(dotColors[i]);
            canvas.drawCircle((float) (rDotS * Math.sin(angleA)), (float) (rDotS * Math.cos(angleA)),
                    dotRS, mTouchedBongPaint);
            angleA += 2 * Math.PI / 7;
            canvas.drawCircle((float) (rDotL * Math.sin(angleB)), (float) (rDotL * Math.cos(angleB)),
                    dotRL, mTouchedBongPaint);
            angleB += 2 * Math.PI / 7;
        }
    }


    private double g2 = 1.41421;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        tabNum = getChildCount();
        for (int i = 0; i < tabNum; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        tabNum = getChildCount();
        for (int i = 0; i < tabNum; i++) {
            View child = getChildAt(i);
            child.layout((int) (div + (1 - scale * 1 / g2) * mRadius + i * (div + 2 * mRadius)), (int) (startY - scale * mRadius / g2), (int) (div + (1 + scale * 1 / g2) * mRadius + i * (div + 2 * mRadius)), (int) (startY + scale * mRadius / g2));
            int index = i % childrenRects.length;
            child.getHitRect(childrenRects[index]);
        }
    }

    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                try {
                    if (!animating) {
                        updateDrop(position, positionOffset, positionOffsetPixels);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                viewPagerState = state;
            }
        });
    }

    float lastCurrentTime = 0;

    private void updateDrop(int position, float positionOffset, int positionOffsetPixels) {
        if (animator != null)
            animator.cancel();
        if ((position + positionOffset) - currentPos > 0) {
            direction = true;
        } else if ((position + positionOffset) - currentPos < 0) {
            direction = false;
        }

        if (direction)
            toPos = currentPos + 1;
        else
            toPos = currentPos - 1;

        if ((position + positionOffset) == currentPos) {
            toPos = currentPos;
            return;
        }

        startColor = roundColors[(currentPos) % 4];
        endColor = roundColors[(currentPos + (direction ? 1 : -1)) % 4];
        startX = div + mRadius + (currentPos) * (div + 2 * mRadius);
        distance = direction ? ((2 * mRadius + div) + -mRadius) : (-(2 * mRadius + div) + mRadius);
        mCurrentTime = position + positionOffset - (int) (position + positionOffset);
        if (!direction) {
            mCurrentTime = 1 - mCurrentTime;
        }
//        Log.e(TAG, "updateDrop###" + "position: " + position + ", positionOffset: " + positionOffset + ", currentPos: " + currentPos + ", toPos: " + toPos);
//        Log.e("ouyang", "updateDrop## currentTime: " + mCurrentTime + ", lastCurrentTime: " + lastCurrentTime);
        if (viewPagerState == ViewPager.SCROLL_STATE_SETTLING) {
            if (lastCurrentTime < 0.1) {
                mCurrentTime = 0;
            } else if (lastCurrentTime > 0.9) {
                mCurrentTime = 1;
            }
        }

        lastCurrentTime = mCurrentTime;

        invalidate();
    }

    class XPoint {
        public float x;
        public float y;
        public float mc;
        public PointF bottom;
        public PointF top;

        public XPoint(float x, float y, float mc) {
            this.x = x;
            this.y = y;
            this.mc = mc;
            if (bottom == null)
                bottom = new PointF();
            if (top == null)
                top = new PointF();
            bottom.y = y + mc;
            top.y = y - mc;
            bottom.x = x;
            top.x = x;
        }

        public void setMc(float mc) {
            this.mc = mc;
            bottom.y = y + mc;
            top.y = y - mc;
        }

        public void setY(float y) {
            this.y = y;
            bottom.y = y + mc;
            top.y = y - mc;
        }

        public void setX(float x) {
            this.x = x;
            bottom.x = x;
            top.x = x;
        }

        @Override
        public String toString() {
            return "XPoint{" +
                    "x=" + x +
                    ", y=" + y +
                    ", mc=" + mc +
                    ", bottom=" + bottom +
                    ", top=" + top +
                    '}';
        }
    }

    class YPoint {
        public float x;
        public float y;
        public float mc;
        public PointF left;
        public PointF right;

        public YPoint(float x, float y, float mc) {
            this.x = x;
            this.y = y;
            this.mc = mc;
            if (left == null)
                left = new PointF();
            if (right == null)
                right = new PointF();
            right.x = x + mc;
            left.x = x - mc;
            left.y = y;
            right.y = y;
        }

        public void setMc(float mc) {
            this.mc = mc;
            right.x = x + mc;
            left.x = x - mc;
        }

        public void setX(float x) {
            this.x = x;
            right.x = x + mc;
            left.x = x - mc;
        }

        public void setY(float y) {
            this.y = y;
            left.y = y;
            right.y = y;
        }

        public void setLeftX(float leftX) {
            left.x = leftX;
            x = (left.x + right.x) / 2;
        }

        @Override
        public String toString() {
            return "YPoint{" +
                    "x=" + x +
                    ", y=" + y +
                    ", mc=" + mc +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }

    public int dip2px(Context mContext, float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    float[][] f;
    float[] result;
    int[] colors = new int[4];

    public int getCurrentColor(float percent, int startColor, int endColor) {
        f[0][0] = (startColor & 0xff0000) >> 16;
        f[0][1] = (startColor & 0x00ff00) >> 8;
        f[0][2] = (startColor & 0x0000ff);
        f[1][0] = (endColor & 0xff0000) >> 16;
        f[1][1] = (endColor & 0x00ff00) >> 8;
        f[1][2] = (endColor & 0x0000ff);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < f.length; j++) {
                if (f.length == 1 || percent == j / (f.length - 1f)) {
                    result = f[j];
                } else {
                    if (percent > j / (f.length - 1f) && percent < (j + 1f) / (f.length - 1)) {
                        result[i] = f[j][i] - (f[j][i] - f[j + 1][i]) * (percent - j / (f.length - 1f)) * (f.length - 1f);
                    }
                }
            }
        }
        return Color.rgb((int) result[0], (int) result[1], (int) result[2]);
    }

    private float getCurrentValue(float original) {
        Interpolator interpolator = new AccelerateInterpolator();
        return interpolator.getInterpolation(original);
    }

    /**
     * return: -1 means do not click any image view, else return i, the click view position
     */
    private int getChildIndexByTouchPoint(float x, float y) {
        for (int i = 0; i < childrenRects.length; i++) {
            Rect item = childrenRects[i];
            if (item != null) {
                if (x > item.left - 10 && x < item.right + 10) {
                    return i;
                }
            }
        }
        return -1;
    }

}


