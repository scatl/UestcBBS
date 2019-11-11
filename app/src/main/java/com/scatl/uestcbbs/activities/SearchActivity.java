package com.scatl.uestcbbs.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.search.SearchPostAdapter;
import com.scatl.uestcbbs.adapters.search.SearchUserAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.SearchPostBean;
import com.scatl.uestcbbs.entities.SearchUserBean;
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

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SearchActivity";

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private RadioButton by_post, by_user;
    private RadioGroup radioGroup;
    private AppCompatEditText keyword_edittext;
    private Button search_btn;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SearchPostAdapter searchPostAdapter;
    private SearchUserAdapter searchUserAdapter;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
    }

    /**
     * author: TanLei
     * description: 初始化
     */
    private void init() {
        coordinatorLayout = findViewById(R.id.search_coorlayout);
        toolbar = findViewById(R.id.search_toolbar);
        by_post = findViewById(R.id.search_radio_btn_by_post);
        by_user = findViewById(R.id.search_radio_btn_by_user);
        radioGroup = findViewById(R.id.search_radio_group);
        keyword_edittext = findViewById(R.id.search_keyword_edittext);
        keyword_edittext.requestFocus();
        search_btn = findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this);
        refreshLayout = findViewById(R.id.search_refresh);
        recyclerView = findViewById(R.id.search_rv);

        toolbar.setTitle("搜索");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchPostAdapter = new SearchPostAdapter(this, R.layout.item_post);
        searchUserAdapter = new SearchUserAdapter(this, R.layout.item_search_user);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));

        refreshLayout.setEnableRefresh(false);
        setOnRefreshListener();
        setOnEnterPressed();
        setOnItemClick();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_btn:
                page = 1;
                if (keyword_edittext.getText().toString().length() == 0) {
                    ToastUtil.showSnackBar(coordinatorLayout, "请输入关键词");
                } else {
                    CommonUtil.hideSoftKeyboard(SearchActivity.this, view);
                    startSearch(true);
                }
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnItemClick() {
        searchPostAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);
                intent.putExtra(Constants.Key.TOPIC_ID, searchPostAdapter.getData().get(position).topic_id);
                startActivity(intent);
            }
        });

        searchPostAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(SearchActivity.this, UserDetailActivity.class);
                intent.putExtra(Constants.Key.USER_ID, searchPostAdapter.getData().get(position).user_id);
                startActivity(intent);
            }
        });

        searchUserAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(SearchActivity.this, UserDetailActivity.class);
                intent.putExtra(Constants.Key.USER_ID, searchUserAdapter.getData().get(position).uid);
                startActivity(intent);
            }
        });
    }

    /**
     * author: sca_tl
     * description: 点击输入法enter键
     */
    private void setOnEnterPressed() {
        keyword_edittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    CommonUtil.hideSoftKeyboard(SearchActivity.this, view);
                    startSearch(true);
                }
                return false;
            }
        });
    }

    /**
     * author: TanLei
     * description: 搜索
     */
    private void startSearch(final boolean refresh) {

        final ProgressDialog progressDialog = ProgressDialog.show(this,
                null, "正在搜索，请稍后...");
        progressDialog.show();

        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "50");
        map.put("keyword", keyword_edittext.getText().toString());
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        String url;
        if (by_post.isChecked()) {
            url = Constants.Api.SEARCH_POST;
        } else {
            url = Constants.Api.SEARCH_USER;
            map.put("searchid", "0");
        }

        HttpRequestUtil.post(url, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                ToastUtil.showSnackBar(coordinatorLayout, getResources().getString(R.string.request_error));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                progressDialog.dismiss();
                if (by_post.isChecked()) {
                    resolvePostData(response, refresh);
                } else {
                    resolveUserData(response, refresh);
                }
            }
        });

    }

    /**
     * author: TanLei
     * description: 搜索帖子的数据
     */
    private void resolvePostData(String data, boolean refresh) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        SearchPostBean searchPostBean = JSON.parseObject(data, new TypeReference<SearchPostBean>(){});
        List<SearchPostBean.ListBean> listBeans = new ArrayList<>();

        if (jsonObject.getJSONArray("list") != null) {
            listBeans = JSON.parseObject(jsonObject.getJSONArray("list").toString(),
                    new TypeReference<List<SearchPostBean.ListBean>>(){});
        }

        if (searchPostBean.rs == 1) {
            if (listBeans != null && listBeans.size() != 0) {
                searchPostAdapter.addSearchPostData(listBeans, refresh);
            }

            if (page == 1) recyclerView.setAdapter(searchPostAdapter);
            if (searchPostBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();
            if (searchPostBean.has_next == 1) refreshLayout.finishLoadMore(true);

        } else {
            ToastUtil.showSnackBar(coordinatorLayout, jsonObject.getString("errcode"));
        }
    }

    /**
     * author: TanLei
     * description: 搜索用户的数据
     */
    private void resolveUserData(String data, boolean refresh) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        SearchUserBean searchUserBean = JSON.parseObject(data, new TypeReference<SearchUserBean>(){});
        List<SearchUserBean.BodyBean.ListBean> listBeans = new ArrayList<>();

        //搜索用户用空格的话jsonarray为空，所以需要判断一下
        JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("list");
        if (jsonArray != null) {
            listBeans = JSON.parseObject(jsonArray.toString(),
                    new TypeReference<List<SearchUserBean.BodyBean.ListBean>>(){});
        }

        if (searchUserBean.rs == 1) {
            if (listBeans != null && listBeans.size() != 0) {
                searchUserAdapter.addSearchUserData(listBeans, refresh);
            }

            if (page == 1)recyclerView.setAdapter(searchUserAdapter);
            if (searchUserBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();
            if (searchUserBean.has_next == 1) refreshLayout.finishLoadMore(true);
        } else {
            ToastUtil.showSnackBar(coordinatorLayout, jsonObject.getJSONObject("body").getString("errInfo"));
        }

    }

    /**
     * author: sca_tl
     * description: 上拉下拉刷新监听
     */
    private void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
            }

            //加载更多，page递增
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                startSearch(false);
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
}
