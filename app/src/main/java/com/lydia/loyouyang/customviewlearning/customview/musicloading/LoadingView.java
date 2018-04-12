package com.lydia.loyouyang.customviewlearning.customview.musicloading;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lydia.loyouyang.customviewlearning.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class LoadingView extends View {
    private Paint mViewFramePaint = new Paint();
    private Paint mViewContentPaint = new Paint();
    private Paint mBitmapPaint = new Paint();
    private Paint mMessagePaint = new Paint();

    //外框颜色　进度条颜色　内容文字颜色
    private int frameColor;
    private int contentColor;
    private int messageColor;

    //外框的半径及长度　默认值
    private int radius;
    private int length;
    private static final int DEFAULT_RADIUS = 70;
    private static final int DEFAULT_LENGTH = 600;

    private Point mCenter = new Point();

    //当前进度值
    private float currentValue = 0;
    //总进度值
    private float maxValue = 1;
    //内容文字
    private String message = "";


    //总长度
    private float mProgressWidth;
    //目前进度条长度的x值
    private float mCurrentContentX;

    // 中等振幅大小
    private static final int MIDDLE_AMPLITUDE = 13;
    // 不同类型之间的振幅差距
    private static final int AMPLITUDE_DISPARITY = 5;
    // 叶子飘动一个周期所花的时间
    private static final long LEAF_FLOAT_TIME = 1500;
    // 叶子旋转一周需要的时间
    private static final long LEAF_ROTATE_TIME = 2000;

    // 中等振幅大小
    private int mMiddleAmplitude = MIDDLE_AMPLITUDE;
    // 振幅差
    private int mAmplitudeDisparity = AMPLITUDE_DISPARITY;

    // 叶子飘动一个周期所花的时间
    private long mLeafFloatTime = LEAF_FLOAT_TIME;
    // 叶子旋转一周需要的时间
    private long mLeafRotateTime = LEAF_ROTATE_TIME;

    // 用于产生叶子信息
    private LeafFactory mLeafFactory;
    // 产生出的叶子信息
    private List<Leaf> mLeafInfos;
    // 用于控制随机增加的时间不抱团
    private int mAddTime;

    private BitmapDrawable mSmallImage;
    private BitmapDrawable mMideleImage;
    private BitmapDrawable mLargeImage;
    private static final int DEFAULT_SMALL_IMAGE = R.drawable.small_leaf;
    private static final int DEFAULT_MIDDLE_IMAGE = R.drawable.middle_leaf;
    private static final int DEFAULT_LARGE_IMAGE = R.drawable.large_leaf;

    private static final int DEFAULT_VIEW_WIDTH = 600;
    private static final int DEFAULT_VIEW_HEIGHT = 800;



    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        radius = px2dp(array.getDimension(R.styleable.LoadingView_loading_radius, DEFAULT_RADIUS));
        length = px2dp(array.getDimension(R.styleable.LoadingView_loading_length, DEFAULT_LENGTH));
        mSmallImage = (BitmapDrawable) array.getDrawable(R.styleable.LoadingView_small_image);
        if (mSmallImage == null){
            mSmallImage = (BitmapDrawable) context.getResources().getDrawable(DEFAULT_SMALL_IMAGE);
        }
        mMideleImage = (BitmapDrawable) array.getDrawable(R.styleable.LoadingView_middle_image);
        if (mMideleImage == null){
            mMideleImage = (BitmapDrawable) context.getResources().getDrawable(DEFAULT_MIDDLE_IMAGE);
        }
        mLargeImage = (BitmapDrawable) array.getDrawable(R.styleable.LoadingView_large_image);
        if (mLargeImage == null){
            mLargeImage = (BitmapDrawable) context.getResources().getDrawable(DEFAULT_LARGE_IMAGE);
        }
        frameColor = array.getColor(R.styleable.LoadingView_loading_frame_color, Color.GRAY);
        contentColor = array.getColor(R.styleable.LoadingView_loading_content_color, Color.RED);
        messageColor = array.getColor(R.styleable.LoadingView_loading_message_color, Color.GREEN);
        array.recycle();
        init();
        mLeafFactory = new LeafFactory();
        mLeafInfos = mLeafFactory.generateLeafs();
    }


    private void init(){
        mViewFramePaint.setColor(frameColor);
        mViewFramePaint.setStyle(Paint.Style.FILL);
        mViewFramePaint.setAntiAlias(true);

        mViewContentPaint.setColor(contentColor);
        mViewContentPaint.setStyle(Paint.Style.FILL);
        mViewContentPaint.setAntiAlias(true);

        mMessagePaint.setColor(messageColor);
        mMessagePaint.setStyle(Paint.Style.STROKE);
        mMessagePaint.setStrokeWidth(2f);
        mMessagePaint.setTextAlign(Paint.Align.CENTER);
        mMessagePaint.setFakeBoldText(true);
        mMessagePaint.setLetterSpacing(0.3f);
        mMessagePaint.setTextSize(40);
        mMessagePaint.setAntiAlias(true);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            widthSize = DEFAULT_VIEW_WIDTH;
            heightSize = DEFAULT_VIEW_HEIGHT;
        }else if (widthMode == MeasureSpec.AT_MOST){
            widthSize = DEFAULT_VIEW_WIDTH;
        }else if (heightMode == MeasureSpec.AT_MOST){
            heightSize = DEFAULT_VIEW_HEIGHT;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenter.x = w / 2;
        mCenter.y = h / 2;
        mProgressWidth = length + 2 * radius;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mCenter.x, mCenter.y);
        drawViewFrame(canvas);
        drawViewContent(canvas);
        drawLeafs(canvas);
        drawMessage(canvas);
    }

    //绘制外框
    private void drawViewFrame(Canvas canvas){
        canvas.save();
        Path framePath = new Path();
        framePath.addArc(-length / 2 - radius, -radius, -length / 2 + radius, radius, 90, 180);
        framePath.addArc(length / 2 - radius, -radius, length / 2 + radius, radius, -90, 180);
        framePath.addRect(-length / 2, -radius, length / 2, radius, Path.Direction.CW);
        canvas.drawPath(framePath, mViewFramePaint);
        canvas.restore();
    }

    //绘制加载条进度
    private void drawViewContent(Canvas canvas){
        canvas.save();
        Path resultPath;
        float l = currentValue / maxValue * mProgressWidth;
        mCurrentContentX = l - mProgressWidth / 2;
        if (l < 0){
            l = 0;
        }
        if (l > mProgressWidth){
            l = mProgressWidth;
        }
        if (l >= 0 && l <= radius){
            resultPath = mode0(l);
        }else if (l > radius && l <= length + radius){
            resultPath = mode1(l);
        }else{
            resultPath = mode2(l);
        }
        canvas.drawPath(resultPath, mViewContentPaint);
        canvas.restore();
    }

    private Path mode0(float l){
        Path path = new Path();
        float angle = (float) (Math.asin((radius - l) / radius)* 360f / (2f * Math.PI));
        path.addArc(-length / 2 - radius, -radius, -length / 2 + radius, radius, 90 + angle, 180 - 2 * angle);
        return path;
    }

    private Path mode1(float l){
        Path path = new Path();
        path.addPath(mode0(radius));
        path.addRect(-length / 2, -radius, -length / 2 + l - radius, radius, Path.Direction.CW);
        return path;
    }

    private Path mode2(float l){
        Path path = new Path();
        path.addPath(mode1(length + radius));
        Path path1 = new Path();
        path1.addArc(length / 2 - radius, -radius, length / 2 + radius, radius, -90, 180);
        Path path2 = new Path();
        float angle = (float) (Math.acos((l - (length + radius)) / radius)* 360f / (2f * Math.PI));
        path2.addArc(length / 2 - radius, -radius, length / 2 + radius, radius, -angle, 2 * angle);
        path1.op(path2, Path.Op.DIFFERENCE);
        path.addPath(path1);
        return path;
    }

    //绘制文字内容
    private void drawMessage(Canvas canvas){
        canvas.save();
        Path path = new Path();
        path.moveTo(-length / 2, 0);
        path.lineTo(length / 2, 0);
        canvas.drawTextOnPath(message, path, 0, 10, mMessagePaint);
        canvas.restore();
    }


    //绘制叶子
    private void drawLeafs(Canvas canvas){
        mLeafRotateTime = mLeafRotateTime <= 0 ? LEAF_ROTATE_TIME : mLeafRotateTime;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mLeafInfos.size(); i++) {
            Leaf leaf = mLeafInfos.get(i);
            if (currentTime > leaf.startTime && leaf.startTime != 0) {
                // 绘制叶子－－根据叶子的类型和当前时间得出叶子的（x，y）
                getLeafLocation(leaf, currentTime);
                if (mCurrentContentX > leaf.x) {
                    continue;
                }
                // 根据时间计算旋转角度
                canvas.save();
                // 通过Matrix控制叶子旋转
                Matrix matrix = new Matrix();
                float transX = leaf.x;
                float transY = leaf.y;
                matrix.postTranslate(transX, transY);
                // 通过时间关联旋转角度，则可以直接通过修改LEAF_ROTATE_TIME调节叶子旋转快慢
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                        / (float) mLeafRotateTime;
                int angle = (int) (rotateFraction * 360);
                // 根据叶子旋转方向确定叶子旋转角度
                int rotate = leaf.rotateDirection == 0 ? angle + leaf.rotateAngle : -angle
                        + leaf.rotateAngle;
                matrix.postRotate(rotate, transX
                        + leaf.bitmap.getWidth() / 2, transY + leaf.bitmap.getHeight() / 2);
                canvas.drawBitmap(leaf.bitmap, matrix, mBitmapPaint);
                canvas.restore();
            }
        }
    }

    private void getLeafLocation(Leaf leaf, long currentTime) {
        long intervalTime = currentTime - leaf.startTime;
        mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
        if (intervalTime < 0) {
            return;
        } else if (intervalTime > mLeafFloatTime) {
            leaf.startTime = System.currentTimeMillis()
                    + new Random().nextInt((int) mLeafFloatTime);
        }

        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = (int) (mProgressWidth / 2 - mProgressWidth * fraction) - leaf.bitmap.getWidth();
        leaf.y = getLocationY(leaf);
    }

    // 通过叶子信息获取当前叶子的Y值
    private int getLocationY(Leaf leaf) {
        // y = A(wx+Q)+h
        float w = (float) ((float) 2 * Math.PI / mProgressWidth);
        float a = mMiddleAmplitude;
        switch (leaf.type) {
            case LITTLE:
                // 小振幅 ＝ 中等振幅 － 振幅差
                a = mMiddleAmplitude - mAmplitudeDisparity;
                break;
            case MIDDLE:
                a = mMiddleAmplitude;
                break;
            case BIG:
                // 小振幅 ＝ 中等振幅 + 振幅差
                a = mMiddleAmplitude + mAmplitudeDisparity;
                break;
            default:
                break;
        }
        return (int) (a * Math.sin(w * leaf.x)) - radius / 2;
    }


    private int px2dp(float pxValue){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public void setMaxValue(float maxValue){
        this.maxValue = maxValue;
    }

    public void setCurrentValue(float currentValue, String message) {
        this.currentValue = currentValue;
        this.message = message;
        invalidate();
    }


    private enum StartType {
        LITTLE, MIDDLE, BIG
    }

    private enum StartSize {
        SMALL, MIDDLE, LARGE
    }

    class Leaf{
        // 在绘制部分的位置
        float x, y;
        // 控制叶子飘动的幅度
        StartType type;
        //叶子大小
        StartSize size;

        //leaf bitmap
        Bitmap bitmap;
        // 旋转角度
        int rotateAngle;
        // 旋转方向--0代表顺时针，1代表逆时针
        int rotateDirection;
        // 起始时间(ms)
        long startTime;
    }

    private class LeafFactory {
        private static final int MAX_LEAFS = 8;
        Random random = new Random();

        // 生成一个叶子信息
        public Leaf generateLeaf() {
            Leaf leaf = new Leaf();
            int randomType = random.nextInt(100) % 3;
            // 随时类型－ 随机振幅
            StartType type = StartType.MIDDLE;
            switch (randomType) {
                case 0:
                    break;
                case 1:
                    type = StartType.LITTLE;
                    break;
                case 2:
                    type = StartType.BIG;
                    break;
                default:
                    break;
            }

            //随机叶子大小
            int randomSize = random.nextInt(50) % 3;
            switch (randomSize){
                case 0:
                    leaf.bitmap = mSmallImage.getBitmap();
                    break;
                case 1:
                    leaf.bitmap = mMideleImage.getBitmap();
                    break;
                case 2:
                default:
                    leaf.bitmap = mMideleImage.getBitmap();
                    break;
            }
            leaf.type = type;
            // 随机起始的旋转角度
            leaf.rotateAngle = random.nextInt(360);
            // 随机旋转方向（顺时针或逆时针）
            leaf.rotateDirection = random.nextInt(2);
            // 为了产生交错的感觉，让开始的时间有一定的随机性
            mLeafFloatTime = mLeafFloatTime <= 0 ? LEAF_FLOAT_TIME : mLeafFloatTime;
            mAddTime += random.nextInt((int) (mLeafFloatTime * 2));
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            return leaf;
        }

        // 根据最大叶子数产生叶子信息
        public List<Leaf> generateLeafs() {
            return generateLeafs(MAX_LEAFS);
        }

        // 根据传入的叶子数量产生叶子信息
        public List<Leaf> generateLeafs(int leafSize) {
            List<Leaf> leafs = new LinkedList<Leaf>();
            for (int i = 0; i < leafSize; i++) {
                leafs.add(generateLeaf());
            }
            return leafs;
        }
    }

    public void startAnimator(float maxValue, long duration, int delay, int repeatMode,
                              int repeatCount, ValueAnimator.AnimatorUpdateListener updateListener, Animator.AnimatorListener listener){
        setMaxValue(maxValue);
        ValueAnimator animator = ValueAnimator.ofFloat(maxValue);
        animator.setDuration(duration);
        animator.setRepeatMode(repeatMode);
        animator.setRepeatCount(repeatCount);
        animator.setStartDelay(delay);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.addUpdateListener(updateListener);
        animator.addListener(listener);
        animator.start();
    }

    public int getFrameColor() {
        return frameColor;
    }

    public int getContentColor() {
        return contentColor;
    }

    public int getMessageColor() {
        return messageColor;
    }

    public int getRadius() {
        return radius;
    }

    public int getLength() {
        return length;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public String getMessage() {
        return message;
    }

    public float getProgressWidth() {
        return mProgressWidth;
    }

    public float getCurrentContentX() {
        return mCurrentContentX;
    }

    public long getLeafFloatTime() {
        return mLeafFloatTime;
    }

    public long getLeafRotateTime() {
        return mLeafRotateTime;
    }

    public BitmapDrawable getSmallImage() {
        return mSmallImage;
    }

    public BitmapDrawable getMideleImage() {
        return mMideleImage;
    }

    public BitmapDrawable getLargeImage() {
        return mLargeImage;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
    }

    public void setMessageColor(int messageColor) {
        this.messageColor = messageColor;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLeafFloatTime(long leafFloatTime) {
        this.mLeafFloatTime = leafFloatTime;
    }

    public void setLeafRotateTime(long leafRotateTime) {
        this.mLeafRotateTime = leafRotateTime;
    }

    public void setSmallImage(BitmapDrawable smallImage) {
        this.mSmallImage = smallImage;
    }

    public void setMideleImage(BitmapDrawable middleImage) {
        this.mMideleImage = middleImage;
    }

    public void setLargeImage(BitmapDrawable largeImage) {
        this.mLargeImage = largeImage;
    }

}
