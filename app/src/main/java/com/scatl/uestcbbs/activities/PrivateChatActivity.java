package com.scatl.uestcbbs.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.PrivateChatAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.glide.Glide4Engine;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.dialogs.ReportDialog;
import com.scatl.uestcbbs.entities.PrivateChatBean;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.interfaces.OnPermission;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.utils.FileUtil;
import com.scatl.uestcbbs.utils.ImageUtil;
import com.scatl.uestcbbs.utils.PermissionUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class PrivateChatActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PrivateChatActivity";

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private PrivateChatAdapter privateChatAdapter;
    private Toolbar toolbar;
    private Button send_msg_btn;
    private ImageView add_img_btn;
    private EditText msg_edittext;

    private int his_id;
    private String from_name;
    private List<PrivateChatBean.PrivateChatListBean> privateChatListBeanList;

    public static final String SEND_TYPE_TEXT = "text";
    public static final String SEND_TYPE_IMG = "image";
    public static final int REQUEST_SEND_IMG = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        init();
    }

    /**
     * author: sca_tl
     * description: 初始化
     */
    private void init() {
        his_id = getIntent().getIntExtra(Constants.Key.USER_ID, Integer.MAX_VALUE);
        from_name = getIntent().getStringExtra(Constants.Key.USER_NAME);
        privateChatListBeanList = new ArrayList<>();

        coordinatorLayout = findViewById(R.id.private_chat_coorlayout);
        send_msg_btn = findViewById(R.id.private_chat_send_btn);
        send_msg_btn.setOnClickListener(this);
        add_img_btn = findViewById(R.id.private_chat_add_photo);
        add_img_btn.setOnClickListener(this);
        CommonUtil.setVectorColor(this, add_img_btn, R.drawable.ic_photo, R.color.colorPrimary);
        msg_edittext = findViewById(R.id.private_chat_edittext);

        toolbar = findViewById(R.id.private_chat_toolbar);
        toolbar.setTitle(from_name);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.private_chat_rv);
        privateChatAdapter = new PrivateChatAdapter(this, R.layout.item_private_chat);
        privateChatAdapter.setHasStableIds(true);
        MyLinearLayoutManger myLinearLayoutManger = new MyLinearLayoutManger(this);
        myLinearLayoutManger.setStackFromEnd(true);
        recyclerView.setLayoutManager(myLinearLayoutManger);
        recyclerView.setAdapter(privateChatAdapter);

        setOnItemClickListener();
        getChatData();
    }

    /**
     * author: sca_tl
     * description:
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.private_chat_add_photo:
                addImage();
                break;

            case R.id.private_chat_send_btn:
                if (msg_edittext.getText().toString().length() == 0) {
                    ToastUtil.showSnackBar(coordinatorLayout, "请输入内容");
                } else {
                    sendPrivateMsg(SEND_TYPE_TEXT, msg_edittext.getText().toString());
                }
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description:
     */
    public void setOnItemClickListener() {
        privateChatAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_private_chat_his_img:
                    case R.id.item_private_chat_mine_img:
                        List<String> urls = new ArrayList<>();
                        urls.add(privateChatAdapter.getData().get(position).content);
                        ImageUtil.showImages(PrivateChatActivity.this, (ImageView) view, urls, 0);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * author: sca_tl
     * description: 获取消息记录
     */
    private void getChatData() {
        Map<String, String> map= new HashMap<>();

        JSONObject pmlist = new JSONObject();
        JSONObject body = new JSONObject();
        JSONArray pmInfos = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startTime", "0");
        jsonObject.put("stopTime", "0");
        jsonObject.put("cacheCount", "30");
        jsonObject.put("pmLimit", "1000");
        jsonObject.put("fromUid", his_id);

        pmInfos.add(jsonObject);
        body.put("pmInfos", pmInfos);

        pmlist.put("body", body);

        map.put("pmlist", pmlist.toJSONString());
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_PRIVATE_CHAT_MSG_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(coordinatorLayout, getResources().getString(R.string.request_error));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                resolveData(response);
            }

        });
    }

    /**
     * author: sca_tl
     * description: 解析数据
     */
    private void resolveData(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。

        if (rs == 1) {
            JSONObject jsonObject1 = jsonObject.getJSONObject("body");
            JSONArray jsonArray = jsonObject1.getJSONArray("pmList");
            if (jsonArray != null && jsonArray.size() != 0) {

                //对方信息
                PrivateChatBean.PrivateChatHisBean privateChatHisBean = new PrivateChatBean.PrivateChatHisBean();
                privateChatHisBean.fromUid = jsonArray.getJSONObject(0).getIntValue("fromUid");
                privateChatHisBean.name = jsonArray.getJSONObject(0).getString("name");
                privateChatHisBean.avatar = jsonArray.getJSONObject(0).getString("avatar");

                JSONArray jsonArray1 = jsonArray.getJSONObject(0).getJSONArray("msgList");
                if (jsonArray1 != null && jsonArray1.size() != 0) {
                    for (int i = 0; i < jsonArray1.size(); i ++) {
                        PrivateChatBean.PrivateChatListBean privateChatListBean = new PrivateChatBean.PrivateChatListBean();
                        privateChatListBean.sender = jsonArray1.getJSONObject(i).getIntValue("sender");
                        privateChatListBean.content = jsonArray1.getJSONObject(i).getString("content");
                        privateChatListBean.mid = jsonArray1.getJSONObject(i).getIntValue("mid");
                        privateChatListBean.time = jsonArray1.getJSONObject(i).getString("time");
                        privateChatListBean.type = jsonArray1.getJSONObject(i).getString("type");

                        privateChatListBeanList.add(privateChatListBean);
                    }
                }

                privateChatAdapter.addMsgData(privateChatListBeanList, privateChatHisBean);
                recyclerView.scrollToPosition(privateChatListBeanList.size() - 1);
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_NEW_PRIVATE_COUNT_SUBTRACT));
                EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.READ_PRIVATE_CHAT_MSG));
            }
        }
    }

    /**
     * author: sca_tl
     * description: 发送消息
     */
    private void sendPrivateMsg(final String type, final String content) {
        Map<String, String> map= new HashMap<>();

        JSONObject msg = new JSONObject();
        msg.put("content", content);
        msg.put("type", type);

        JSONObject json = new JSONObject();
        json.put("msg", msg);
        json.put("action", "send");
        json.put("plid", "0");
        json.put("pmid", "0");
        json.put("toUid", his_id);

        map.put("json", json.toJSONString());
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.SEND_PRIVATE_MSG, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(coordinatorLayout, "发送失败：" + e.getMessage());
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。

                if (rs == 1) {
                    insertMsg(type, content);
                }

                String err = jsonObject.getString("errcode");
                ToastUtil.showSnackBar(coordinatorLayout, err);
            }
        });
    }

    /**
     * author: sca_tl
     * description: 发送消息成功后刷新列表
     */
    private void insertMsg(String type, String content) {
        PrivateChatBean.PrivateChatListBean privateChatListBean = new PrivateChatBean.PrivateChatListBean();
        privateChatListBean.type = type;
        privateChatListBean.sender = SharePrefUtil.getId(PrivateChatActivity.this);
        privateChatListBean.time = String.valueOf(System.currentTimeMillis());
        privateChatListBean.content = content;

        msg_edittext.setText("");

        privateChatAdapter.addData(privateChatListBean);
        recyclerView.scrollToPosition(privateChatListBeanList.size() - 1);
    }

    /**
     * author: sca_tl
     * description: 添加图片
     */
    private void addImage() {
        PermissionUtil.requestPermission(PrivateChatActivity.this, new OnPermission() {
            @Override
            public void onGranted() {
                Matisse.from(PrivateChatActivity.this)
                        .choose(MimeType.of(MimeType.PNG, MimeType.JPEG))
                        .countable(true)
                        .maxSelectable(1)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .imageEngine(new Glide4Engine())
                        .forResult(REQUEST_SEND_IMG);
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

    /**
     * author: sca_tl
     * description: 压缩图片
     */
    private void compressImg(String img_path) {
        Luban.with(this)
                .load(img_path)
                .ignoreBy(1)
                .setTargetDir(getExternalFilesDir(Constants.AppFilePath.TEMP_PATH).getAbsolutePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() { }

                    @Override
                    public void onSuccess(File file) {
                        uploadImg(file, SEND_TYPE_IMG);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showSnackBar(coordinatorLayout, "压缩图片失败，请重试");
                    }
                }).launch();
    }

    /**
     * author: sca_tl
     * description: 上传图片
     */
    private void uploadImg(final File file, final String type) {
        Map<String, String> map = new HashMap<>();
        map.put("type", "image");
        map.put("module", "pm");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        List<File> files = new ArrayList<>();
        files.add(file);

        HttpRequestUtil.postFormFile(Constants.Api.UPLOAD_IMG, files, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(coordinatorLayout,"发送失败：" + e.getMessage());
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs");

                if (rs == 1) {
                    JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("attachment");
                    if (jsonArray != null && jsonArray.size() == 1) {
                        String img_url = jsonArray.getJSONObject(0).getString("urlName");
                        sendPrivateMsg(type, img_url);  //上传图片成功后发送消息
                        FileUtil.deleteFile(file);
                    } else {
                        ToastUtil.showSnackBar(coordinatorLayout, "上传图片失败，可能是不支持的图片类型");
                    }
                } else {
                    ToastUtil.showSnackBar(coordinatorLayout,
                            "发送失败：" + jsonObject.getString("errcode"));
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_private_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_private_chat_user_detail:  //用户主页
                Intent intent = new Intent(this, UserDetailActivity.class);
                intent.putExtra(Constants.Key.USER_ID, his_id);
                startActivity(intent);
                break;

            case R.id.menu_private_chat_add_to_black:  //拉黑
                Intent intent1 = new Intent(this, BlackUserActivity.class);
                intent1.putExtra(Constants.Key.USER_ID, his_id);
                startActivity(intent1);
                break;

            case R.id.menu_private_chat_report_user:  //举报
                ReportDialog.show(this, Constants.Api.REPORT_TYPE_USER, his_id);
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择图片后即发送图片
        if (requestCode == REQUEST_SEND_IMG && resultCode == RESULT_OK){
            final List<String> path = Matisse.obtainPathResult(data);
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("发送图片")
                    .setMessage("确定要发送这张图片吗？")
                    .setPositiveButton("发送", null)
                    .setNegativeButton("取消", null)
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    p.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            ToastUtil.showSnackBar(coordinatorLayout, "发送中，请不要重复操作");
                            compressImg(path.get(0));
                        }
                    });
                }
            });
            dialog.show();

        }

    }

}
