package com.scatl.uestcbbs.fragments.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.CreatePostActivity;
import com.scatl.uestcbbs.activities.PostDetailActivity;
import com.scatl.uestcbbs.activities.UserDetailActivity;
import com.scatl.uestcbbs.adapters.SimplePostAdapter;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.databases.PostListDataBase;
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


/**
 * author: sca_tl
 * description: homepage的三个fragment
 */
public class PostListFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "PostListFragment";

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SimplePostAdapter simplePostAdapter;
    private FloatingActionButton up_btn, create_btn;

    private String type;   //三种类型：最新回复，最新发布，最热发布
    private int page = 1;  //初始页数

    public static PostListFragment newInstance(Bundle bundle) {
        PostListFragment postListFragment = new PostListFragment();
        postListFragment.setArguments(bundle);
        return postListFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_post_list;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        type = bundle.getString(Constants.Key.TYPE);
    }

    @Override
    protected void init() {
        up_btn = view.findViewById(R.id.home_page_up_btn);
        up_btn.setOnClickListener(this);
        create_btn = view.findViewById(R.id.home_page_create_btn);
        create_btn.setOnClickListener(this);
        refreshLayout = view.findViewById(R.id.post_list_refresh);
        recyclerView = view.findViewById(R.id.post_list_rv);

        simplePostAdapter = new SimplePostAdapter(mActivity, R.layout.item_post);
        if (type.equals(Constants.Api.TYPE_LATEST_POST) || type.equals(Constants.Api.TYPE_LATEST_REPLY)) {
            simplePostAdapter.setType(SimplePostAdapter.TYPE_REPLY_DATE);
        }
        if (type.equals(Constants.Api.TYPE_HOT_POST)) {
            simplePostAdapter.setType(SimplePostAdapter.TYPE_POST_DATE);
        }
        simplePostAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(simplePostAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        setOnRefreshListener();
        setOnPostClickListener();
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_page_up_btn:
                recyclerView.smoothScrollToPosition(0);
                break;

            case R.id.home_page_create_btn:
                startActivity(new Intent(mActivity, CreatePostActivity.class));
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description: 帖子点击事件
     */
    private void setOnPostClickListener() {

        simplePostAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.card_view_item_post:
                        Intent intent = new Intent(mActivity, PostDetailActivity.class);
                        intent.putExtra(Constants.Key.TOPIC_ID, simplePostAdapter.getData().get(position).topic_id);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

        simplePostAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_post_user_avatar:
                        Intent intent1 = new Intent(mActivity, UserDetailActivity.class);
                        intent1.putExtra(Constants.Key.USER_ID, simplePostAdapter.getData().get(position).user_id);
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
                getPostsData(1, true);
            }

            //加载更多，page递增
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getPostsData(page, false);
            }
        });
    }


    /**
     * author: sca_tl
     * description: 初始化登陆成功后的界面，获取数据
     */
    private void initView() {
        page = 1;

        if (type.equals(Constants.Api.TYPE_LATEST_REPLY)) {
            simplePostAdapter.addPostData(PostListDataBase.getPostData(mActivity, PostListDataBase.TYPE_LATEST_REPLY), false);
        }
        if (type.equals(Constants.Api.TYPE_LATEST_POST)) {
            simplePostAdapter.addPostData(PostListDataBase.getPostData(mActivity, PostListDataBase.TYPE_LATEST_POST), false);
        }
        if (type.equals(Constants.Api.TYPE_HOT_POST)) {
            simplePostAdapter.addPostData(PostListDataBase.getPostData(mActivity, PostListDataBase.TYPE_HOT), false);
        }

        recyclerView.scheduleLayoutAnimation();
        refreshLayout.autoRefresh(1000, 300, 1 , false);
    }


    /**
     * author: sca_tl
     * description: 获取帖子列表数据
     * @param page 第几页
     */
    private void getPostsData(int page, final boolean refresh) {
        Map<String, String> map= new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "30");
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        String url = "";

        if (type.equals(Constants.Api.TYPE_LATEST_REPLY)) {
            map.put("boardId", "0");
            map.put("sortby", "all");
            url = Constants.Api.LATEST_REPLY_URL;
        }
        if (type.equals(Constants.Api.TYPE_LATEST_POST)) {
            map.put("boardId", "0");
            map.put("sortby", "new");
            url = Constants.Api.LATEST_POST_URL;
        }
        if (type.equals(Constants.Api.TYPE_HOT_POST)) {
            map.put("moduleId", "2");
            url = Constants.Api.HOT_POST_URL;
        }


        HttpRequestUtil.post(url, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                refreshLayout.finishRefresh();
                ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), mActivity.getResources().getString(R.string.request_error));
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
     * description:
     */
    private void resolveData(String data, boolean refresh) {
        JSONObject jsonObject = JSONObject.parseObject(data);
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
                    postListBean.last_reply_date = jsonObject1.getLongValue("last_reply_date");
                    postListBean.replies = jsonObject1.getIntValue("replies");
                    postListBean.board_name = jsonObject1.getString("board_name");
                    postListBean.user_avatar = jsonObject1.getString("userAvatar");
                    postListBean.user_id = jsonObject1.getIntValue("user_id");
                    postListBean.hits = jsonObject1.getIntValue("hits");
					postListBean.vote = jsonObject1.getIntValue("vote");

                    if (type.equals(Constants.Api.TYPE_LATEST_POST) || type.equals(Constants.Api.TYPE_LATEST_REPLY)) {
                        postListBean.topic_id = jsonObject1.getIntValue("topic_id");
                        postListBean.subject = jsonObject1.getString("subject");
                        simplePostAdapter.setType(SimplePostAdapter.TYPE_REPLY_DATE);
                    }
                    if (type.equals(Constants.Api.TYPE_HOT_POST)) {
                        postListBean.subject = jsonObject1.getString("summary");
                        postListBean.topic_id = jsonObject1.getIntValue("source_id");
                        simplePostAdapter.setType(SimplePostAdapter.TYPE_POST_DATE);
                    }

                    postListBeans.add(postListBean);
                }
            }

            simplePostAdapter.addPostData(postListBeans, refresh);

            if (type.equals(Constants.Api.TYPE_LATEST_REPLY)) {
                PostListDataBase.savePostData(mActivity, PostListDataBase.TYPE_LATEST_REPLY, postListBeans);
            }
            if (type.equals(Constants.Api.TYPE_LATEST_POST)) {
                PostListDataBase.savePostData(mActivity, PostListDataBase.TYPE_LATEST_POST, postListBeans);
            }
            if (type.equals(Constants.Api.TYPE_HOT_POST)) {
                PostListDataBase.savePostData(mActivity, PostListDataBase.TYPE_HOT, postListBeans);
            }

            refreshLayout.finishLoadMore(true);
            if (type.equals(Constants.Api.TYPE_HOT_POST)) {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
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
        if (! EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }




}
