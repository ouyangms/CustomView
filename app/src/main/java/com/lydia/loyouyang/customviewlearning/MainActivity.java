package com.lydia.loyouyang.customviewlearning;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lydia.loyouyang.customviewlearning.customview.dropIndicator.DropIndicatorActivity;
import com.lydia.loyouyang.customviewlearning.customview.musicloading.MusicLoadingActivity;
import com.lydia.loyouyang.customviewlearning.customview.musicloading.MusicLoadingView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button dropIndicator = findViewById(R.id.drop_indication);
        Button musicLoading = findViewById(R.id.music_loading);
        dropIndicator.setOnClickListener(this);
        musicLoading.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.drop_indication:
                startActivity(DropIndicatorActivity.class);
                break;
            case R.id.music_loading:
                startActivity(MusicLoadingActivity.class);
                break;
            default:
                    break;
        }
    }

    private void startActivity(Class cls){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        startActivity(intent);
    }
}
