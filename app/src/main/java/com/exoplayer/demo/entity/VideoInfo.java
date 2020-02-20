package com.exoplayer.demo.entity;

import java.io.Serializable;

public class VideoInfo implements Serializable {

    private int state;
    private String videoUrl;
    private int playDuration;
    private int switchEffect;
    private int isLoop;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public int getSwitchEffect() {
        return switchEffect;
    }

    public void setSwitchEffect(int switchEffect) {
        this.switchEffect = switchEffect;
    }

    public int getIsLoop() {
        return isLoop;
    }

    public void setIsLoop(int isLoop) {
        this.isLoop = isLoop;
    }
}
