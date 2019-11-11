package com.scatl.uestcbbs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.notification.SystemMsgAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.SystemMsgBean;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.interfaces.OnRefresh;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.RefreshUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class SystemMsgActivity extends BaseActivity {

    private static final String TAG = "SystemMsgActivity";

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SystemMsgAdapter systemMsgAdapter;
    private TextView request_error;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_msg);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        coordinatorLayout = findViewById(R.id.systen_msg_coorlayout);
        refreshLayout = findViewById(R.id.system_notification_refresh);
        recyclerView = findViewById(R.id.system_notification_rv);

        toolbar = findViewById(R.id.system_notification_toolbar);
        toolbar.setTitle("系统通知");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        request_error = findViewById(R.id.system_msg_error);

        systemMsgAdapter = new SystemMsgAdapter(this, R.layout.item_system_msg);
        systemMsgAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(systemMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, true);
        getSystemMsgData(true);
        setOnRefreshListener();
        setOnItemClick();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnItemClick() {

        systemMsgAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_system_msg_user_icon:
                        Intent intent = new Intent(SystemMsgActivity.this, UserDetailActivity.class);
                        intent.putExtra(Constants.Key.USER_ID, Integer.valueOf(systemMsgAdapter.getData().get(position).user_id));
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

    }

    /**
     * author: sca_tl
     * description: 获取短消息列表数据
     */
    private void getSystemMsgData(final boolean refresh) {
        Map<String, String> map= new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "20");
        map.put("type", "system");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_SYSTEM_NOTIFICATION, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                refreshLayout.finishRefresh();
                ToastUtil.showSnackBar(coordinatorLayout, getResources().getString(R.string.request_error));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                resolveData(response, refresh);
            }
        });
    }

    /**
     * author: sca_tl
     * description: 解析短消息列表数据
     */
    private void resolveData(String data, boolean refresh) {
        refreshLayout.finishRefresh();

        JSONObject jsonObject = JSONObject.parseObject(data);
        SystemMsgBean systemMsgBean = JSON.parseObject(data, new TypeReference<SystemMsgBean>(){});

        if (systemMsgBean.rs == 1) {
            List<SystemMsgBean.BodyBean.DataBean> dataBeans =
                    JSON.parseObject(jsonObject.getJSONObject("body").getJSONArray("data").toString(),
                            new TypeReference<List<SystemMsgBean.BodyBean.DataBean>>(){});

            JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i ++) {

                if (jsonArray.getJSONObject(i).containsKey("actions")) {
                    dataBeans.get(i).has_action = true;
                    dataBeans.get(i).actionBean =
                        JSON.parseObject(jsonArray.getJSONObject(i).getJSONArray("actions").toString(),
                        new TypeReference<List<SystemMsgBean.BodyBean.DataBean.ActionBean>>(){});
                } else {
                    dataBeans.get(i).has_action = false;
                }
            }

            systemMsgAdapter.addSystemMeMsgData(dataBeans, refresh);
            if (page == 1) recyclerView.scheduleLayoutAnimation();
            if (systemMsgBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();
            if (systemMsgBean.has_next == 1) refreshLayout.finishLoadMore(true);
            request_error.setVisibility(jsonArray.size() == 0 ? View.VISIBLE : View.GONE);
        } else {
            refreshLayout.finishLoadMore();
            request_error.setVisibility(View.VISIBLE);
            request_error.setText(jsonObject.getJSONObject("head").getString("errInfo"));
        }
    }

    /**
     * author: sca_tl
     * description: 刷新监听
     */
    private void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getSystemMsgData(true);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getSystemMsgData(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
