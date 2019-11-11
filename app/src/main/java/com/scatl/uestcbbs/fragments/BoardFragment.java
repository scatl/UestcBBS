package com.scatl.uestcbbs.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.PostDetailActivity;
import com.scatl.uestcbbs.activities.UserDetailActivity;
import com.scatl.uestcbbs.adapters.SimplePostAdapter;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.ForumListBean;
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


public class BoardFragment extends BaseFragment {

    private static final String TAG = "BoardFragment";

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SimplePostAdapter simplePostAdapter;

    private String sortby;
    private String current_boardId;
    private String current_filterId;

    public static final String BOARD_SORTBY_NEW = "new"; //最新
    public static final String BOARD_SORTBY_ALL = "all"; //全部
    public static final String BOARD_SORTBY_ESSENCE = "essence";//精华

    //板块所有分类
    private List<ForumListBean.BoardBean.BoardListBean.BoardCatBean> boardCatBeanList = new ArrayList<>();

    private int page = 1;

    public static BoardFragment newInstance(Bundle bundle) {
        BoardFragment mineFragment = new BoardFragment();
        mineFragment.setArguments(bundle);
        return mineFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_board;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        sortby = bundle.getString(Constants.Key.SORT_BY);
        current_boardId = bundle.getString(Constants.Key.BOARD_ID);
        current_filterId = bundle.getString(Constants.Key.FILTER_ID);
    }

    @Override
    protected void init() {
        refreshLayout = view.findViewById(R.id.board_refresh);
        recyclerView = view.findViewById(R.id.board_rv);

        simplePostAdapter = new SimplePostAdapter(mActivity, R.layout.item_post);
        simplePostAdapter.setType(SimplePostAdapter.TYPE_REPLY_DATE);
        simplePostAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(simplePostAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        setOnRefreshListener();
        setOnPostClickListener();

        refreshLayout.autoRefresh(0, 300, 1, true);
        getBoardData(true);

    }


    /**
     * author: TanLei
     * description: 获取板块帖子列表数据，该处返回该fid下的所有分类信息
     */
    private void getBoardData(final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "30");
        map.put("sortby", sortby);
        map.put("boardId", current_boardId);
        map.put("topOrder", 0 + "");
        map.put("filterType", "typeid");
        map.put("filterId", current_filterId);
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_TOPIC_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                refreshLayout.finishRefresh();
                ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), getResources().getString(R.string.request_error));
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

    private void resolveData(String data, boolean refresh) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
        int has_next = jsonObject.getIntValue("has_next");

        if (rs == 1) {
            List<PostListBean> listBeans = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            if (jsonArray != null) {
                listBeans = JSON.parseObject(jsonArray.toString(), new TypeReference<List<PostListBean>>(){});
            }

            simplePostAdapter.addPostData(listBeans, refresh);
            if (page == 1) recyclerView.scheduleLayoutAnimation();
            if (has_next == 1) refreshLayout.finishLoadMore(true);
            if (has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();

            //获取该板块下的所有分类
            JSONArray jsonArray1 = jsonObject.getJSONArray("classificationType_list");
            boardCatBeanList.clear();
            ForumListBean.BoardBean.BoardListBean.BoardCatBean all = new ForumListBean.BoardBean.BoardListBean.BoardCatBean();
            all.classificationType_name = "全部";
            all.classificationType_id = 0;
            boardCatBeanList.add(all);
            for (int j = 0; j < jsonArray1.size(); j ++) {
                ForumListBean.BoardBean.BoardListBean.BoardCatBean boardCatBean = new ForumListBean.BoardBean.BoardListBean.BoardCatBean();
                boardCatBean.classificationType_id = jsonArray1.getJSONObject(j).getIntValue("classificationType_id");
                boardCatBean.classificationType_name = jsonArray1.getJSONObject(j).getString("classificationType_name");
                boardCatBeanList.add(boardCatBean);
            }

            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.BOARD_CAT_INIT, boardCatBeanList));

        } else {
            String err = jsonObject.getString("errcode");
            ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), err);

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
                getBoardData(  true);
            }

            //加载更多，page递增
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getBoardData(  false);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent<BaseEvent.BoardChange> baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.BOARD_CHANGED) {
            current_boardId = baseEvent.eventData.boardId + "";
            current_filterId = baseEvent.eventData.catId + "";
            boolean b = refreshLayout.autoRefresh(0, 400, 1, true);
            Log.e(TAG, b+"");
            //refreshLayout.autoRefreshAnimationOnly();
            getBoardData(true);
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
