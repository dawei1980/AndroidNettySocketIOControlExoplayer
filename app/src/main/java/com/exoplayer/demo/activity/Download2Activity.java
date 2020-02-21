package com.exoplayer.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.exoplayer.demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Download2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_2);

        ButterKnife.bind(this);
    }

    @OnClick({R.id.back_btn,R.id.download_btn1,R.id.download_btn2,R.id.download_btn3})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.download_btn1:

                break;
            case R.id.download_btn2:

                break;
            case R.id.download_btn3:

                break;
        }
    }
}
