package com.exoplayer.demo.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class DownloadUtil {
    public static void downloadFile1ByURLConnection (String url, String path) {
        try{
            //下载路径，如果路径无效了，可换成你的下载路径
//            String url = "http://c.qijingonline.com/test.mkv";
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            final long startTime = System.currentTimeMillis();
            Log.i("DOWNLOAD","startTime="+startTime);
            //下载函数
            String filename=url.substring(url.lastIndexOf("/") + 1);
            //获取文件名
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            File file1 = new File(path);
            if(!file1.exists()){
                file1.mkdirs();
            }
            //把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(path+"/"+filename);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            do{
                //循环读取
                int numread = is.read(buf);
                if (numread == -1)
                {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
                //更新进度条
            } while (true);

            Log.i("DOWNLOAD","download success");
            Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));

            is.close();
        } catch (Exception ex) {
            Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
        }
    }

    public static void downloadFileByCustom(String url, String path,Context context){
        //下载路径，如果路径无效了，可换成你的下载路径
//        String url = "http://c.qijingonline.com/test.mkv";
        //创建下载任务,downloadUrl就是下载链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //指定下载路径和下载文件名
        request.setDestinationInExternalPublicDir("", url.substring(url.lastIndexOf("/") + 1));
        //获取下载管理器
        DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载
        downloadManager.enqueue(request);
    }

    public static void downloadFileByOkhttp3(String url, String mSDCardPath){
        //下载路径，如果路径无效了，可换成你的下载路径
//        final String url = "http://c.qijingonline.com/test.mkv";
        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD","startTime="+startTime);

        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD","download failed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
//                    String mSDCardPath= Environment.getExternalStorageDirectory().getAbsolutePath();
                    File dest = new File(mSDCardPath,   url.substring(url.lastIndexOf("/") + 1));
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i("DOWNLOAD","download success");
                    Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DOWNLOAD","download failed");
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }

                }
            }
        });
    }
}
