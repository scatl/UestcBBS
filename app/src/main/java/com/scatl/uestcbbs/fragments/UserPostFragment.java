package com.scatl.uestcbbs.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.PostDetailActivity;
import com.scatl.uestcbbs.activities.UserDetailActivity;
import com.scatl.uestcbbs.adapters.userdetail.UserPostAdapter;
import com.scatl.uestcbbs.base.BaseFragment;
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

public class UserPostFragment extends BaseFragment {

    private static final String TAG = "UserPostFragment";

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private UserPostAdapter userPostAdapter;

    public static final String TYPE_POST = "topic";
    public static final String TYPE_REPLY = "reply";

    private String type;
    private int user_id;
    private int page = 1;

    public static UserPostFragment newInstance(Bundle bundle) {
        UserPostFragment userPostFragment = new UserPostFragment();
        userPostFragment.setArguments(bundle);
        return userPostFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_user_post;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        type = bundle.getString(Constants.Key.TYPE);
        user_id = bundle.getInt(Constants.Key.USER_ID);
    }

    @Override
    protected void init() {
        refreshLayout = view.findViewById(R.id.user_detail_post_refresh);
        recyclerView = view.findViewById(R.id.user_detail_post_rv);

        userPostAdapter = new UserPostAdapter(mActivity, UserPostAdapter.TYPE_POST_DATE, R.layout.item_post);
        userPostAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(userPostAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_from_top);
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
        map.put("uid", user_id + "");
        map.put("page", page + "");
        map.put("pageSize", "30");
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());
        map.put("type", type);

        HttpRequestUtil.post(Constants.Api.GET_USER_POST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                if (refresh) refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore(false);
                ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), mActivity.getResources().getString(R.string.request_error));
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
        List<UserPostBean.ListBean> listBeans = JSON.parseObject(jsonObject.getJSONArray("list").toString(),
                new TypeReference<List<UserPostBean.ListBean>>(){});

        if (userPostBean.rs == 1) {
            if (listBeans!= null && listBeans.size() != 0){
                userPostAdapter.addPostListData(listBeans, refresh);
            }

            if (page == 1) recyclerView.scheduleLayoutAnimation();
            if (userPostBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();
            if (userPostBean.has_next == 1) refreshLayout.finishLoadMore(true);

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
                        Intent intent = new Intent(mActivity, PostDetailActivity.class);
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
                        Intent intent1 = new Intent(mActivity, UserDetailActivity.class);
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
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {

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

}
