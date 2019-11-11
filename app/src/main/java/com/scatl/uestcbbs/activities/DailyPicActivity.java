package com.scatl.uestcbbs.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.interfaces.OnPermission;
import com.scatl.uestcbbs.utils.PermissionUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.ToastUtil;

public class DailyPicActivity extends BaseActivity {

    private static final String TAG = "DailyPicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_pic);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ImageView pic_img = findViewById(R.id.daily_picture_img);
        final TextView copy_right_text = findViewById(R.id.daily_pic_copyright);
        final ImageView back = findViewById(R.id.daily_pic_back);
        final ImageView download = findViewById(R.id.daily_pic_download);

        Glide.with(this).load(SharePrefUtil.getDailyPicInfo(this).remote_url).into(pic_img);
        copy_right_text.setText(SharePrefUtil.getDailyPicInfo(this).copy_right);

        pic_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (copy_right_text.getVisibility() == View.VISIBLE){
                    copy_right_text.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                } else if (copy_right_text.getVisibility() == View.GONE){
                    copy_right_text.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                PermissionUtil.requestPermission(DailyPicActivity.this, new OnPermission() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(DailyPicActivity.this, "下载中，下拉通知栏查看下载进度",
                                Toast.LENGTH_SHORT).show();
                        String name = SharePrefUtil.getDailyPicInfo(DailyPicActivity.this).copy_right.replace("/", "_") + ".jpg";
                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(SharePrefUtil.getDailyPicInfo(DailyPicActivity.this).remote_url));
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE
                                | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        downloadManager.enqueue(request);
                    }

                    @Override
                    public void onRefused() {
                        ToastUtil.showSnackBar(getWindow().getDecorView(), getResources().getString(R.string.permission_request));
                    }

                    @Override
                    public void onRefusedWithNoMoreRequest() {
                        ToastUtil.showSnackBar(getWindow().getDecorView(), getResources().getString(R.string.permission_refuse));
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        });
    }
}
