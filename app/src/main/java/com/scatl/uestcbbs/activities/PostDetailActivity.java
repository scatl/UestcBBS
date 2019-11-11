package com.scatl.uestcbbs.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.just.agentweb.AgentWeb;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.PostReplyAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.dialogs.ReplyDialog;
import com.scatl.uestcbbs.custom.dialogs.ReportDialog;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.PostDetailBean;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.interfaces.OnRefresh;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.ImageUtil;
import com.scatl.uestcbbs.utils.MediaPlayerUtil;
import com.scatl.uestcbbs.utils.RefreshUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.TimeUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "PostDetailActivity";

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;
    private ContentView contentView;
    private Spinner spinner;
    private SmartRefreshLayout post_detail_refresh;
    private TextView post_detail_title;
    private TextView post_detail_level;
    private CircleImageView post_detail_author_avatar;
    private TextView post_detail_author_name;
    private TextView post_detail_author_time;
    private RelativeLayout no_reply_hint;
    private NestedScrollView nestedScrollView;
    private ImageView post_detail_favorite;
    private RelativeLayout error_rl, webview_rl;
    private ImageView error_img;
    private TextView error_text;
    private FloatingActionButton create_comment_btn, up_btn;
    private RecyclerView post_reply_recyclerview;
    private PostReplyAdapter postReplyAdapter;
    private CardView cardView1, cardview2;
    private LinearLayout zhanwei, huadong;
    private AgentWeb agentWeb;

    private String[] spinner_item = new String[]{"按时间正序", "按时间倒序", "只看楼主"};

    private int page = 1;
    private boolean isRefreshed = false;
    private boolean spinner_first = true;
    private int current_order = 0;

    private int topic_id = 0;
    private String topic_url;
    private int user_id = 0;

    private PostDetailBean.PostBasicBean postBasicBean = new PostDetailBean.PostBasicBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        topic_id = getIntent().getIntExtra(Constants.Key.TOPIC_ID, Integer.MAX_VALUE);
        topic_url = Constants.Api.TOPIC_URL + topic_id;

        coordinatorLayout = findViewById(R.id.post_detail_coorlayout);
        post_detail_refresh = findViewById(R.id.post_detail_refresh);
        contentView = findViewById(R.id.post_detail_rl2);
        no_reply_hint = findViewById(R.id.no_reply_hint);
        create_comment_btn = findViewById(R.id.post_detail_create_comment_btn);
        create_comment_btn.setOnClickListener(this);
        up_btn = findViewById(R.id.post_detail_up_btn);
        up_btn.setOnClickListener(this);

        nestedScrollView = findViewById(R.id.pose_detail_scrollview);
        setOnScrollChange();

        post_detail_favorite = findViewById(R.id.post_detail_favorite);
        post_detail_favorite.setOnClickListener(this);

        error_text = findViewById(R.id.post_detail_error_text);
        error_img = findViewById(R.id.post_detail_error_img);
        error_rl = findViewById(R.id.post_detail_error_rl);
        CommonUtil.setVectorColor(this, error_img, R.drawable.ic_error, R.color.colorPrimary);
        error_rl.setOnClickListener(this);
        webview_rl = findViewById(R.id.post_detail_webview_rl);

        cardview2 = findViewById(R.id.post_detail_cardview2);
        zhanwei = findViewById(R.id.zhanwei_linearlayout);
        huadong = findViewById(R.id.post_detail_sticky_root_view);
        cardView1 = findViewById(R.id.post_detail_view);

        spinner = findViewById(R.id.post_detail_spinner);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        setOnSpinnerItemSelected();

        toolbar = findViewById(R.id.post_detail_toolbar);
        toolbar.setTitle("帖子详情");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        post_detail_title = findViewById(R.id.post_detail_title);
        post_detail_author_avatar = findViewById(R.id.post_detail_author_avatar);
        post_detail_author_avatar.setOnClickListener(this);
        post_detail_author_name = findViewById(R.id.post_detail_author_name);
        post_detail_author_time = findViewById(R.id.post_detail_author_time);
        post_detail_level = findViewById(R.id.post_detail_author_level);

        post_reply_recyclerview = findViewById(R.id.post_reply_rv);
        post_reply_recyclerview.setFocusableInTouchMode(false);
        post_reply_recyclerview.setNestedScrollingEnabled(false);
        postReplyAdapter = new PostReplyAdapter(this, R.layout.item_reply);
        postReplyAdapter.setHasStableIds(true);
        post_reply_recyclerview.setLayoutManager(new MyLinearLayoutManger(this));
        post_reply_recyclerview.setAdapter(postReplyAdapter);

        post_detail_refresh.autoRefresh(0, 300, 1, true);
        nestedScrollView.setVisibility(View.GONE);

        getData(1, 0, 0,true);
        setOnRefreshListener();
        setOnPostItemClick();

    }

    /**
     * author: TanLei
     * description: 帖子回复和查看个人详情以及评论和主题里的图片点击
     */

    private void setOnPostItemClick() {
        postReplyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.reply_button: //回复评论
                        ReplyDialog replyDialog = new ReplyDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.Key.BOARD_ID, postBasicBean.boardId);
                        bundle.putInt(Constants.Key.TOPIC_ID, postBasicBean.topic_id);
                        bundle.putInt(Constants.Key.QUOTE_ID, postReplyAdapter.getData().get(position).reply_posts_id);
                        bundle.putBoolean(Constants.Key.IS_QUOTE, true);
                        bundle.putString(Constants.Key.USER_NAME, postReplyAdapter.getData().get(position).reply_name);
                        replyDialog.setArguments(bundle);
                        replyDialog.show(getSupportFragmentManager(), TimeUtil.getStringMs());
                        break;

                    case R.id.post_reply_author_avatar: //转至个人详情
                        Intent intent = new Intent(PostDetailActivity.this, UserDetailActivity.class);
                        intent.putExtra(Constants.Key.USER_ID, postReplyAdapter.getData().get(position).reply_id);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

        postReplyAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.post_reply_root_rl:  //TODO 评论长按

                        break;

                    default:
                        break;
                }

                return false;
            }
        });

        //评论图片点击
        postReplyAdapter.setOnImgClickListener(new PostReplyAdapter.onImageClickListener() {
            @Override
            public void onImgClick(View view, List<String> urls, int selected) {
                ImageUtil.showImages(PostDetailActivity.this, (ImageView) view, urls, selected);
            }
        });


        //主题帖子图片点击
        contentView.setOnImageClickListener(new ContentView.OnImageClickListener() {
            @Override
            public void onImageClick(View view, List<String> urls, int selected) {
                ImageUtil.showImages(PostDetailActivity.this, (ImageView) view, urls, selected);
            }
        });

        //投票按钮点击
        contentView.setOnPollBtnClickListener(new ContentView.OnPollBtnClickListener() {
            @Override
            public void onPollBtnClick(List<Integer> ids) {
                int max = contentView.getVoteJson().getIntValue("type");
                if (ids.size() == 0) {
                    ToastUtil.showSnackBar(coordinatorLayout, "至少选择一项");
                } else if (ids.size() > max) {
                    ToastUtil.showSnackBar(coordinatorLayout, "最多选择" + max + "项");
                } else {
                    submitVote(ids);
                }
            }
        });

        //播放音频按钮点击
        contentView.setOnPlayClickListener(new ContentView.OnPlayClickListener() {
            @Override
            public void onPlayClick(String url) {
                MediaPlayerUtil.getMediaPlayer().playOrPause(url);
            }
        });
    }

    /**
     * author: sca_tl
     * description: nestedScrollView滑动监听
     */
    private void setOnScrollChange() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                //当滑动的距离大于cardview1到顶部的距离时，将cardview2添加到占位linearlayout布局中，
                //并将cardview2从可以随着屏幕滚动的linearlayout布局中移除。反之，做相反的处理，便能达到吸顶的效果
                if (scrollY > cardView1.getHeight()){
                    if (zhanwei.getChildCount() == 0) {
                        if (cardview2.getParent() != null) huadong.removeView(cardview2);
                        zhanwei.addView(cardview2);
                    }

                } else {
                    if (huadong.getChildCount() == 0) {
                        if (cardview2.getParent() != null) zhanwei.removeView(cardview2);
                        huadong.addView(cardview2);
                    }
                }

                //自动显示隐藏fab
                if (scrollY > oldScrollY && !create_comment_btn.isHidden()) {
                    create_comment_btn.hide(true);
                    up_btn.hide(true);
                } else if (scrollY < oldScrollY && create_comment_btn.isHidden()){
                    create_comment_btn.show(true);
                    up_btn.show(true);
                }

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post_detail_error_rl:
                post_detail_refresh.autoRefresh(0, 0, 1, true);
                getData(1, 0, 0,true);
                break;

            case R.id.post_detail_favorite:
                ToastUtil.showSnackBar(coordinatorLayout, "正在操作，请不要重复点击收藏按钮");
                favoritePost();
                break;

            case R.id.post_detail_author_avatar:
                Intent intent = new Intent(PostDetailActivity.this, UserDetailActivity.class);
                intent.putExtra(Constants.Key.USER_ID, user_id);
                startActivity(intent);
                break;

            case R.id.post_detail_create_comment_btn://发表评论
                ReplyDialog replyDialog = new ReplyDialog();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.Key.BOARD_ID, postBasicBean.boardId);
                bundle.putInt(Constants.Key.TOPIC_ID, postBasicBean.topic_id);
                bundle.putInt(Constants.Key.QUOTE_ID, 0);
                bundle.putBoolean(Constants.Key.IS_QUOTE, false);
                bundle.putString(Constants.Key.USER_NAME, postBasicBean.user_nick_name);
                replyDialog.setArguments(bundle);
                replyDialog.show(getSupportFragmentManager(), TimeUtil.getStringMs());
                break;

            case R.id.post_detail_up_btn:
                nestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
                break;

            default:
                break;
        }
    }

    /**
     * author: TanLei
     * description: 收藏帖子
     */
    private void favoritePost() {
        Map<String, String> map = new HashMap<>();
        map.put("idType", "tid");
        map.put("id", topic_id + "");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());
        map.put("action", postBasicBean.is_favor == 1 ? "delfavorite" : "favorite");

        HttpRequestUtil.post(Constants.Api.SET_POST_FAVORITE, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(coordinatorLayout,
                        postBasicBean.is_favor == 1 ? "取消收藏失败" : "收藏失败");
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
                if (rs == 1) {
                    if (postBasicBean.is_favor == 1) {  //取消收藏
                        ToastUtil.showSnackBar(coordinatorLayout, "取消收藏成功" );
                        CommonUtil.setVectorColor(PostDetailActivity.this, post_detail_favorite, R.drawable.ic_not_favorite, R.color.colorPrimary);
                        postBasicBean.is_favor = 0;
                    } else {  //收藏
                        ToastUtil.showSnackBar(coordinatorLayout,"收藏成功" );
                        CommonUtil.setVectorColor(PostDetailActivity.this, post_detail_favorite, R.drawable.ic_favorite, R.color.colorPrimary);
                        postBasicBean.is_favor = 1;
                    }
                } else {
                    String error = jsonObject.getString("errcode");
                    if (postBasicBean.is_favor == 1) {  //取消收藏
                        ToastUtil.showSnackBar(coordinatorLayout, "取消收藏失败，" + error );
                    } else {   //收藏
                        ToastUtil.showSnackBar(coordinatorLayout, "收藏失败，"+ error );
                    }
                }
            }
        });
    }

    /**
     * author: sca_tl
     * description: 发表投票
     */
    private void submitVote(List<Integer> ids) {
        Map<String, String> map = new HashMap<>();
        map.put("tid", postBasicBean.topic_id + "");
        map.put("boardId", postBasicBean.boardId + "");
        map.put("options", ids.toString().replace("[", "").replace("]", ""));
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        final ProgressDialog progressDialog = ProgressDialog.show(this, "投票", "正在处理，请稍后");

        HttpRequestUtil.post(Constants.Api.VOTE, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                ToastUtil.showSnackBar(coordinatorLayout,"投票失败：" + e.getMessage());
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                progressDialog.dismiss();
                JSONObject jsonObject = JSONObject.parseObject(response);
                ToastUtil.showSnackBar(coordinatorLayout, jsonObject.getString("errcode"));
            }
        });
    }

    /**
     * author: sca_tl
     * description: 获取帖子数据
     * @param page 页数
     * @param refresh 是否刷新
     * @param order 顺序，0：按时间正序（最早的帖子在最前面），1：按时间倒序
     * @param author_id 返回指定作者的回复，默认为0返回所有回复
     */
    private void getData(final int page, int order, int author_id, final boolean refresh) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", "25");
        map.put("topicId", topic_id + "");
        map.put("order", order + "");
        map.put("authorId", author_id + "");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_POST_DETAIL, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                post_detail_refresh.finishRefresh();
                if (! isRefreshed) {
                    nestedScrollView.setVisibility(View.GONE);
                    error_rl.setVisibility(View.VISIBLE);
                    isRefreshed = false;
                    if (JSON.isValidObject(e.getMessage()) && Constants.RESPONSE_ERROR_500_TOPIC.equals(JSONObject.parseObject(e.getMessage()).get("response"))) {
                        webview_rl.setVisibility(View.VISIBLE);
                        agentWeb = AgentWeb.with(PostDetailActivity.this)
                            .setAgentWebParent(webview_rl, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                            .useDefaultIndicator()
                            .createAgentWeb()
                            .ready()
                            .go(topic_url);
                        ToastUtil.showSnackBar(coordinatorLayout,"响应500，为您打开浏览器查看帖子");
                    } else {
                        webview_rl.setVisibility(View.GONE);
                        ToastUtil.showSnackBar(coordinatorLayout,e.getMessage());
                    }
                }
                post_detail_refresh.setEnableLoadMore(false);
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
     * author: TanLei
     * description: 解析帖子数据
     */
    private void resolveData(String data, boolean refresh) {
        post_detail_refresh.finishRefresh();
        JSONObject jsonObject = JSONObject.parseObject(data);
        int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。

        if (rs == 1) {
            nestedScrollView.setVisibility(View.VISIBLE);
            error_rl.setVisibility(View.GONE);
            post_detail_refresh.setEnableLoadMore(true);

            if (jsonObject.getIntValue("has_next") == 0) {
                post_detail_refresh.finishLoadMoreWithNoMoreData();
            } else {
                post_detail_refresh.finishLoadMore(true);
            }

            setBasicData(jsonObject);   //加载基本数据

            if (page == 1 && ! isRefreshed) {  //加载帖子内容数据，帖子内容只加载一次
                JSONArray jsonArray = jsonObject.getJSONObject("topic").getJSONArray("content");
                if (jsonArray != null) {
                    List<PostDetailBean.PostContentBean> postContentBeanList =
                            JSON.parseObject(jsonArray.toString(),
                                    new TypeReference<List<PostDetailBean.PostContentBean>>(){});
                    postBasicBean.postContentBeanList.addAll(postContentBeanList);
                    contentView.setContentData(postBasicBean.postContentBeanList);
                }
            }

            setReplyData(jsonObject.getJSONArray("list"), page, refresh);  //加载回复数据

            isRefreshed = true;

        } else {
            String error = jsonObject.getString("errcode");
            nestedScrollView.setVisibility(View.GONE);
            error_rl.setVisibility(View.VISIBLE);
            error_text.setText(error);
            post_detail_refresh.setEnableLoadMore(false);
        }

    }

    /**
     * author: sca_tl
     * description: 帖子基本数据，楼主头像，标题，时间等
     * page=1时服务器才会返回帖子基本数据
     */
    private void setBasicData(JSONObject jsonObject1) {

        if (page == 1) {
            JSONObject jsonObject = jsonObject1.getJSONObject("topic");

            postBasicBean.user_id = jsonObject.getInteger("user_id");
            postBasicBean.title = jsonObject.getString("title");
            postBasicBean.user_nick_name = jsonObject.getString("user_nick_name");
            postBasicBean.userTitle = jsonObject.getString("userTitle");
            postBasicBean.icon = jsonObject.getString("icon");
            postBasicBean.is_favor = jsonObject.getIntValue("is_favor");
            postBasicBean.create_date = jsonObject.getString("create_date");
            postBasicBean.topic_id = jsonObject.getIntValue("topic_id");
            postBasicBean.vote = jsonObject.getIntValue("vote");

            //若是投票帖
            if (postBasicBean.vote == 1) {
                contentView.setVoteJson(jsonObject.getJSONObject("poll_info"));
            }

            postBasicBean.forumTopicUrl = jsonObject1.getString("forumTopicUrl");
            postBasicBean.forumName = jsonObject1.getString("forumName");
            postBasicBean.boardId = jsonObject1.getIntValue("boardId");


            user_id = jsonObject.getIntValue("user_id");
            postReplyAdapter.setAuthorId(user_id);

            post_detail_title.setText(jsonObject.getString("title"));
            post_detail_author_name.setText(jsonObject.getString("user_nick_name"));
            //post_detail_level.setText(jsonObject.getString("userTitle"));
            if (! PostDetailActivity.this.isFinishing()) {
                Glide.with(this).load(jsonObject.getString("icon")).into(post_detail_author_avatar);
            }

            if (jsonObject.getIntValue("is_favor") == 1) {  //收藏了该帖子
                CommonUtil.setVectorColor(this, post_detail_favorite, R.drawable.ic_favorite, R.color.colorPrimary);
            } else {
                CommonUtil.setVectorColor(this, post_detail_favorite, R.drawable.ic_not_favorite, R.color.colorPrimary);
            }

            post_detail_author_time.setText(TimeUtil.timeFormat1(jsonObject.getString("create_date"), R.string.post_time1, this));

            if (postBasicBean.userTitle != null && postBasicBean.userTitle.length() != 0 ) {
                Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(postBasicBean.userTitle);
                if (matcher.find()) {
                    String level = matcher.group(2);
                    post_detail_level.setText(level);

                } else {
                    post_detail_level.setText(postBasicBean.userTitle);
                }
                post_detail_level.setBackgroundResource(R.drawable.shape_textview_level_reply);
            } else if (postBasicBean.userTitle != null) {
                post_detail_level.setText(postBasicBean.user_nick_name);
                post_detail_level.setBackgroundResource(R.drawable.shape_textview_level_reply);
            }
        }

    }

    /**
     * author: sca_tl
     * description: 帖子回复数据
     */
    private void setReplyData(JSONArray jsonArray, int page, final boolean refresh) {

        final List<PostDetailBean.PostReplyBean> postReplyBeanList = new ArrayList<>();

        if (jsonArray.size() == 0) {  //没有评论
            if (page == 1) {
                post_reply_recyclerview.setVisibility(View.GONE);
                no_reply_hint.setVisibility(View.VISIBLE);
            }
        } else {
            post_reply_recyclerview.setVisibility(View.VISIBLE);
            no_reply_hint.setVisibility(View.GONE);
            for (int i = 0; i < jsonArray.size(); i ++) {
                PostDetailBean.PostReplyBean postReplyBean = new PostDetailBean.PostReplyBean();
                postReplyBean.reply_id = jsonArray.getJSONObject(i).getIntValue("reply_id");
                postReplyBean.reply_type = jsonArray.getJSONObject(i).getString("reply_type");
                postReplyBean.reply_name = jsonArray.getJSONObject(i).getString("reply_name");
                postReplyBean.reply_posts_id = jsonArray.getJSONObject(i).getIntValue("reply_posts_id");
                postReplyBean.poststick = jsonArray.getJSONObject(i).getIntValue("poststick");
                postReplyBean.position = jsonArray.getJSONObject(i).getIntValue("position");
                postReplyBean.posts_date = jsonArray.getJSONObject(i).getString("posts_date");
                postReplyBean.icon = jsonArray.getJSONObject(i).getString("icon");
                postReplyBean.level = jsonArray.getJSONObject(i).getIntValue("level");
                postReplyBean.userTitle = jsonArray.getJSONObject(i).getString("userTitle");
                postReplyBean.userColor = jsonArray.getJSONObject(i).getString("userColor");
                postReplyBean.location = jsonArray.getJSONObject(i).getString("location");
                postReplyBean.mobileSign = jsonArray.getJSONObject(i).getString("mobileSign");
                postReplyBean.reply_status = jsonArray.getJSONObject(i).getIntValue("reply_status");
                postReplyBean.status = jsonArray.getJSONObject(i).getIntValue("status");
                postReplyBean.role_num = jsonArray.getJSONObject(i).getIntValue("role_num");
                postReplyBean.title = jsonArray.getJSONObject(i).getString("title");
                postReplyBean.gender = jsonArray.getJSONObject(i).getIntValue("gender");
                postReplyBean.is_quote = jsonArray.getJSONObject(i).getIntValue("is_quote");
                postReplyBean.quote_pid = jsonArray.getJSONObject(i).getIntValue("quote_pid");
                postReplyBean.quote_content = jsonArray.getJSONObject(i).getString("quote_content");
                postReplyBean.quote_user_name = jsonArray.getJSONObject(i).getString("quote_user_name");
                postReplyBean.delThread = jsonArray.getJSONObject(i).getBooleanValue("delThread");

                JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("reply_content");
                for (int j = 0; j < jsonArray1.size(); j ++) {
                    PostDetailBean.PostContentBean postContentBean = new PostDetailBean.PostContentBean();
                    postContentBean.aid = jsonArray1.getJSONObject(j).getIntValue("aid");
                    postContentBean.desc = jsonArray1.getJSONObject(j).getString("desc");
                    postContentBean.infor = jsonArray1.getJSONObject(j).getString("infor");
                    postContentBean.originalInfo = jsonArray1.getJSONObject(j).getString("originalInfo");
                    postContentBean.type = jsonArray1.getJSONObject(j).getIntValue("type");
                    postContentBean.url = jsonArray1.getJSONObject(j).getString("url");

                    postReplyBean.postContentBeanList.add(postContentBean);
                }
                postReplyBeanList.add(postReplyBean);

            }

            if (page != 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postReplyAdapter.addPostReplyData(postReplyBeanList, refresh);
                    }
                }, 600);
            } else {
                postReplyAdapter.addPostReplyData(postReplyBeanList, refresh);
            }
        }



    }

    /**
     * author: sca_tl
     * description: spinneritem选中
     */
    private void setOnSpinnerItemSelected(){
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner_first) {     //防止一初始化就触发监听事件
                    spinner_first = false;
                } else {
                    //回到顶部
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            nestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
                            //nestedScrollView.smoothScrollTo(0, cardView1.getHeight());
                        }
                    });
                    post_detail_refresh.autoRefresh(0, 300, 1, true);
                    switch (i) {
                        case 0:  //按时间正序
                            current_order = 0;
                            getData(1, 0, 0, true);
                            break;
                        case 1:  //按时间倒序
                            current_order = 1;
                            getData(1, 1, 0, true);
                            break;
                        case 2:  //只看楼主
                            current_order = 2;
                            getData(1, 0, user_id, true);
                            break;

                        default:
                            break;
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * author: sca_tl
     * description: 上拉下拉刷新监听
     */
    private void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(this, post_detail_refresh, new OnRefresh() {

            //刷新，则获取第一页数据，并将page=1
            //还要判断一下选的查看类型是什么
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                switch (current_order) {
                    case 0:  //按时间正序
                        getData(page, 0, 0,true);
                        break;
                    case 1:  //按时间倒序
                        getData(page, 1, 0,true);
                        break;
                    case 2:  //只看楼主
                        getData(page, 0, user_id, true);
                        break;

                    default:
                        break;
                }
            }

            //加载更多，page递增
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                page = page + 1;
                switch (current_order) {
                    case 0:  //按时间正序
                        getData(page, 0, 0,false);
                        break;
                    case 1:  //按时间倒序
                        getData(page, 1, 0,false);
                        break;
                    case 2:  //只看楼主
                        getData(page, 0, user_id, false);
                        break;

                    default:
                        break;
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_post_detail_report_thread:  //举报帖子
                ReportDialog.show(this, Constants.Api.REPORT_TYPE_THREAD, topic_id);
                break;

            case R.id.menu_post_detail_favorite_post:
                favoritePost();
                break;

            case R.id.menu_post_detail_share_post:
                String title = getResources().getString(R.string.share_title, postBasicBean.title);
                String content = getResources().getString(R.string.share_content,
                        postBasicBean.title, postBasicBean.forumTopicUrl);
                CommonUtil.share(this, title, content);
                break;

            case R.id.menu_post_detail_copy_link:
                CommonUtil.clipToClipBoard(this, postBasicBean.forumTopicUrl);
                break;

            case R.id.menu_post_detail_open_link:
                CommonUtil.openBrowser(this, postBasicBean.forumTopicUrl);
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SEND_REPLY_SUCCEED) {
            nestedScrollView.fullScroll(NestedScrollView.FOCUS_UP);
            post_detail_refresh.autoRefresh();
        }
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
    protected void onStart() {
        super.onStart();
        if (! EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        if (agentWeb != null) agentWeb.getWebLifeCycle().onDestroy();
        MediaPlayerUtil.getMediaPlayer().stopPlay();
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

}