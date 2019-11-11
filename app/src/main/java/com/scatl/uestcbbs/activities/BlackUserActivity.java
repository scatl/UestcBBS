package com.scatl.uestcbbs.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class BlackUserActivity extends BaseActivity {

    private static final String TAG = "BlackUserActivity";

    private SwitchCompat black_switch;
    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;

    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_user);

        init();

    }


    /**
     * author: TanLei
     * description:
     */
    private void init() {
        user_id = getIntent().getIntExtra(Constants.Key.USER_ID, Integer.MAX_VALUE);

        coordinatorLayout = findViewById(R.id.black_user_coorlayout);
        black_switch = findViewById(R.id.black_user_switch);
        relativeLayout = findViewById(R.id.black_user_rl);
        relativeLayout.setVisibility(View.GONE);
        toolbar = findViewById(R.id.black_user_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("黑名单操作");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUserData();

        black_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (! compoundButton.isPressed()) return;
                blackUser(b ? "black" : "delblack");
            }
        });
    }

    /**
     * author: TanLei
     * description: 获取用户数据
     */
    private void getUserData() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", user_id + "");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_USER_INFO, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(coordinatorLayout, getResources().getString(R.string.request_error));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。

                if (rs == 1) {

                    relativeLayout.setVisibility(View.VISIBLE);

                   int is_black = jsonObject.getIntValue("is_black");
                   black_switch.setChecked(is_black == 1);

                } else {
                    black_switch.setEnabled(false);
                    ToastUtil.showSnackBar(coordinatorLayout, jsonObject.getString("errcode"));
                }
            }
        });
    }

    /**
     * author: TanLei
     * description: 黑名单操作
     */
    private void blackUser(String type) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", user_id + "");
        map.put("type", type);
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.BLACK_USER, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                black_switch.setChecked(! black_switch.isChecked());
                ToastUtil.showSnackBar(coordinatorLayout, "操作失败");
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
                if (rs == 1) {
                    black_switch.setChecked(black_switch.isChecked());
                }
                ToastUtil.showSnackBar(coordinatorLayout, jsonObject.getString("errcode"));
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
