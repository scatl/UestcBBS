package com.scatl.uestcbbs.activities;

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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.clans.fab.FloatingActionButton;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.SuggestionAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.dialogs.SuggestionDialog;
import com.scatl.uestcbbs.entities.SuggestionBean;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.interfaces.OnRefresh;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.RefreshUtil;
import com.scatl.uestcbbs.utils.TimeUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

import okhttp3.Call;

public class SuggestionActivity extends BaseActivity implements View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SuggestionAdapter suggestionAdapter;
    private FloatingActionButton create_btn;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        init();
    }

    private void init() {
        coordinatorLayout = findViewById(R.id.suggestion_coorlayout);
        toolbar = findViewById(R.id.suggestion_toolbar);
        toolbar.setTitle("意见反馈");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.suggestion_rv);
        refreshLayout = findViewById(R.id.suggestion_refresh);
        create_btn = findViewById(R.id.suggestion_create_btn);
        create_btn.setOnClickListener(this);
        hint = findViewById(R.id.suggestion_hint);

        suggestionAdapter = new SuggestionAdapter(this, R.layout.item_suggestion);
        suggestionAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(suggestionAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        setOnRefreshListener();
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.autoRefresh(0, 300, 1, false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suggestion_create_btn:
                SuggestionDialog suggestionDialog = new SuggestionDialog();
                suggestionDialog.show(getSupportFragmentManager(), TimeUtil.getStringMs());
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description: 获取数据
     */
    private void getSuggestionData() {
        HttpRequestUtil.postString(Constants.Api.GET_SUGGESTION, "", new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                refreshLayout.finishRefresh();
                if (JSON.isValidObject(response)) {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    List<SuggestionBean> suggestionBeanList = JSON.parseObject(jsonObject.get("data").toString(),
                            new TypeReference<List<SuggestionBean>>(){});
                    suggestionAdapter.addSuggestionData(suggestionBeanList, true);
                    recyclerView.scheduleLayoutAnimation();
                    hint.setText(suggestionBeanList.size() == 0 ? "还没有内容" : "");
                }
            }
        });
    }

    private void setOnRefreshListener(){
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                getSuggestionData();
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
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
