package com.exoplayer.demo.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.exoplayer.demo.R;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private Button btnStartDownLoad, btnStopDownLoad;
    private TextView tvDownLoadFileName, tvDownLoadSpeed, tvDownLoadDetail;
    private ProgressBar pBar;

    private String LIULISHUO_APK_URL = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk";
    private String downLoadFileName;
    private String rootPath = "/mnt/sdcard/TestDownLoadFiles";
    private String downLoadFilePath;//下载文件的保存路径（完整路径，带有文件名）
    private String downLoadFileDirectory;//下载文件的保存文件夹路径
    private int downLoadId;//分配的下载进程编号
    private boolean currentDownLoadState = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        //判断android版本号，弹出申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showConfirmAppPermissions();
        }

        FileDownloader.setup(this);
        bindViews();
        bindData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileDownloader.getImpl().pause(downLoadId);
    }

    private void bindViews() {
        mContext = DownloadActivity.this;
        btnStartDownLoad = findViewById(R.id.SecondDownLoad_btnStartDownLoad);
        btnStopDownLoad = findViewById(R.id.SecondDownLoad_btnStopDownLoad);
        tvDownLoadFileName = findViewById(R.id.SecondDownLoad_tvDownLoadFileName);
        tvDownLoadSpeed = findViewById(R.id.SecondDownLoad_tvDownLoadSpeed);
        tvDownLoadDetail = findViewById(R.id.SecondDownLoad_tvDownLoadDetail);
        pBar = findViewById(R.id.SecondDownLoad_pBarProgress);

        btnStartDownLoad.setOnClickListener(this);
        btnStopDownLoad.setOnClickListener(this);
    }

    private void bindData() {
        pBar.setMax(100);
        downLoadFileName = regGetFileNameWithoutFileFormat(LIULISHUO_APK_URL, "apk") + ".apk";
        tvDownLoadFileName.setText(downLoadFileName);
        downLoadFilePath = rootPath + File.separator + "1" + File.separator + downLoadFileName;
        downLoadFileDirectory = rootPath + File.separator + "1";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SecondDownLoad_btnStartDownLoad:{
                if (!getFileExist(downLoadFilePath)) {
                    currentDownLoadState = !currentDownLoadState;
                    if (currentDownLoadState) {
                        downLoadId = createDownloadTask(1).start();
                        btnStartDownLoad.setText("暂停");
                    } else {
                        FileDownloader.getImpl().pause(downLoadId);
                        btnStartDownLoad.setText("开始");
                    }
                } else {
                    Toast.makeText(mContext, "当前文件已存在，请勿重新下载，浪费资源！", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.SecondDownLoad_btnStopDownLoad:{
                new File(downLoadFilePath).delete();
                Toast.makeText(mContext, "【" + downLoadFilePath + "】删除成功", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    //判断当前文件是否存在，如存在给出提示，如不存在，开始下载
    public boolean getFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    //创建下载任务
    private BaseDownloadTask createDownloadTask(final int position) {
        final ViewHolder tag;
        final String url;
        boolean isDir = false;
        String path = "";

        switch (position) {
            case 1:
                url = LIULISHUO_APK_URL;
                tag = new ViewHolder(new WeakReference<>(this), pBar, tvDownLoadDetail, tvDownLoadSpeed, position);
                path = downLoadFilePath;
                tag.setFilenameTv(tvDownLoadFileName);
                break;
            default:
                tag = null;
                url = "";
                break;
        }

        //创建单任务下载
        BaseDownloadTask baseDownloadTask = FileDownloader.getImpl().create(url)
                .setPath(path, isDir)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setTag(tag)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                        ((ViewHolder) task.getTag()).updatePending(task);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        ((ViewHolder) task.getTag()).updateProgress(soFarBytes, totalBytes,
                                task.getSpeed());
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        ((ViewHolder) task.getTag()).updateError(e, task.getSpeed());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        ((ViewHolder) task.getTag()).updateConnected(etag, task.getFilename());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        ((ViewHolder) task.getTag()).updatePaused(task.getSpeed());
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        ((ViewHolder) task.getTag()).updateCompleted(task);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                        ((ViewHolder) task.getTag()).updateWarn();
                    }
                });

        return baseDownloadTask;
    }

    private class ViewHolder {
        private ProgressBar pb;
        private TextView detailTv;
        private TextView speedTv;
        private int position;
        private TextView filenameTv;

        private WeakReference<DownloadActivity> weakReferenceContext;
        public ViewHolder(WeakReference<DownloadActivity> weakReferenceContext,
                          final ProgressBar pb, final TextView detailTv, final TextView speedTv,
                          final int position) {
            this.weakReferenceContext = weakReferenceContext;
            this.pb = pb;
            this.detailTv = detailTv;
            this.position = position;
            this.speedTv = speedTv;
        }

        //设置文件名
        public void setFilenameTv(TextView filenameTv) {
            this.filenameTv = filenameTv;
        }
        //更新下载速度
        private void updateSpeed(int speed) {
            speedTv.setText(String.format("%dKB/s", speed));
        }
        //更新进度条
        public void updateProgress(final int sofar, final int total, final int speed) {
            if (total == -1) {
                // chunked transfer encoding data
                pb.setIndeterminate(true);
            } else {
                pb.setMax(total);
                pb.setProgress(sofar);
            }
            updateSpeed(speed);

            if (detailTv != null) {
                detailTv.setText(String.format("【sofar: %dM  total: %dM】", convertFileSize(sofar), convertFileSize(total)));
            }
        }

        public int convertFileSize(int size) {
            size = size / 1024 / 1024;
            return size;
        }

        //更新Pending时的UI（等待）
        public void updatePending(BaseDownloadTask task) {
            if (filenameTv != null) {
                filenameTv.setText(task.getFilename());
            }
        }
        //更新Pause时的UI（暂停）
        public void updatePaused(final int speed) {
            //toast(String.format("paused %d", position));
            updateSpeed(speed);
            pb.setIndeterminate(false);
        }
        //更新连接时的UI
        public void updateConnected(String etag, String filename) {
            if (filenameTv != null) {
                filenameTv.setText(filename);
            }
        }
        //更新出现警告时的UI
        public void updateWarn() {
            //toast(String.format("warn %d", position));
            pb.setIndeterminate(false);
        }
        //更新出现错误时的UI
        public void updateError(final Throwable ex, final int speed) {
            //toast(String.format("error %d %s", position, ex));
            updateSpeed(speed);
            pb.setIndeterminate(false);
            ex.printStackTrace();
        }
        //更新下载完成时的UI
        public void updateCompleted(final BaseDownloadTask task) {
            toast(String.format("下载完成！保存路径为：【%s】", task.getTargetFilePath()));
            if (detailTv != null) {
                detailTv.setText(String.format("【sofar: %dM  total: %dM】",
                        convertFileSize(task.getSmallFileSoFarBytes()), convertFileSize(task.getSmallFileTotalBytes())));
            }
            updateSpeed(task.getSpeed());
            pb.setIndeterminate(false);
            pb.setMax(task.getSmallFileTotalBytes());
            pb.setProgress(task.getSmallFileSoFarBytes());
        }


    }

    public void toast(final String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    // 正则检测url包含指定格式并获取文件名(不带文件格式后缀)
    public static String regGetFileNameWithoutFileFormat(String fileName, String fileFormat) {
        Pattern pat = Pattern.compile("(.android/)(.+?)(\\.)(" + fileFormat + ")");
        Matcher m = pat.matcher(fileName);
        if(m.find()){
            return m.group(2);
        }
        return null;
    }

    // 7.0动态申请权限
    public void showConfirmAppPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
