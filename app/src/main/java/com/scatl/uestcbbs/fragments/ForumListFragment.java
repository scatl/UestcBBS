package com.scatl.uestcbbs.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.SearchActivity;
import com.scatl.uestcbbs.adapters.forumlist.ForumListLeftAdapter;
import com.scatl.uestcbbs.adapters.forumlist.ForumListRightAdapter;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.ForumListBean;
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


public class ForumListFragment extends BaseFragment {

    private static final String TAG = "ForumListFragment";

    private RecyclerView left_rv, right_rv;
    private ForumListLeftAdapter leftAdapter;
    private ForumListRightAdapter rightAdapter;
    private SmartRefreshLayout refreshLayout;
    private RelativeLayout forum_second_rl;
    private Toolbar toolbar;

    public static ForumListFragment newInstance(Bundle bundle) {
        ForumListFragment forumListFragment = new ForumListFragment();
        forumListFragment.setArguments(bundle);
        return forumListFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_forum_list;
    }

    @Override
    protected void init() {
        left_rv = view.findViewById(R.id.forum_list_left_rv);
        right_rv = view.findViewById(R.id.forum_list_right_rv);
        refreshLayout = view.findViewById(R.id.forum_list_refresh);
        forum_second_rl = view.findViewById(R.id.forum_list_second_rl);
        toolbar = view.findViewById(R.id.forum_list_toolbar);
        toolbar.setTitle("");
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        refreshLayout.setEnableLoadMore(false);

        leftAdapter = new ForumListLeftAdapter(mActivity, R.layout.item_forum_list_left);
        left_rv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        left_rv.setAdapter(leftAdapter);

        rightAdapter = new ForumListRightAdapter(mActivity, R.layout.item_forum_list_right);
        right_rv.setLayoutManager(new MyLinearLayoutManger(mActivity));
        right_rv.setAdapter(rightAdapter);

        setOnRefreshListener();
        setOnLeftItemClick();
        refreshLayout.autoRefresh(0, 300, 1, false);
    }

    /**
     * author: TanLei
     * description:
     */
    //TODO 右侧滑动，左侧应该随之改变
    private void setOnLeftItemClick() {

        leftAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                leftAdapter.setSelected(position);
                right_rv.smoothScrollToPosition(position);
            }
        });

    }

    /**
     * author: sca_tl
     * description: 获取板块列表
     */
    private void getForumListData() {
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_FORUM_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                refreshLayout.finishRefresh();
                ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), getResources().getString(R.string.request_error));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                resolveForumListData(response);
            }
        });
    }

    /**
     * author: sca_tl
     * description:
     */
    private void resolveForumListData(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。

        if (rs == 1) {

            List<ForumListBean.BoardBean> boardBeans = new ArrayList<>();

            JSONArray jsonArray = jsonObject.getJSONArray("list");

            for (int i = 0; i < jsonArray.size(); i ++) {
                ForumListBean.BoardBean boardBean = new ForumListBean.BoardBean();

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONArray jsonArray1 = jsonObject1.getJSONArray("board_list");
                List<ForumListBean.BoardBean.BoardListBean> boardListBean =
                        JSON.parseObject(jsonArray1.toString(),
                                new TypeReference<List<ForumListBean.BoardBean.BoardListBean>>(){});
                boardBean.boardListBeans.addAll(boardListBean);

                boardBean.board_category_name = jsonObject1.getString("board_category_name");
                boardBeans.add(boardBean);
            }

            //增加今日热门选项
            ForumListBean.BoardBean tdHotBoardBean = new ForumListBean.BoardBean();
            tdHotBoardBean.board_category_name = "今日热门";
            boardBeans.add(0, tdHotBoardBean);

            for (int j = 1; j < boardBeans.size(); j ++) {
                for (int k = 0; k < boardBeans.get(j).boardListBeans.size(); k ++) {
                    //筛选5个发帖数最高的板块
                    if (tdHotBoardBean.boardListBeans.size() < 5) {
                        tdHotBoardBean.boardListBeans.add(boardBeans.get(j).boardListBeans.get(k));
                    } else {
                        tdHotBoardBean.boardListBeans.add(boardBeans.get(j).boardListBeans.get(k));
                        int aa = tdHotBoardBean.boardListBeans.get(0).td_posts_num;
                        int index = 0;
                        for (int m = 0; m < tdHotBoardBean.boardListBeans.size(); m ++) {
                            if (aa >= tdHotBoardBean.boardListBeans.get(m).td_posts_num) {
                                aa = tdHotBoardBean.boardListBeans.get(m).td_posts_num;
                                index = m;
                            }
                        }
                        tdHotBoardBean.boardListBeans.remove(index);
                    }
                }
            }


            leftAdapter.addData(boardBeans);
            rightAdapter.addData(boardBeans);
        }

        refreshLayout.finishRefresh();
    }


    /**
     * author: sca_tl
     * description: 刷新监听
     */
    private void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getForumListData();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forum_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_forum_list_search:
                Intent intent = new Intent(mActivity, SearchActivity.class);
                startActivity(intent);
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCEED
                || baseEvent.eventCode == BaseEvent.EventCode.LOGOUT_SUCCEED) {
            refreshLayout.autoRefresh(0, 300, 1, false);
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
