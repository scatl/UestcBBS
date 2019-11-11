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
import com.scatl.uestcbbs.adapters.notification.AtMeMsgAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.AtMeMsgBean;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.interfaces.OnRefresh;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.utils.RefreshUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtMeMsgActivity extends BaseActivity {

    private static final String TAG = "AtMeMsgActivity";

    private CoordinatorLayout coordinatorLayout;
    private SmartRefreshLayout refreshLayout;
    private AtMeMsgAdapter atMeMsgAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView error;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_me_msg);

        init();
    }

    /**
     * author: sca_tl
     * description: 初始化
     */
    private void init() {
        toolbar = findViewById(R.id.at_me_toolbar);
        toolbar.setTitle("提到我的");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = findViewById(R.id.at_me_coorlayout);
        refreshLayout = findViewById(R.id.at_me_refresh);
        error = findViewById(R.id.at_me_error);
        recyclerView = findViewById(R.id.at_me_rv);
        atMeMsgAdapter = new AtMeMsgAdapter(this, R.layout.item_at_me);
        atMeMsgAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(atMeMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, true);
        getAtMeMsgData(true);
        setOnRefreshListener();
        setOnItemClickListener();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnItemClickListener() {
        atMeMsgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(AtMeMsgActivity.this, PostDetailActivity.class);
                intent.putExtra(Constants.Key.TOPIC_ID, atMeMsgAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        atMeMsgAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_at_me_icon:
                        Intent intent = new Intent(AtMeMsgActivity.this, UserDetailActivity.class);
                        intent.putExtra(Constants.Key.TOPIC_ID, atMeMsgAdapter.getData().get(position).user_id);
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
     * description: 获取at数据
     */
    private void getAtMeMsgData(final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "30");
        map.put("type", "at");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_AT_ME_MESSAGE, map, new OnHttpRequest() {
            @Override
            public void onRequestError(okhttp3.Call call, Exception e, int id) {
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
     * description: 解析at数据
     */
    private void resolveData(String data, boolean refresh) {
        refreshLayout.finishRefresh();

        JSONObject jsonObject = JSONObject.parseObject(data);
        AtMeMsgBean atMeMsgBean = JSON.parseObject(data, new TypeReference<AtMeMsgBean>(){});
        List<AtMeMsgBean.BodyBean.DataBean> dataBeans = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("data");
        if (jsonArray != null) {
            dataBeans = JSON.parseObject(jsonArray.toString(),
                    new TypeReference<List<AtMeMsgBean.BodyBean.DataBean>>(){});
        }

        if (jsonArray != null && atMeMsgBean.rs == 1) {

            atMeMsgAdapter.addAtMeMsgData(dataBeans, refresh);
            if (page == 1) recyclerView.scheduleLayoutAnimation();
            if (atMeMsgBean.has_next == 1) refreshLayout.finishLoadMore(true);
            if (atMeMsgBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();
            error.setVisibility(jsonArray.size() == 0 ? View.VISIBLE : View.GONE);
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_NEW_AT_COUNT_ZERO));

        } else {
            refreshLayout.finishLoadMore(false);
            error.setVisibility(View.VISIBLE);
            error.setText(jsonObject.getJSONObject("head").getString("errInfo"));
        }
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnRefreshListener(){
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getAtMeMsgData(true);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getAtMeMsgData(false);
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
