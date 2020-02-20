package com.exoplayer.demo;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ExoPlayerActivity extends AppCompatActivity {

    public static final String VIDEO_URL = "https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv";

    private SimpleExoPlayerView simpleExoPlayerView;

    // step1. 创建一个默认的TrackSelector
    // 创建带宽
    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    // 创建轨道选择工厂
    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

    // 创建轨道选择器实例
    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

    //step2. 创建播放器
    SimpleExoPlayer player;
    private int resumeWindow;
    private long resumePosition;

    //创建socket连接
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.104:8081");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);

        ButterKnife.bind(this);

        socketConn();
        mSocket.on("borcast", onLogin);

        initExoplayer();
    }

    private void initExoplayer() {
        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView);

        //step2. 创建播放器
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        // 创建加载数据的工厂
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yourApplicationName"), (TransferListener) bandwidthMeter);

        // 创建解析数据的工厂
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // 传入Uri、加载数据的工厂、解析数据的工厂，就能创建出MediaSource
        Uri mp4VideoUri = Uri.parse(VIDEO_URL);
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri,
                dataSourceFactory, extractorsFactory, null, null);
        // Prepare
        player.prepare(videoSource);

        simpleExoPlayerView.setPlayer(player);
    }

    @OnClick({R.id.play_btn,R.id.pause_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                startPlayer();
                break;
            case R.id.pause_btn:
                pausePlayer();
                break;
        }
    }

    private void startPlayer() {
        player.setPlayWhenReady(true);
    }

    private void pausePlayer(){
        player.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (player != null&& player.getCurrentPosition()>0) {
            player.setPlayWhenReady(true);
            player.seekTo(resumePosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null && player.getPlayWhenReady()) {
            resumeWindow = player.getCurrentWindowIndex();
            resumePosition = Math.max(0, player.getContentPosition());
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放播放器
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        mSocket.off("borcast", onLogin);

        //取消连接Server
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
    }

    //连接到Server
    private void socketConn() {
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.connect();
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... objects) {
            final String data = (String) objects[0];

            try {
                JSONObject jsonObject = new JSONObject(data);

//                final VideoInfo videoInfo = new VideoInfo();
//                videoInfo.setState(jsonObject.getInt("state"));
//                videoInfo.setVideoUrl(jsonObject.getString("video_url"));
//                videoInfo.setSwitchEffect(jsonObject.getInt("switch_effect"));
//                videoInfo.setPlayDuration(jsonObject.getInt("play_duration"));
//                videoInfo.setIsLoop(jsonObject.getInt("is_loop"));

                int playerState = jsonObject.getInt("state");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(playerState == 2){
                            pausePlayer();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Failed to connected...", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
}
