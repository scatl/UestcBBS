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
import com.scatl.uestcbbs.adapters.notification.PrivateMsgAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.PrivateMsgBean;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class PrivateMsgActivity extends BaseActivity {

    private static final String TAG = "PrivateMsgActivity";

    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;
    private PrivateMsgAdapter privateMsgAdapter;
    private SmartRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private TextView request_error;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_msg);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        coordinatorLayout = findViewById(R.id.private_msg_coorlayout);
        recyclerView = findViewById(R.id.private_msg_rv);
        refreshLayout = findViewById(R.id.private_msg_refresh);
        request_error = findViewById(R.id.private_msg_error);

        toolbar = findViewById(R.id.private_msg_toolbar);
        toolbar.setTitle("私信");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        privateMsgAdapter = new PrivateMsgAdapter(this, R.layout.item_private_msg);
        privateMsgAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(privateMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, true);
        getPrivateMsgListData(true);
        setOnRefreshListener();
        setOnItemClick();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnItemClick() {
        privateMsgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(PrivateMsgActivity.this, PrivateChatActivity.class);
                intent.putExtra(Constants.Key.USER_ID, privateMsgAdapter.getData().get(position).toUserId);
                intent.putExtra(Constants.Key.USER_NAME, privateMsgAdapter.getData().get(position).toUserName);
                startActivity(intent);
            }
        });

        privateMsgAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_private_msg_user_icon:
                        Intent intent = new Intent(PrivateMsgActivity.this, UserDetailActivity.class);
                        intent.putExtra(Constants.Key.USER_ID, privateMsgAdapter.getData().get(position).toUserId);
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
    private void getPrivateMsgListData(final boolean refresh) {
        Map<String, String> map= new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", page);
        jsonObject.put("pageSize", 20);
        map.put("json", jsonObject.toString());
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_PRIVATE_MSG_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                refreshLayout.finishRefresh();
                ToastUtil.showSnackBar(coordinatorLayout, getResources().getString(R.string.request_error));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                refreshLayout.finishRefresh();
                resolveData(response, refresh);
            }
        });
    }

    /**
     * author: sca_tl
     * description: 解析短消息列表数据
     */
    private void resolveData(String data, boolean refresh) {

        JSONObject jsonObject = JSONObject.parseObject(data);
        PrivateMsgBean privateMsgBean = JSON.parseObject(data, new TypeReference<PrivateMsgBean>(){});
        PrivateMsgBean.BodyBean bodyBean = JSONObject.parseObject(jsonObject.getJSONObject("body").toString(),
                new TypeReference<PrivateMsgBean.BodyBean>(){});
        List<PrivateMsgBean.BodyBean.ListBean> listBeans = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("list");
        if (jsonArray != null) {
            listBeans = JSON.parseObject(jsonArray.toString(),
                    new TypeReference<List<PrivateMsgBean.BodyBean.ListBean>>(){});
        }

        if (jsonArray != null && privateMsgBean.rs == 1) {
            if (listBeans != null && listBeans.size() != 0) {
                privateMsgAdapter.addPrivateMsgData(listBeans, refresh);
            }
            if (bodyBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();
            if (bodyBean.has_next == 1) refreshLayout.finishLoadMore(true);
            if (page == 1) recyclerView.scheduleLayoutAnimation();
            request_error.setVisibility(jsonArray.size() == 0 ? View.VISIBLE : View.GONE);
        } else {
            refreshLayout.finishLoadMore(false);
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
                getPrivateMsgListData(true);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getPrivateMsgListData(false);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.READ_PRIVATE_CHAT_MSG) {
            getPrivateMsgListData(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (! EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
