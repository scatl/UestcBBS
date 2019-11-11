package com.scatl.uestcbbs.custom.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/13 18:45
 */
public class ReportDialog {
    public static void show(final Context context, final String type, final int id) {
        final View report_view = LayoutInflater.from(context).inflate(R.layout.dialog_report, new RelativeLayout(context));
        final AppCompatEditText editText = report_view.findViewById(R.id.dialog_report_text);
        final RadioGroup radioGroup = report_view.findViewById(R.id.dialog_report_radio_group);

        final AlertDialog report_dialog = new AlertDialog.Builder(context)
                .setPositiveButton("确认举报", null)
                .setNegativeButton("取消", null)
                .setView(report_view)
                .setTitle("举报")
                .create();
        report_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button p = report_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RadioButton radioButton = report_view.findViewById(radioGroup.getCheckedRadioButtonId());
                        String s = radioButton.getText().toString();
                        String msg = "[" + s + "]" + editText.getText().toString();
                        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
                        Map<String, String> map = new HashMap<>();
                        map.put("idType", type);
                        map.put("id", id + "");
                        map.put("message", msg);
                        map.put("accessToken", sharedPreferences.getString("token", ""));
                        map.put("accessSecret", sharedPreferences.getString("secret", ""));
                        map.put("apphash", CommonUtil.getAppHashValue());
                        HttpRequestUtil.post(Constants.Api.REPORT_USER, map, new OnHttpRequest() {
                            @Override
                            public void onRequestError(Call call, Exception e, int id) {
                                ToastUtil.showToast(context, "提交失败，请重试");
                            }

                            @Override
                            public void onRequestInProgress(float progress, long total, int id) {

                            }

                            @Override
                            public void onRequestSuccess(String response, int id) {
                                JSONObject jsonObject = JSONObject.parseObject(response);
                                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
                                if (rs == 1) {
                                    report_dialog.dismiss();
                                }
                                ToastUtil.showToast(context, jsonObject.getString("errcode"));
                            }
                        });
                    }
                });
            }
        });
        report_dialog.show();
    }
}
