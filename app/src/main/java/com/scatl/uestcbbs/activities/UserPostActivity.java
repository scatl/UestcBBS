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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.userdetail.UserPostAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.entities.UserPostBean;
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

public class UserPostActivity extends BaseActivity {

    private static final String TAG = "UserPostActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private UserPostAdapter userPostAdapter;
    private TextView request_error;

    public static final String TYPE_USER_POST = "topic";
    public static final String TYPE_USER_REPLY = "reply";
    public static final String TYPE_USER_FAVORITE = "favorite";

    private String type;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        type = getIntent().getStringExtra(Constants.Key.TYPE);

        refreshLayout = findViewById(R.id.user_post_refresh);
        request_error = findViewById(R.id.user_post_error);
        coordinatorLayout = findViewById(R.id.user_post_coorlayout);

        toolbar = findViewById(R.id.user_post_toolbar);
        if (type.equals(TYPE_USER_POST)) toolbar.setTitle("我的发表");
        if (type.equals(TYPE_USER_REPLY)) toolbar.setTitle("我的回复");
        if (type.equals(TYPE_USER_FAVORITE)) toolbar.setTitle("我的收藏");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.user_post_rv);
        userPostAdapter = new UserPostAdapter(this, UserPostAdapter.TYPE_POST_DATE, R.layout.item_post);
        userPostAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userPostAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        setOnPostClickListener();
        setOnRefreshListener();
        refreshLayout.autoRefresh(0, 300, 1, true);
        getPostsData(true);
    }

    /**
     * author: sca_tl
     * description: 获取帖子列表数据
     */
    private void getPostsData(final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", SharePrefUtil.getId(this) + "");
        map.put("page", page + "");
        map.put("pageSize", "50");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());
        map.put("type", type);

        HttpRequestUtil.post(Constants.Api.GET_USER_POST, map, new OnHttpRequest() {
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
     * description:
     */
    private void resolveData(String data, boolean refresh) {
        refreshLayout.finishRefresh();
        JSONObject jsonObject = JSONObject.parseObject(data);
        UserPostBean userPostBean = JSON.parseObject(data, new TypeReference<UserPostBean>(){});

        if (userPostBean.rs == 1) {
            List<UserPostBean.ListBean> listBeans = JSON.parseObject(jsonObject.getJSONArray("list").toString(),
                    new TypeReference<List<UserPostBean.ListBean>>(){});

            if (listBeans != null && listBeans.size() != 0){
                userPostAdapter.addPostListData(listBeans, refresh);
            } else {
                request_error.setVisibility(View.VISIBLE);
            }

            if (page == 1) recyclerView.scheduleLayoutAnimation();
            if (userPostBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();
            if (userPostBean.has_next == 1) refreshLayout.finishLoadMore(true);

        } else {
            request_error.setVisibility(View.VISIBLE);
            request_error.setText(jsonObject.getJSONObject("head").getString("errInfo"));
        }
    }

    /**
     * author: sca_tl
     * description: 帖子点击事件
     */
    private void setOnPostClickListener() {

        userPostAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.card_view_item_post:
                        Intent intent = new Intent(UserPostActivity.this, PostDetailActivity.class);
                        intent.putExtra(Constants.Key.TOPIC_ID, userPostAdapter.getData().get(position).topic_id);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

        userPostAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_post_user_avatar:
                        Intent intent1 = new Intent(UserPostActivity.this, UserDetailActivity.class);
                        intent1.putExtra(Constants.Key.USER_ID, userPostAdapter.getData().get(position).user_id);
                        startActivity(intent1);
                        break;

                    default:
                        break;
                }
            }
        });


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
                getPostsData( true);
            }

            //加载更多，page递增
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getPostsData(false);
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
