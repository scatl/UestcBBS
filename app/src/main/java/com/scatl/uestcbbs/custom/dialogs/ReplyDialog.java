package com.scatl.uestcbbs.custom.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.DeviceConfig;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.AtUserListActivity;
import com.scatl.uestcbbs.adapters.SelectImageAdapter;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.emoticon.EmoticonPanelLayout;
import com.scatl.uestcbbs.custom.glide.Glide4Engine;
import com.scatl.uestcbbs.fragments.AtUserListFragment;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.interfaces.OnPermission;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.FileUtil;
import com.scatl.uestcbbs.utils.ImageUtil;
import com.scatl.uestcbbs.utils.PermissionUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
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

/**
 * author: sca_tl
 * description:
 * date: 2019/8/17 15:22
 */
public class ReplyDialog extends BaseDialogFragment implements View.OnClickListener {

    private TextView cancel, reply, word_count;
    private EditText content;
    private ImageView add_emotion, add_img, send_msg, at_friend;
    private RecyclerView recyclerView;
    private SelectImageAdapter selectImageAdapter;
    private ProgressDialog progressDialog;
    private EmoticonPanelLayout emotion_panel;

    private static final int SELECT_IMAGE_REQUEST = 10;
    private static final int AT_USER_REQUEST = 11;

    private int board_id;
    private int topic_id;
    private boolean is_quote;
    private int quote_id;
    private String user_name;

    private boolean send_success = false;

    private List<Integer> img_ids = new ArrayList<>();
    private List<String> img_urls = new ArrayList<>();

    @Override
    protected int setLayoutResourceId() {
        return R.layout.dialog_comment;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            board_id = bundle.getInt("board_id", Integer.MAX_VALUE);
            topic_id = bundle.getInt("topic_id", Integer.MAX_VALUE);
            quote_id = bundle.getInt("quote_id", Integer.MAX_VALUE);
            is_quote = bundle.getBoolean("is_quote", false);
            user_name = bundle.getString("user_name");
        }
    }

    @Override
     protected void init() {

        setCancelable(false);
        cancel = view.findViewById(R.id.dialog_comment_cancel);
        cancel.setOnClickListener(this);
        reply = view.findViewById(R.id.dialog_comment_reply);
        reply.setOnClickListener(this);
        content = view.findViewById(R.id.dialog_comment_content);
        content.setHint("回复给：" + user_name);
        CommonUtil.showSoftKeyboard(mActivity, content);

        word_count = view.findViewById(R.id.dialog_comment_word_count);
        word_count.setText(mActivity.getResources().getString(R.string.comment_word_count, 0, 0));

        add_emotion = view.findViewById(R.id.dialog_comment_emotion);
        CommonUtil.setVectorColor(mActivity, add_emotion, R.drawable.ic_emotion_send_msg, R.color.colorPrimary);
        //add_emotion.setOnClickListener(this);

        add_img = view.findViewById(R.id.dialog_comment_image);
        CommonUtil.setVectorColor(mActivity, add_img, R.drawable.ic_add_photo, R.color.colorPrimary);
        add_img.setOnClickListener(this);

        send_msg = view.findViewById(R.id.dialog_comment_send);
        CommonUtil.setVectorColor(mActivity, send_msg, R.drawable.ic_send, R.color.colorPrimary);
        send_msg.setOnClickListener(this);

        at_friend = view.findViewById(R.id.dialog_comment_at);
        CommonUtil.setVectorColor(mActivity, at_friend, R.drawable.ic_at, R.color.colorPrimary);
        at_friend.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.dialog_comment_rv);
        selectImageAdapter = new SelectImageAdapter(mActivity, R.layout.item_dialog_comment);
        selectImageAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager = new MyLinearLayoutManger(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(selectImageAdapter);

        emotion_panel = view.findViewById(R.id.dialog_comment_emoticon_panel);
        final LinearLayout p = view.findViewById(R.id.dialog_comment_parentview);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DeviceConfig.KEYBOARD_HEIGHT = CommonUtil.keyBoardHeight(mActivity);
                //ToastUtil.showToast(mActivity, DeviceConfig.KEYBOARD_HEIGHT + "=" + CommonUtil.px2dip(mActivity, DeviceConfig.KEYBOARD_HEIGHT));
                emotion_panel.bindEditText(content).bindEmotionBtn(add_emotion)
                        .bindParentView(p).init();
            }
        }, 500);


        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("发送消息");
        progressDialog.setCancelable(false);

        setOnItemClickListener();
        setOnWordCountListener();

    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnItemClickListener() {
        selectImageAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_dialog_comment_delete_img:
                        selectImageAdapter.delete(position);
                        word_count.setText(mActivity.getResources().getString(R.string.comment_word_count,
                                content.getText().toString().length(), selectImageAdapter.getData().size()));
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnWordCountListener() {
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                word_count.setText(mActivity.getResources().getString(R.string.comment_word_count,
                        content.getText().toString().length(), selectImageAdapter.getData().size()));
            }
        });
    }

    /**
     * author: sca_tl
     * description: 插入表情
     */
    private void insertEmotion(String emotion_path) {
        String emotion_name = emotion_path.substring(emotion_path.lastIndexOf("/") + 1).replace("_", ":").replace(".gif", "");
        SpannableString spannableString = new SpannableString(emotion_name);
        Drawable drawable = ImageUtil.bitmap2Drawable(BitmapFactory.decodeFile(emotion_path));
        drawable.setBounds(10, 10, 80, 80);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        spannableString.setSpan(imageSpan, 0, emotion_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.getText().insert(content.getSelectionStart(), spannableString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_comment_cancel:
                onCancel();
                break;

            case R.id.dialog_comment_reply: //发送消息
            case R.id.dialog_comment_send:
                progressDialog.show();
                if (selectImageAdapter.getData().size() == 0) { //没有图片
                    sendReplyMsg();
                } else {  //有图片
                    compressImgs();
                }

                break;

//            case R.id.dialog_comment_emotion:  //选择表情
//                //ToastUtil.showSnackBar(getView(), "开发中，敬请期待");
//                CommonUtil.hideSoftKeyboard(mActivity, content);
//                //emotion_panel.setVisibility(View.VISIBLE);
//                emotion_panel.bindEditText(content).bindEmotionBtn(add_emotion).init();
//                break;

            case R.id.dialog_comment_image:  //添加图片
                addImage();
                break;

            case R.id.dialog_comment_at:  //at列表
                Intent intent = new Intent(mActivity, AtUserListActivity.class);
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
    private void sendReplyMsg() {

        JSONObject json = new JSONObject();
        json.put("fid", board_id + "");
        json.put("tid", topic_id + "");

        if (is_quote) {
            json.put("isQuote", "1");
            json.put("replyId", quote_id + "");
        } else {
            json.put("isQuote", "0");
        }

        JSONArray jsonArray = new JSONArray();

        if (content.getText().toString().length() != 0) {
            JSONObject content_json = new JSONObject();
            content_json.put("type", "0");
            content_json.put("infor", content.getText().toString());
            jsonArray.add(content_json);
        }

        if (selectImageAdapter.getData().size() != 0) { //有图片
            json.put("aid", img_ids.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(" ", ""));

            for (int i = 0; i < selectImageAdapter.getData().size(); i ++) {
                JSONObject image_json = new JSONObject();
                image_json.put("type", "1");
                image_json.put("infor", img_urls.get(i));
                jsonArray.add(image_json);
            }
        }

        json.put("content", jsonArray.toJSONString());


        JSONObject body = new JSONObject();
        body.put("json", json);

        JSONObject json_ = new JSONObject();
        json_.put("body", body);

        Map<String, String> map = new HashMap<>();
        map.put("act", "reply");
        map.put("json", json_.toJSONString());
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.SEND_POST_AND_REPLY, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                ToastUtil.showSnackBar(getView(), "消息发送失败：" + e.getMessage());
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) {
                progressDialog.setMessage("正在发送消息...");
            }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs");
                if (rs == 1) {
                    send_success = true;
                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SEND_REPLY_SUCCEED));
                    CommonUtil.hideSoftKeyboard(mActivity, view);
                    dismiss();
                } else {
                    send_success = false;
                }

                progressDialog.dismiss();
                ToastUtil.showSnackBar(getView(), jsonObject.getString("errcode"));
            }
        });
    }

    /**
     * author: sca_tl
     * description: 添加图片
     */
    private void addImage() {
        if (selectImageAdapter.getData().size() >= 20) {
            final AlertDialog dialog = new AlertDialog.Builder(mActivity)
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
                            Matisse.from(mActivity)
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
            PermissionUtil.requestPermission(getActivity(), new OnPermission() {
                @Override
                public void onGranted() {
                    CommonUtil.hideSoftKeyboard(mActivity, content);
                    Matisse.from(mActivity)
                            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                            .countable(true)
                            .maxSelectable(20)
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .imageEngine(new Glide4Engine())
                            .forResult(SELECT_IMAGE_REQUEST);
                }

                @Override
                public void onRefused() {
                    ToastUtil.showSnackBar(getView(), getResources().getString(R.string.permission_request));
                }

                @Override
                public void onRefusedWithNoMoreRequest() {
                    ToastUtil.showSnackBar(getView(), getResources().getString(R.string.permission_refuse));
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
        Luban.with(mActivity)
                .load(selectImageAdapter.getData())
                .ignoreBy(1)
                .setTargetDir(mActivity.getExternalFilesDir(Constants.AppFilePath.TEMP_PATH).getAbsolutePath())
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        progressDialog.setMessage("正在压缩图片...");
                    }

                    @Override
                    public void onSuccess(File file) {
                        files.add(file);
                        //全部压缩成功
                        if (files.size() == selectImageAdapter.getData().size()) {
                            uploadImgs(files);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        ToastUtil.showSnackBar(getView(), "压缩图片失败，请重试：" + e.getMessage());
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
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.postFormFile(Constants.Api.UPLOAD_IMG, files, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(getView(), "图片上传失败：" + e.getMessage());
                progressDialog.dismiss();
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) {
                progressDialog.setMessage("正在上传图片...");
            }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs");

                if (rs == 1) {
                    JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("attachment");
                    //若服务器返回的图片数量和上传的图片数量相同，表示所有图片上传成功
                    if (jsonArray != null && jsonArray.size() == selectImageAdapter.getData().size()) {

                        for (int j = 0; j < jsonArray.size(); j ++) {
                            img_ids.add(jsonArray.getJSONObject(j).getIntValue("id"));
                            img_urls.add(jsonArray.getJSONObject(j).getString("urlName"));
                        }

                        sendReplyMsg(); //上传图片成功后发送消息

                        //删除压缩后的图片
                        for (int i = 0; i < files.size(); i ++) { FileUtil.deleteFile(files.get(i)); }
                    } else {
                        progressDialog.dismiss();
                        img_ids.clear();
                        img_urls.clear();
                        ToastUtil.showSnackBar(getView(), "上传图片失败，可能是不支持的图片类型，请重试");
                    }
                } else {
                    progressDialog.dismiss();
                    img_ids.clear();
                    img_urls.clear();
                    ToastUtil.showSnackBar(getView(), "上传失败：" + jsonObject.getString("errcode"));
                }
            }
        });
    }


    /**
     * author: sca_tl
     * description: 退出编辑警告
     */
    private void onCancel() {
        if (content.getText().toString().length() != 0 || selectImageAdapter.getData().size() != 0) {
            final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                    .setNegativeButton("确认退出", null)
                    .setPositiveButton("继续编辑", null )
                    .setTitle("退出编辑")
                    .setMessage("确认退出编辑吗？你将丢失已经编辑的内容")
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    n.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CommonUtil.hideSoftKeyboard(mActivity, content);
                            dialog.dismiss();
                            dismiss();
                        }
                    });
                }
            });
            dialog.show();
        } else {
            CommonUtil.hideSoftKeyboard(mActivity, content);
            dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.INSERT_EMOTION) {
            insertEmotion((String) baseEvent.eventData);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            final List<String> path = Matisse.obtainPathResult(data);
            selectImageAdapter.addData(path);
            recyclerView.smoothScrollToPosition(selectImageAdapter.getData().size() - 1);
            word_count.setText(mActivity.getResources().getString(R.string.comment_word_count,
                    content.getText().toString().length(), selectImageAdapter.getData().size()));

        }
        if (requestCode == AT_USER_REQUEST && resultCode == AtUserListFragment.AT_USER_RESULT && data != null) {
            content.requestFocus();
            content.getText().append(data.getStringExtra(Constants.Key.AT_USER));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (! EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
