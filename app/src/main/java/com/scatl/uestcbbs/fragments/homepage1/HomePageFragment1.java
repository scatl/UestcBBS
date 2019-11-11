package com.scatl.uestcbbs.fragments.homepage1;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.CreatePostActivity;
import com.scatl.uestcbbs.activities.DailyPicActivity;
import com.scatl.uestcbbs.activities.PostDetailActivity;
import com.scatl.uestcbbs.activities.UserDetailActivity;
import com.scatl.uestcbbs.adapters.homepage1.HomePageAdapter1;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.databases.PostListDataBase;
import com.scatl.uestcbbs.entities.DailyPicBean;
import com.scatl.uestcbbs.entities.HomePage2MultipleItem;
import com.scatl.uestcbbs.entities.PostListBean;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class HomePageFragment1 extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "HomePageFragment1";

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private HomePageAdapter1 homePageAdapter1;
    private Toolbar toolbar;
    private FloatingActionButton up_btn, create_btn;

    private int latest_post_page = 1;
    private int latest_reply_page = 0;
    private int total_post_page = 1;

    private static final String SORT_BY_NEW = "new"; //最新发表
    private static final String SORT_BY_ALL = "all"; //最新回复

    public static HomePageFragment1 newInstance(Bundle bundle) {
        HomePageFragment1 homePageFragment1 = new HomePageFragment1();
        homePageFragment1.setArguments(bundle);
        return homePageFragment1;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_home_page1;
    }

    @Override
    protected void init() {
        up_btn = view.findViewById(R.id.home_page1_up_btn);
        up_btn.setOnClickListener(this);
        create_btn = view.findViewById(R.id.home_page1_create_btn);
        create_btn.setOnClickListener(this);
        refreshLayout = view.findViewById(R.id.home_page1_refresh);
        toolbar = view.findViewById(R.id.home_page1_toolbar);
        toolbar.setTitle("");

        List<HomePage2MultipleItem> data = new ArrayList<>();
        data.add(new HomePage2MultipleItem(HomePageAdapter1.TYPE_DAILY_PIC));
        data.add(new HomePage2MultipleItem(HomePageAdapter1.TYPE_HOT_POST));
        data.add(new HomePage2MultipleItem(HomePageAdapter1.TYPE_SIMPLE_POST));
        homePageAdapter1 = new HomePageAdapter1(mActivity, data);

        recyclerView = view.findViewById(R.id.home_page1_rv);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(homePageAdapter1);

        setOnPostClick();
        setOnRefreshListener();
        initView();
    }


    /**
     * author: TanLei
     * description:
     */
    private void setOnPostClick() {

        //item里的recyclerview点击事件
        homePageAdapter1.setOnItemClickListener(new HomePageAdapter1.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.card_view_item_post:
                        Intent intent = new Intent(mActivity, PostDetailActivity.class);
                        intent.putExtra(Constants.Key.TOPIC_ID, homePageAdapter1.getSimplePostData().get(position).topic_id);
                        startActivity(intent);
                        break;

                    case R.id.main_page_hot_post_cardview:
                        Intent intent1 = new Intent(mActivity, PostDetailActivity.class);
                        intent1.putExtra(Constants.Key.TOPIC_ID, homePageAdapter1.getHotPostData().get(position).topic_id);
                        startActivity(intent1);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onItemChildClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.item_post_user_avatar:
                        Intent intent = new Intent(mActivity, UserDetailActivity.class);
                        intent.putExtra(Constants.Key.USER_ID, homePageAdapter1.getSimplePostData().get(position).user_id);
                        startActivity(intent);
                        break;

                    case R.id.main_page_hot_post_user_avatar:
                        Intent intent1 = new Intent(mActivity, UserDetailActivity.class);
                        intent1.putExtra(Constants.Key.USER_ID, homePageAdapter1.getHotPostData().get(position).user_id);
                        startActivity(intent1);
                        break;


                    default:
                        break;
                }
            }
        });

        //item点击事件
        homePageAdapter1.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.daily_pic_fullscreen:
                        Intent intent2 = new Intent(mActivity, DailyPicActivity.class);
                        startActivity(intent2);
                        break;

                    default:
                        break;
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_page1_up_btn:
                recyclerView.smoothScrollToPosition(0);
                break;

            case R.id.home_page1_create_btn:
                startActivity(new Intent(mActivity, CreatePostActivity.class));
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description: 先从数据库里取出旧数据显示
     */
    private void initView() {

        List<PostListBean> hot_post_data = PostListDataBase.getPostData(mActivity, PostListDataBase.TYPE_HOT);
        List<PostListBean> simple_post_data = PostListDataBase.getPostData(mActivity, PostListDataBase.TYPE_SIMPLE);

        homePageAdapter1.addHotPostData(hot_post_data, true);
        homePageAdapter1.addSimplePostData(simple_post_data, true);
        homePageAdapter1.addDailyPicData(SharePrefUtil.getDailyPicInfo(mActivity));
        recyclerView.scheduleLayoutAnimation();
        refreshLayout.autoRefresh(1000, 300, 1, false);

    }

    /**
     * author: sca_tl
     * description: 获取每日一图数据
     */
    private void getDailyPicData() {
        final DailyPicBean dailyPicBean = new DailyPicBean();
        HttpRequestUtil.get(Constants.Api.DAILY_PIC, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("images");

                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                String image_url = jsonObject1.getString("url");
                String copy_right = jsonObject1.getString("copyright");
                String hsh = jsonObject1.getString("hsh");

                dailyPicBean.copy_right = copy_right;
                dailyPicBean.remote_url = "https://cn.bing.com" + image_url;
                dailyPicBean.hsh = hsh;
                SharePrefUtil.setDailyPicInfo(mActivity, dailyPicBean);

                homePageAdapter1.addDailyPicData(dailyPicBean);
            }
        });
    }


    /**
     * author: sca_tl
     * description: 获取热帖数据
     */
    private void getHotPostData(int page, final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "10");
        map.put("moduleId", "2");
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.HOT_POST_URL, map, new OnHttpRequest() {
            @Override
            public void onRequestError(okhttp3.Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
                if (rs == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray("list");

                    List<PostListBean> postListBeans = new ArrayList<>();

                    if (jsonArray.size() != 0){
                        for (int i = 0; i < jsonArray.size(); i ++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PostListBean postListBean = new PostListBean();

                            postListBean.user_nick_name = jsonObject1.getString("user_nick_name");
                            postListBean.title = jsonObject1.getString("title");
                            postListBean.subject = jsonObject1.getString("summary");
                            postListBean.last_reply_date = jsonObject1.getLongValue("last_reply_date");
                            postListBean.replies = jsonObject1.getIntValue("replies");
                            postListBean.board_name = jsonObject1.getString("board_name");
                            postListBean.user_avatar = jsonObject1.getString("userAvatar");
                            postListBean.topic_id = jsonObject1.getIntValue("source_id");
                            postListBean.user_id = jsonObject1.getIntValue("user_id");
                            postListBean.hits = jsonObject1.getIntValue("hits");

                            postListBeans.add(postListBean);
                        }
                    }
                    homePageAdapter1.addHotPostData(postListBeans, refresh);
                    PostListDataBase.savePostData(mActivity, PostListDataBase.TYPE_HOT, postListBeans);
                }
            }
        });

    }


    /**
     * author: sca_tl
     * description: 获取帖子
     */
    private void getSimplePostData(int page, String sort, final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "30");
        map.put("boardId", "0");
        map.put("sortby", sort);
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.LATEST_REPLY_URL, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                refreshLayout.finishRefresh(0);
                ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), e.getMessage());
                //ToastUtil.showToast(mActivity, mActivity.getResources().getString(R.string.request_error));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                refreshLayout.finishRefresh();
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
                if (rs == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    if (jsonArray != null) {
                        List<PostListBean> postListBeans = JSON.parseObject(jsonArray.toString(),
                                new TypeReference<List<PostListBean>>(){});
                        homePageAdapter1.addSimplePostData(postListBeans, refresh);
                        PostListDataBase.savePostData(mActivity,  PostListDataBase.TYPE_SIMPLE, postListBeans);
                        refreshLayout.finishLoadMore(true);
                    }
                }
            }
        });
    }

    /**
     * author: sca_tl
     * description: 刷新监听
     */
    private void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                latest_post_page = 1;
                latest_reply_page = 0;
                total_post_page = 1;
                getDailyPicData();
                getHotPostData(1, true);
                getSimplePostData(1, SORT_BY_NEW, true);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                //间隔获取最新发表和最新回复数据
                total_post_page = total_post_page + 1;

                if (total_post_page % 2 == 0){
                    latest_reply_page = latest_reply_page + 1;
                    getSimplePostData(latest_reply_page, SORT_BY_ALL, false);
                } else {
                    latest_post_page = latest_post_page + 1;
                    getSimplePostData(latest_post_page, SORT_BY_NEW, false);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCEED
                || baseEvent.eventCode == BaseEvent.EventCode.LOGOUT_SUCCEED) {
            initView();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (! EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }


}

