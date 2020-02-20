package com.exoplayer.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick({R.id.video_btn_1,R.id.video_btn_2,R.id.video_btn_3})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.video_btn_1:
                intent = new Intent(MainActivity.this, ExoPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.video_btn_2:
                intent = new Intent(MainActivity.this, GoogleExoplayerActivity.class);
                startActivity(intent);
                break;
            case R.id.video_btn_3:
                intent = new Intent(MainActivity.this, GoogleExoplayerActivity.class);
                startActivity(intent);
                break;
        }
    }

}

