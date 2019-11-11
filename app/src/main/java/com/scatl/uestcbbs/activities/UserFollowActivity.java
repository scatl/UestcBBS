package com.scatl.uestcbbs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.userdetail.UserFollowAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.UserFollowBean;
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

public class UserFollowActivity extends BaseActivity {

    private static final String TAG = "UserFollowActivity";

    private SmartRefreshLayout refreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private UserFollowAdapter userFollowAdapter;

    public static final String TYPE_FOLLOW = "follow";
    public static final String TYPE_FOLLOWED = "followed";

    private String type;
    private int user_id;
    private String name;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_follow);

        init();
    }

    /**
     * author: TanLei
     * description:
     */
    private void init() {

        type = getIntent().getStringExtra(Constants.Key.TYPE);
        user_id = getIntent().getIntExtra(Constants.Key.USER_ID, Integer.MAX_VALUE);
        name = getIntent().getStringExtra(Constants.Key.USER_NAME);

        recyclerView = findViewById(R.id.user_follow_rv);
        coordinatorLayout = findViewById(R.id.user_follow_coorlayout);
        refreshLayout = findViewById(R.id.user_follow_refresh);
        toolbar = findViewById(R.id.user_follow_toolbar);
        if (user_id == SharePrefUtil.getId(this)) {
            toolbar.setTitle(type.equals(TYPE_FOLLOW) ? "我关注的人" : "我的粉丝");
        } else {
            toolbar.setTitle(type.equals(TYPE_FOLLOW) ? name + "关注的人" : name + "的粉丝");
        }
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userFollowAdapter = new UserFollowAdapter(this, R.layout.item_user_follow);
        userFollowAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(userFollowAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        setOnRefreshListener();
        setOnItemClick();
        getFollowData(true);

    }

    /**
     * author: TanLei
     * description:
     */
    private void setOnItemClick() {
        userFollowAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(UserFollowActivity.this, UserDetailActivity.class);
                intent.putExtra(Constants.Key.USER_ID, userFollowAdapter.getData().get(position).uid);
                startActivity(intent);

            }
        });
    }

    /**
     * author: TanLei
     * description: 获取列表数据
     */
    private void getFollowData(final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", user_id + "");
        map.put("page", page + "");
        map.put("pageSize", "100");
        map.put("orderBy", "dateline");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());
        map.put("type", type);

        HttpRequestUtil.post(Constants.Api.GET_FOLLOW_LIST, map, new OnHttpRequest() {
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

    private void resolveData(String data, boolean refresh) {
        refreshLayout.finishRefresh();

        JSONObject jsonObject = JSONObject.parseObject(data);
        UserFollowBean userFollowBean = JSON.parseObject(data, new TypeReference<UserFollowBean>(){});
        List<UserFollowBean.ListBean> listBeans = JSON.parseObject(jsonObject.getJSONArray("list").toString(),
                new TypeReference<List<UserFollowBean.ListBean>>(){});

        if (userFollowBean.rs == 1) {
            if (listBeans != null && listBeans.size() != 0) {
                userFollowAdapter.addUserFollowData(listBeans, refresh);
            }

            if (page == 1) recyclerView.scheduleLayoutAnimation();
            if (userFollowBean.has_next == 1) refreshLayout.finishLoadMore(true);
            if (userFollowBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();

        } else {
            String err = jsonObject.getString("errcode");
            ToastUtil.showSnackBar(coordinatorLayout, err);
        }
    }


    /**
     * author: sca_tl
     * description: 上拉下拉刷新监听
     */
    private void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {

            //刷新，则获取第一页数据，并将page=1
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getFollowData(true);
            }

            //加载更多，page递增
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getFollowData(false);
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
