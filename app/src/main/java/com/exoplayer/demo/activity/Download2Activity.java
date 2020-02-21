package com.exoplayer.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.exoplayer.demo.R;
import com.exoplayer.demo.util.DownloadUtil;
import com.exoplayer.demo.util.SDPathHelper;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Download2Activity extends AppCompatActivity {

    public static final String VIDEO_URL = "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv";

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
                DownloadUtil.downloadFile1ByURLConnection(VIDEO_URL, SDPathHelper.VIDEO_PATH);
                break;
            case R.id.download_btn2:
                DownloadUtil.downloadFileByCustom(VIDEO_URL, SDPathHelper.VIDEO_PATH,Download2Activity.this);
                break;
            case R.id.download_btn3:
                DownloadUtil.downloadFileByOkhttp3(VIDEO_URL, SDPathHelper.VIDEO_PATH);
                break;
        }
    }
}
