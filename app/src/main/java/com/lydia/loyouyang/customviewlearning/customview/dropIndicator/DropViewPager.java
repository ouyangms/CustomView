package com.lydia.loyouyang.customviewlearning.customview.dropIndicator;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by loy.ouyang on 2018/4/10.
 */

public class DropViewPager extends ViewPager implements Touchable{
    private boolean touchable = true;

    public DropViewPager(Context context) {
        this(context, null);
    }

    public DropViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return touchable && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return touchable && super.onTouchEvent(ev);
    }
}
