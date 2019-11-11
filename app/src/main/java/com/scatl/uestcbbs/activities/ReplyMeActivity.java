package com.scatl.uestcbbs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.notification.ReplyMeMsgAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.dialogs.ReplyDialog;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.ReplyMeMsgBean;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplyMeActivity extends BaseActivity {

    private static final String TAG = "ReplyMeActivity";

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private ReplyMeMsgAdapter replyMeMsgAdapter;
    private TextView request_error;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_me);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {

        coordinatorLayout = findViewById(R.id.reply_me_coorlayout);
        refreshLayout = findViewById(R.id.reply_me_refresh);
        toolbar = findViewById(R.id.reply_me_toolbar);
        toolbar.setTitle("回复我的");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.reply_me_rv);
        request_error = findViewById(R.id.reply_me_error);
        replyMeMsgAdapter = new ReplyMeMsgAdapter(this, R.layout.item_reply_me);
        replyMeMsgAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(replyMeMsgAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        refreshLayout.autoRefresh(0, 300, 1, true);
        getReplyMeMsgData(true);
        setOnRefreshListener();
        setOnItemClickListener();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnItemClickListener() {

        replyMeMsgAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_reply_me_user_icon:
                        Intent intent = new Intent(ReplyMeActivity.this, UserDetailActivity.class);
                        intent.putExtra(Constants.Key.USER_ID, replyMeMsgAdapter.getData().get(position).user_id);
                        startActivity(intent);
                        break;

                    case R.id.item_reply_me_quote_rl:
                        Intent intent1 = new Intent(ReplyMeActivity.this, PostDetailActivity.class);
                        intent1.putExtra(Constants.Key.TOPIC_ID, replyMeMsgAdapter.getData().get(position).topic_id);
                        startActivity(intent1);
                        break;

                    case R.id.item_reply_me_reply_btn:
                        ReplyDialog replyDialog = new ReplyDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.Key.BOARD_ID, replyMeMsgAdapter.getData().get(position).board_id);
                        bundle.putInt(Constants.Key.TOPIC_ID, replyMeMsgAdapter.getData().get(position).topic_id);
                        bundle.putInt(Constants.Key.QUOTE_ID, replyMeMsgAdapter.getData().get(position).reply_remind_id);
                        bundle.putBoolean(Constants.Key.IS_QUOTE, true);
                        bundle.putString(Constants.Key.USER_NAME, replyMeMsgAdapter.getData().get(position).user_name);
                        replyDialog.setArguments(bundle);
                        replyDialog.show(getSupportFragmentManager(), System.currentTimeMillis() + "");
                        break;

                    default:
                        break;
                }
            }
        });

    }

    /**
     * author: sca_tl
     * description: 获取回复数据
     */
    private void getReplyMeMsgData(final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "30");
        map.put("type", "post");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_REPLY_ME_MESSAGE, map, new OnHttpRequest() {
            @Override
            public void onRequestError(okhttp3.Call call, Exception e, int id) {
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
     * description: 解析回复数据
     */
    private void resolveData(String data, boolean refresh) {
        refreshLayout.finishRefresh();
        JSONObject jsonObject = JSONObject.parseObject(data);

        ReplyMeMsgBean replyMeMsgBean = JSON.parseObject(data, new TypeReference<ReplyMeMsgBean>(){});
        List<ReplyMeMsgBean.BodyBean.ListBean> listBeans = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("data");
        if (jsonArray != null) {
            listBeans = JSON.parseObject(jsonArray.toString(),
                    new TypeReference<List<ReplyMeMsgBean.BodyBean.ListBean>>(){});
        }

        if (jsonArray != null && replyMeMsgBean.rs == 1) {
            if (listBeans != null && listBeans.size() != 0) {
                replyMeMsgAdapter.addReplyMeMsgData(listBeans, refresh);
            }
            if (page == 1) recyclerView.scheduleLayoutAnimation();
            if (replyMeMsgBean.has_next == 1) refreshLayout.finishLoadMore(true);
            if (replyMeMsgBean.has_next == 0) refreshLayout.finishLoadMoreWithNoMoreData();
            request_error.setVisibility(jsonArray.size() == 0 ? View.VISIBLE : View.GONE);
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_NEW_REPLY_COUNT_ZERO));

        } else {
            request_error.setVisibility(View.VISIBLE);
            request_error.setText(jsonObject.getJSONObject("head").getString("errInfo"));
        }
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnRefreshListener(){
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getReplyMeMsgData(true);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                getReplyMeMsgData(false);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //确保子fragment调用onActivityResult方法
        getSupportFragmentManager().getFragments();
        if (getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
