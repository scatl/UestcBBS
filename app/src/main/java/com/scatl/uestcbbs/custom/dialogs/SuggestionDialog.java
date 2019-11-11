package com.scatl.uestcbbs.custom.dialogs;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import okhttp3.Call;

public class SuggestionDialog extends BaseDialogFragment implements View.OnClickListener {
    private TextView cancel_text, submit_text, hint_text;
    private AppCompatEditText content;

    @Override
    protected int setLayoutResourceId() { return R.layout.dialog_suggestion; }

    @Override
    protected void init() {
        setCancelable(false);
        content = view.findViewById(R.id.dialog_suggestion_content);
        cancel_text = view.findViewById(R.id.dialog_suggestion_cancel);
        cancel_text.setOnClickListener(this);
        submit_text = view.findViewById(R.id.dialog_suggestion_reply);
        submit_text.setOnClickListener(this);
        hint_text = view.findViewById(R.id.dialog_suggestion_hint);
        hint_text.setText(getString(R.string.suggestion_privacy_note));
        CommonUtil.showSoftKeyboard(mActivity, content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_suggestion_cancel:
                onCancel();
                break;

            case R.id.dialog_suggestion_reply:
                submitSuggestion();
                break;

            default:
                break;
        }
    }

    private void submitSuggestion() {
        if (TextUtils.isEmpty(content.getText().toString())){
            ToastUtil.showToast(mActivity, "请输入内容");
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content.getText().toString());

            HttpRequestUtil.postString(Constants.Api.SUBMIT_SUGGESTION, jsonObject.toString(), new OnHttpRequest() {
                @Override
                public void onRequestError(Call call, Exception e, int id) {
                    ToastUtil.showToast(mActivity, "提交失败");
                }

                @Override
                public void onRequestInProgress(float progress, long total, int id) { }

                @Override
                public void onRequestSuccess(String response, int id) {
                    if (JSON.isValidObject(response)) {
                        JSONObject jsonObject1 = JSONObject.parseObject(response);
                        int code = jsonObject1.getIntValue("returnCode");
                        String msg = jsonObject1.getString("returnMsg");
                        if (code == 1) { dismiss(); }
                        ToastUtil.showToast(mActivity, msg);
                    }
                }
            });
        }
    }

    /**
     * author: sca_tl
     * description: 退出编辑警告
     */
    private void onCancel() {
        if (! TextUtils.isEmpty(content.getText().toString())) {
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
                            dialog.dismiss();
                            dismiss();
                        }
                    });
                }
            });
            dialog.show();
        } else {
            dismiss();
        }
    }

}
