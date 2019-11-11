package com.scatl.uestcbbs.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.UserDetailActivity;
import com.scatl.uestcbbs.adapters.AtUserListAdapter;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.AtUserListBean;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class AtUserListFragment extends BaseFragment {

    private static final String TAG = "AtUserListFragment";

    private SmartRefreshLayout refreshLayout;
    private RecyclerView  recyclerView;
    private AtUserListAdapter atUserListAdapter;

    public static final String AT_LIST_TYPE_FRIEND = "friend";
    public static final String AT_LIST_TYPE_FOLLOW = "follow";

    public static final int AT_USER_RESULT = 20;

    private String type;
    private int page = 1;

    public static AtUserListFragment newInstance(Bundle bundle) {
        AtUserListFragment atUserListFragment = new AtUserListFragment();
        atUserListFragment.setArguments(bundle);
        return atUserListFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_at_user_list;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        super.getBundle(bundle);
        type = bundle.getString(Constants.Key.TYPE);
    }

    @Override
    protected void init() {
        refreshLayout = view.findViewById(R.id.at_user_list_refresh);
        recyclerView = view.findViewById(R.id.at_user_list_rv);

        atUserListAdapter = new AtUserListAdapter(mActivity, R.layout.item_at_user_list);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(mActivity));
        recyclerView.setAdapter(atUserListAdapter);

        refreshLayout.autoRefresh(0, 300, 1, true);
        setOnRefreshListener();
        setOnItemClickListener();
        getAtUserListData(true);

    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnItemClickListener(){
        atUserListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_at_user_list_icon:
                        Intent intent = new Intent(mActivity, UserDetailActivity.class);
                        intent.putExtra(Constants.Key.USER_ID, atUserListAdapter.getData().get(position).uid);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

        atUserListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra(Constants.Key.AT_USER, "@" + atUserListAdapter.getData().get(position).name + " ");
                mActivity.setResult(AT_USER_RESULT, intent);
                mActivity.finish();
            }
        });
    }

    /**
     * author: TanLei
     * description: 获取列表数据
     */
    private void getAtUserListData(final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "100");
        map.put("accessToken", SharePrefUtil.getAccessToken(mActivity));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(mActivity));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_ICANAT_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                refreshLayout.finishRefresh();
                ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), getResources().getString(R.string.request_error));
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
        int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
        int has_next = jsonObject.getIntValue("has_next");

        if (rs == 1) {
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            List<AtUserListBean> atUserListBeanList = new ArrayList<>();

            for (int i = 0; i < jsonArray.size(); i ++) {
                AtUserListBean atUserListBean = new AtUserListBean();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                atUserListBean.uid = jsonObject1.getIntValue("uid");
                atUserListBean.name = jsonObject1.getString("name");
                atUserListBean.role_num = jsonObject1.getIntValue("role_num");

                if (type.equals(AT_LIST_TYPE_FRIEND) && atUserListBean.role_num == 2) {
                    atUserListBeanList.add(atUserListBean);
                }
                if (type.equals(AT_LIST_TYPE_FOLLOW) && atUserListBean.role_num == 6) {
                    atUserListBeanList.add(atUserListBean);
                }
            }

            atUserListAdapter.addAtUserListData(atUserListBeanList, refresh);

            if (page == 1) recyclerView.scheduleLayoutAnimation();
            if (has_next == 0) { refreshLayout.finishLoadMoreWithNoMoreData(); }
            if (has_next == 1) {refreshLayout.finishLoadMore(true);}

        } else {
            String err = jsonObject.getString("errcode");
            ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), err);
        }
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
                getAtUserListData(true);
            }

            //加载更多，page递增
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getAtUserListData(false);
            }
        });
    }

}
