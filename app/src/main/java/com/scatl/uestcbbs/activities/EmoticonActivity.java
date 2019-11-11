package com.scatl.uestcbbs.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.EmoticonAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.EmoticonBean;
import com.scatl.uestcbbs.interfaces.OnHttpFileRequest;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.FileUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import java.io.File;
import java.util.List;

import okhttp3.Call;

public class EmoticonActivity extends BaseActivity {

    private static final String TAG = "EmoticonActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EmoticonAdapter emoticonAdapter;

    public static final String BTN_DOWNLOAD = "download";
    public static final String BTN_DOWNLOADING = "downloading";
    public static final String BTN_DOWNLOADED = "downloaded";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoticon);

        init();
    }

    private void init() {
        toolbar = findViewById(R.id.emoticon_toolbar);
        toolbar.setTitle("表情包管理");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.emotion_rv);

        emoticonAdapter = new EmoticonAdapter(this, R.layout.item_emoticon_download);
        emoticonAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(emoticonAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        setOnclickListener();
        setData();
    }

    private void setOnclickListener() {
        emoticonAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_emoticon_btn:
                        Button button = ((Button) view);
                        switch ((String) button.getTag()) {
                            case BTN_DOWNLOAD://未加载，点击下载
                                downloadEmoticon(emoticonAdapter.getData().get(position).link, emoticonAdapter.getData().get(position).code, button);
                                break;

                            case BTN_DOWNLOADED://下载完成，点击删除
                                deleteEmoticon(emoticonAdapter.getData().get(position).code, button);
                                break;

                            default:
                                break;
                        }
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void setData() {
        HttpRequestUtil.get(Constants.Api.EMOTICON_LIST, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                List<EmoticonBean> emoticonBeanList = JSON.parseObject(jsonObject.get("list").toString(),
                        new TypeReference<List<EmoticonBean>>() {});
                emoticonAdapter.addData(emoticonBeanList);
                recyclerView.scheduleLayoutAnimation();
            }
        });
    }

    private void downloadEmoticon(String url, final String emoticon_code, final Button button) {
        final String path = getExternalFilesDir(Constants.AppFilePath.EMOTICON_PATH).getAbsolutePath();
        final String name = url.substring(url.lastIndexOf("/"));
        HttpRequestUtil.getFile(url, path, name, new OnHttpFileRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) {
                button.setText("下载中");
                button.setTag(BTN_DOWNLOADING);
            }

            @Override
            public void onRequestSuccess(File response, int id) {
                //解压文件并删除压缩文件，添加到pref中
                SharePrefUtil.setDownloadedEmoticon(EmoticonActivity.this, emoticon_code, true);
                ToastUtil.showToast(EmoticonActivity.this, "下载完成");
                button.setTag(BTN_DOWNLOADED);
                button.setText("删除");
                FileUtil.unzipFile(path + "/" + name, path + "/" + emoticon_code, true);
            }
        });
    }

    private void deleteEmoticon(String emoticon_code, Button button) {
        //从pref删除
        SharePrefUtil.setDownloadedEmoticon(EmoticonActivity.this, emoticon_code, false);
        FileUtil.deleteDir(new File(getExternalFilesDir(Constants.AppFilePath.EMOTICON_PATH).getAbsolutePath() + "/" + emoticon_code), true);
        ToastUtil.showToast(this, "删除成功");
        button.setText("下载");
        button.setTag(BTN_DOWNLOAD);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }




}

