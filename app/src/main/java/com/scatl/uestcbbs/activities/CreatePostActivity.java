package com.scatl.uestcbbs.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.DeviceConfig;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.custom.dialogs.SelectBoardAndCatDialog;
import com.scatl.uestcbbs.custom.emoticon.EmoticonPanelLayout;
import com.scatl.uestcbbs.custom.glide.Glide4Engine;
import com.scatl.uestcbbs.custom.posteditor.ContentEditor;
import com.scatl.uestcbbs.databases.PostDraftDataBase;
import com.scatl.uestcbbs.entities.PostDraftBean;
import com.scatl.uestcbbs.fragments.AtUserListFragment;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.interfaces.OnPermission;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.FileUtil;
import com.scatl.uestcbbs.utils.PermissionUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.TimeUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class CreatePostActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "CreatePostActivity";

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private TextView board_name;
    private ImageView add_emotion, at_friend, add_image, send_post;
    private AppCompatEditText title;
    private ProgressDialog progressDialog;
    private ContentEditor contentEditor;
    private EmoticonPanelLayout emoticonPanelLayout;

    //选择的板块和分类id
    private int current_board_id;
    private int current_filter_id;
    private String current_board_name;
    private String current_cat_name;
    private long current_time;
    private String current_title;
    private String current_content;

    //图片上传后的服务器返回的图片id和链接
    private List<Integer> img_ids = new ArrayList<>();
    private List<String> img_urls = new ArrayList<>();
    private int img_index = 0;

    private static final int SELECT_IMAGE_REQUEST = 10;
    private static final int AT_USER_REQUEST = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {

        countDownTimer.start();
        current_board_id = getIntent().getIntExtra("board_id", 0);
        current_filter_id = getIntent().getIntExtra("cat_id", 0);
        current_board_name = getIntent().getStringExtra("board_name") == null ? "" : getIntent().getStringExtra("board_name");
        current_cat_name = getIntent().getStringExtra("cat_name") == null ? "" : getIntent().getStringExtra("cat_name");
        current_title = getIntent().getStringExtra("title") == null ? "" : getIntent().getStringExtra("title");
        current_content = getIntent().getStringExtra("content") == null ? "" : getIntent().getStringExtra("content");
        current_time = getIntent().getLongExtra("time", TimeUtil.getLongMs());

        contentEditor = findViewById(R.id.create_post_content_editor);
        coordinatorLayout = findViewById(R.id.create_post_coorlayout);
        toolbar = findViewById(R.id.create_post_toolbar);
        toolbar.setTitle("发表帖子");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.create_post_title);
        title.setText(current_title);
        title.requestFocus();

        board_name = findViewById(R.id.create_post_board_name);
        board_name.setOnClickListener(this);
        board_name.setText(TextUtils.isEmpty(current_board_name) && TextUtils.isEmpty(current_cat_name) ? "点击选择板块" :
                current_board_name + "->" + current_cat_name);

        add_emotion = findViewById(R.id.create_post_emotion);
        CommonUtil.setVectorColor(this, add_emotion, R.drawable.ic_emotion_send_msg, R.color.colorPrimary);
        add_emotion.setOnClickListener(this);
        at_friend = findViewById(R.id.create_post_at);
        CommonUtil.setVectorColor(this, at_friend, R.drawable.ic_at, R.color.colorPrimary);
        at_friend.setOnClickListener(this);
        add_image = findViewById(R.id.create_post_image);
        CommonUtil.setVectorColor(this, add_image, R.drawable.ic_add_photo, R.color.colorPrimary);
        add_image.setOnClickListener(this);
        send_post = findViewById(R.id.create_post_send);
        CommonUtil.setVectorColor(this, send_post, R.drawable.ic_send, R.color.colorPrimary);
        send_post.setOnClickListener(this);

        emoticonPanelLayout = findViewById(R.id.create_post_emotion_panel);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DeviceConfig.KEYBOARD_HEIGHT = CommonUtil.keyBoardHeight(CreatePostActivity.this);
                emoticonPanelLayout.bindEditText(contentEditor)
                        .bindEmotionBtn(add_emotion)
                        .init();
            }
        }, 500);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("发表帖子");
        progressDialog.setCancelable(false);

        //若内容不为空，则说明是草稿，直接显示内容
        if (! TextUtils.isEmpty(current_content)) contentEditor.setEditorData(current_content);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_post_board_name:
                SelectBoardAndCatDialog boardSelectDialog = new SelectBoardAndCatDialog();
                boardSelectDialog.show(getSupportFragmentManager(), TimeUtil.getStringMs());
                break;

            case R.id.create_post_send:  //发送消息
                if (current_board_id == 0) {
                    ToastUtil.showSnackBar(coordinatorLayout, "请选择板块");
                } else if (TextUtils.isEmpty(title.getText())){
                    ToastUtil.showSnackBar(coordinatorLayout, "请输入标题");
                } else if (contentEditor.isEditorEmpty()) {
                    ToastUtil.showSnackBar(coordinatorLayout, "请输入帖子内容");
                } else if (contentEditor.getImgPathList().size() != 0){
                    progressDialog.show();
                    compressImgs();
                } else if (contentEditor.getImgPathList().size() == 0){
                    progressDialog.show();
                    sendPostMsg();
                }

                break;

//            case R.id.create_post_emotion:  //选择表情
//                ToastUtil.showSnackBar(coordinatorLayout, "开发中，敬请期待");
//                break;

            case R.id.create_post_image:  //添加图片
                addImage();
                break;

            case R.id.create_post_at:  //at列表
                Intent intent = new Intent(CreatePostActivity.this, AtUserListActivity.class);
                startActivityForResult(intent, AT_USER_REQUEST);
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description: 发送消息
     */
    private void sendPostMsg() {

        JSONObject json = new JSONObject();

        json.put("fid", current_board_id);
        json.put("typeId", current_filter_id);
        json.put("title", title.getText().toString());
        json.put("isQuote", 0);
        json.put("aid", img_ids.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(" ", ""));

        JSONArray jsonArray = new JSONArray();


        for (int i = 0; i < contentEditor.buildEditorData().size(); i ++) {
            JSONObject jsonObject = new JSONObject();
            if (contentEditor.buildEditorData().get(i).content_type == 0) {
                jsonObject.put("type", 0);
                jsonObject.put("infor", contentEditor.buildEditorData().get(i).inputStr);
                jsonArray.add(jsonObject);
            }

            if (contentEditor.buildEditorData().get(i).content_type == 1) {
                jsonObject.put("type", 1);
                jsonObject.put("infor", img_urls.get(img_index));
                jsonArray.add(jsonObject);
                img_index = img_index + 1;
            }
        }

        img_index = 0;

        json.put("content", jsonArray.toJSONString());

        JSONObject body = new JSONObject();
        body.put("json", json);

        JSONObject json_ = new JSONObject();
        json_.put("body", body);

        Map<String, String> map = new HashMap<>();
        map.put("act", "new");
        map.put("json", json_.toJSONString());
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.SEND_POST_AND_REPLY, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                ToastUtil.showSnackBar(coordinatorLayout, "帖子发送失败：" + e.getMessage());
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) {
                progressDialog.setMessage("正在发表帖子，请稍候...");
            }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs");
                progressDialog.dismiss();
                ToastUtil.showToast(CreatePostActivity.this, jsonObject.getString("errcode"));
                if (rs == 1) { finish();}
            }
        });
    }

    /**
     * author: sca_tl
     * description: 添加图片
     */
    private void addImage() {
        if (contentEditor.getImgPathList().size() >= 20) {
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("继续", null )
                    .setTitle("请确认")
                    .setMessage("上传大量图片建议使用网页端操作")
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    p.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Matisse.from(CreatePostActivity.this)
                                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                                    .countable(true)
                                    .maxSelectable(20)
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                    .imageEngine(new Glide4Engine())
                                    .forResult(SELECT_IMAGE_REQUEST);
                        }
                    });
                }
            });
            dialog.show();
        } else {
            PermissionUtil.requestPermission(this, new OnPermission() {
                @Override
                public void onGranted() {
                    Matisse.from(CreatePostActivity.this)
                            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                            .countable(true)
                            .maxSelectable(20)
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .imageEngine(new Glide4Engine())
                            .forResult(SELECT_IMAGE_REQUEST);
                }

                @Override
                public void onRefused() {
                    ToastUtil.showSnackBar(coordinatorLayout, getResources().getString(R.string.permission_request));
                }

                @Override
                public void onRefusedWithNoMoreRequest() {
                    ToastUtil.showSnackBar(coordinatorLayout, getResources().getString(R.string.permission_refuse));
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
    }

    /**
     * author: sca_tl
     * description: 压缩图片
     */
    private void compressImgs() {
        final List<File> files = new ArrayList<>();
        Luban.with(this)
                .load(contentEditor.getImgPathList())
                .ignoreBy(1)
                .setTargetDir(getExternalFilesDir(Constants.AppFilePath.TEMP_PATH).getAbsolutePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        progressDialog.setMessage("正在压缩图片...");
                    }

                    @Override
                    public void onSuccess(File file) {
                        files.add(file);
                        //全部压缩成功
                        if (files.size() == contentEditor.getImgPathList().size()) {
                            uploadImgs(files);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        ToastUtil.showSnackBar(coordinatorLayout, "压缩图片失败，请重试，" + e.getMessage());
                    }
                }).launch();
    }

    /**
     * author: sca_tl
     * description: 上传图片
     */
    private void uploadImgs(final List<File> files) {
        Map<String, String> map = new HashMap<>();
        map.put("module", "forum");
        map.put("type", "image");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.postFormFile(Constants.Api.UPLOAD_IMG, files, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(coordinatorLayout, "图片上传失败：" + e.getMessage());
                progressDialog.dismiss();
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) {
                progressDialog.setMessage("正在上传图片，请稍候...");
            }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs");

                if (rs == 1) {
                    JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("attachment");

                    if (jsonArray != null) {
                        //若服务器返回的图片数量和上传的图片数量相同，表示所有图片上传成功
                        if (jsonArray.size() == contentEditor.getImgPathList().size()) {
                            for (int j = 0; j < jsonArray.size(); j ++) {
                                img_ids.add(jsonArray.getJSONObject(j).getIntValue("id"));
                                img_urls.add(jsonArray.getJSONObject(j).getString("urlName"));
                            }

                            sendPostMsg(); //上传图片成功后发送消息

                            //删除压缩后的图片
                            for (int i = 0; i < files.size(); i ++) { FileUtil.deleteFile(files.get(i)); }

                        } else {
                            progressDialog.dismiss();
                            img_ids.clear();
                            img_urls.clear();
                            ToastUtil.showSnackBar(coordinatorLayout, "与服务器返回的图片数量不一致，请重新上传图片");
                        }

                    } else {
                        progressDialog.dismiss();
                        img_ids.clear();
                        img_urls.clear();
                        ToastUtil.showSnackBar(coordinatorLayout, "上传图片失败，可能是不支持的图片类型，请重试");
                    }
                } else {
                    progressDialog.dismiss();
                    img_ids.clear();
                    img_urls.clear();
                    ToastUtil.showSnackBar(coordinatorLayout, "上传图片失败：" + jsonObject.getString("errcode"));
                }
            }
        });
    }

    /**
     * author: sca_tl
     * description: 保存草稿
     */
    private void onSaveDraftData() {
        List<ContentEditor.EditData> dataList = contentEditor.buildEditorData();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < dataList.size(); i ++) {
            JSONObject content_json = new JSONObject();
            if (dataList.get(i).content_type == ContentEditor.CONTENT_TYPE_TEXT) {
                content_json.put("content_type", ContentEditor.CONTENT_TYPE_TEXT);
                content_json.put("content", dataList.get(i).inputStr);
            }
            if (dataList.get(i).content_type == ContentEditor.CONTENT_TYPE_IMAGE) {
                content_json.put("content_type", ContentEditor.CONTENT_TYPE_IMAGE);
                content_json.put("content", dataList.get(i).imagePath);
            }
            jsonArray.add(content_json);
        }

        PostDraftBean postDraftBean = new PostDraftBean();
        postDraftBean.board_id = current_board_id;
        postDraftBean.cat_id = current_filter_id;
        postDraftBean.title = title.getText().toString();
        postDraftBean.content = jsonArray.toJSONString();
        postDraftBean.board_name = current_board_name;
        postDraftBean.cat_name = current_cat_name;
        postDraftBean.time = current_time;

        PostDraftDataBase.saveEditorData(this, postDraftBean);

    }

    private CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long l) { }

        @Override
        public void onFinish() {
            onSaveDraftData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    countDownTimer.start(); }
            }, 1000);
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (countDownTimer != null) countDownTimer.cancel();
                if (contentEditor.isEditorEmpty())
                    PostDraftDataBase.deleteEditorData(this, current_time);
                else onSaveDraftData();

                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SELECTED_BOARD) {
            BaseEvent.BoardSelected boardSelected = (BaseEvent.BoardSelected)baseEvent.eventData;
            current_board_id = boardSelected.board_id;
            current_filter_id = boardSelected.cat_id;
            current_board_name = boardSelected.board_name;
            current_cat_name = boardSelected.cat_name;
            board_name.setText(String.valueOf(boardSelected.board_name + "->" + boardSelected.cat_name));
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.INSERT_EMOTION) {
            contentEditor.insertEmotion((String) baseEvent.eventData);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            final List<String> path = Matisse.obtainPathResult(data);
            for (int i = 0; i < path.size(); i ++) {
                contentEditor.insertImage(path.get(i), 1000);
            }

        }
        if (requestCode == AT_USER_REQUEST && resultCode == AtUserListFragment.AT_USER_RESULT && data != null) {
            contentEditor.insertText(data.getStringExtra(Constants.Key.AT_USER));
        }
    }

    @Override
    public void onBackPressed() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (contentEditor.isEditorEmpty())
            PostDraftDataBase.deleteEditorData(this, current_time);
        else {
            onSaveDraftData();
            ToastUtil.showToast(this, "已保存至草稿箱");
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (! EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (contentEditor.isEditorEmpty())
            PostDraftDataBase.deleteEditorData(this, current_time);
        else {
            onSaveDraftData();
            ToastUtil.showToast(this, "已保存至草稿箱");
        }
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

}
