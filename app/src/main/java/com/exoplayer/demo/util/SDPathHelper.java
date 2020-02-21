package com.exoplayer.demo.util;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;

/**
 * 设置在SD卡中存储数据库
 * Dawei Jiang
 * */
@SuppressLint("SdCardPath")
public class SDPathHelper {

    public static final String VIDEO_PATH = Environment.getExternalStorageDirectory()+"/"+"ExoplayerVideo/";
    static {
        while (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();

                break;
            }
        }
        File dbFolder = new File(VIDEO_PATH);
        // 目录不存在则自动创建目录
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
    }
}
