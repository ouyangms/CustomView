package com.lydia.loyouyang.customviewlearning.customview.musicloading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.lydia.loyouyang.customviewlearning.R;

public class MusicLoadingView extends View {
    private Paint mViewFramePaint = new Paint();
    private Paint mViewContentPaint = new Paint();
    private Paint mMessagePaint = new Paint();

    private int frameColor;
    private int contentColor;
    private int messageColor;

    private int radius;
    private int length;
    private String imageName;
    private static final int DEFAULT_RADIUS = 70;
    private static final int DEFAULT_LENGTH = 600;
    private static final String DEFAULT_IMAGE_NAME = "music.png";

    private int mWidth;
    private int mHeight;
    private Point mCenter = new Point();

    private float currentValue = 0;
    private float maxValue = 1;
    private String message = "";


    public MusicLoadingView(Context context) {
        this(context, null);
    }

    public MusicLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MusicLoadingView);
        radius = px2dp(array.getDimension(R.styleable.MusicLoadingView_loading_radius, DEFAULT_RADIUS));
        length = px2dp(array.getDimension(R.styleable.MusicLoadingView_loading_length, DEFAULT_LENGTH));
        imageName = array.getString(R.styleable.MusicLoadingView_loading_image_name);
        if (imageName == null || imageName.isEmpty()){
            imageName = DEFAULT_IMAGE_NAME;
        }
        frameColor = array.getColor(R.styleable.MusicLoadingView_loading_frame_color, Color.GRAY);
        contentColor = array.getColor(R.styleable.MusicLoadingView_loading_content_color, Color.RED);
        messageColor = array.getColor(R.styleable.MusicLoadingView_loading_message_color, Color.GREEN);
        array.recycle();
        init();
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
        mMessagePaint.setTextSize(40);
        mMessagePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenter.x = w / 2;
        mCenter.y = h / 2;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mCenter.x, mCenter.y);
        drawViewFrame(canvas);
        drawViewContent(canvas);
        drawMessage(canvas);
    }

    private void drawViewFrame(Canvas canvas){
        canvas.save();
        Path framePath = new Path();
        framePath.addArc(-length / 2 - radius, -radius, -length / 2 + radius, radius, 90, 180);
        framePath.addArc(length / 2 - radius, -radius, length / 2 + radius, radius, -90, 180);
        framePath.addRect(-length / 2, -radius, length / 2, radius, Path.Direction.CW);
        canvas.drawPath(framePath, mViewFramePaint);
        canvas.restore();
    }

    private void drawViewContent(Canvas canvas){
        canvas.save();
        Path resultPath;
        float maxLength = length + 2 * radius;
        float l = currentValue / maxValue * maxLength;
        if (l < 0){
            l = 0;
        }
        if (l > maxLength){
            l = maxLength;
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

    private void drawMessage(Canvas canvas){
        canvas.save();
        Path path = new Path();
        path.moveTo(-length / 2, 0);
        path.lineTo(length / 2, 0);
        canvas.drawTextOnPath(message, path, 0, 10, mMessagePaint);
        canvas.restore();
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

}
