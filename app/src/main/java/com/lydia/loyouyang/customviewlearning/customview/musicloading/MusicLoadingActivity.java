package com.lydia.loyouyang.customviewlearning.customview.musicloading;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateInterpolator;

import com.lydia.loyouyang.customviewlearning.R;

@SuppressLint("Registered")
public class MusicLoadingActivity extends Activity {
    private static final float MAX_VALUE = 1.0f;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_loding);
        final MusicLoadingView view = findViewById(R.id.music_loading_view);
        ValueAnimator va = ValueAnimator.ofFloat(0 ,MAX_VALUE);
        va.setDuration(5000);
        va.setInterpolator(new AccelerateInterpolator());
        va.setRepeatCount(2);
        va.setRepeatMode(ObjectAnimator.RESTART);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                String message  = "已加载" + (int)(currentValue / MAX_VALUE * 100) + "%";
                view.setCurrentValue(currentValue, message);
            }
        });
        view.setMaxValue(MAX_VALUE);
        va.start();
    }
}
