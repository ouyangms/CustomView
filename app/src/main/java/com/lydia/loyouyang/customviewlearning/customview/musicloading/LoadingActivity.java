package com.lydia.loyouyang.customviewlearning.customview.musicloading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateInterpolator;

import com.lydia.loyouyang.customviewlearning.R;

@SuppressLint("Registered")
public class LoadingActivity extends Activity {
    private static final float MAX_VALUE = 1.0f;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        final LoadingView view = findViewById(R.id.loading_view);
        //方式１
//        ValueAnimator va = ValueAnimator.ofFloat(0 ,MAX_VALUE);
//        va.setDuration(5000);
//        va.setInterpolator(new AccelerateInterpolator());
//        va.setRepeatCount(ValueAnimator.INFINITE);
//        va.setRepeatMode(ValueAnimator.RESTART);
//        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float currentValue = (float) animation.getAnimatedValue();
//                String message  = "已加载" + (int)(currentValue / MAX_VALUE * 100) + "%";
//                view.setCurrentValue(currentValue, message);
//            }
//        });
//        view.setMaxValue(MAX_VALUE);
//        va.start();

        //方式２
        view.startAnimator(MAX_VALUE, 6000, 0, ValueAnimator.RESTART, ValueAnimator.INFINITE, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                String message  = "已加载" + (int)(currentValue / MAX_VALUE * 100) + "%";
                view.setCurrentValue(currentValue, message);
            }
        }, new AnimatorListenerAdapter(){
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
            }
        });

    }

}
