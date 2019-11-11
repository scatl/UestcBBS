package com.scatl.uestcbbs.custom.dialogs;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.entities.LoginBean;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.services.HeartMsgService;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.ServiceUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.TextUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class LoginDialog extends BaseDialogFragment implements View.OnClickListener {

    private AppCompatEditText user_name_et, user_psw_et;
    private TextView login_hint;
    private Button login_btn, register_btn;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.dialog_login;
    }

    @Override
    protected void init() {
        user_psw_et = view.findViewById(R.id.dialog_login_user_psw);
        login_hint = view.findViewById(R.id.dialog_login_hint);
        login_btn = view.findViewById(R.id.dialog_login_btn);
        login_btn.setOnClickListener(this);
        register_btn = view.findViewById(R.id.dialog_register_btn);
        register_btn.setOnClickListener(this);
        user_name_et = view.findViewById(R.id.dialog_login_user_name);
        CommonUtil.showSoftKeyboard(mActivity, user_name_et);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_login_btn:
                login();
                break;

            case R.id.dialog_register_btn:
                CommonUtil.openBrowser(mActivity, Constants.Api.REGISTER_URL);
                CommonUtil.hideSoftKeyboard(mActivity, user_psw_et);
                dismiss();
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description:
     */
    private void login() {
        Map<String, String> map= new HashMap<>();
        map.put("username", TextUtil.isEditableNull(user_name_et.getText()) ? "" : user_name_et.getText().toString());
        map.put("password", TextUtil.isEditableNull(user_psw_et.getText()) ? "" : user_psw_et.getText().toString());
        HttpRequestUtil.post(Constants.Api.LOGIN_URL, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                login_hint.setText(mActivity.getResources().getString(R.string.login_error, e.getMessage()));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {

                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
                if (rs == 1) {
                    CommonUtil.hideSoftKeyboard(mActivity, user_psw_et);
                    dismiss();

                    LoginBean loginBean = JSON.parseObject(jsonObject.toString(), new TypeReference<LoginBean>(){});
                    SharePrefUtil.setLogin(mActivity, true, loginBean);

                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.LOGIN_SUCCEED));

                    //开启消息提醒服务
                    if (! ServiceUtil.isServiceRunning(mActivity, HeartMsgService.serviceName)) {
                        Intent intent1 = new Intent(mActivity, HeartMsgService.class);
                        mActivity.startService(intent1);
                    }

                    ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), getString(R.string.login_welcome, loginBean.user_name));

                } else if (rs == 0) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("head");
                    String error = jsonObject1.getString("errInfo");
                    login_hint.setText(mActivity.getResources().getString(R.string.login_error, error));
                }
            }
        });
    }
}
