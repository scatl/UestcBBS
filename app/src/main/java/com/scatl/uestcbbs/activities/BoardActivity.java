package com.scatl.uestcbbs.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.just.agentweb.AgentWeb;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.CommonPagerAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.custom.dialogs.ChangeBoardAndCatDialog;
import com.scatl.uestcbbs.entities.ForumListBean;
import com.scatl.uestcbbs.fragments.BoardFragment;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.ImageUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class BoardActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BoardActivity";

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private MagicIndicator magicIndicator;
    private ViewPager viewPager;
    private ImageView board_background;
    private ImageView board_icon, sub_board_icon, cat_icon;
    private TextView board_today_post, board_name, board_cat_tv;
    private RelativeLayout board_rl1;
    private FloatingActionButton create_post;
    private LinearLayout webview;
    private AgentWeb agentWeb;

    private ForumListBean.BoardBean.BoardListBean boardListBean;

    //所有的子版块，包括母版块
    private List<ForumListBean.BoardBean.BoardListBean> subBoardList = new ArrayList<>();
    //板块的所有分类信息，第一个为‘全部’
    private List<ForumListBean.BoardBean.BoardListBean.BoardCatBean> boardCatBeanList = new ArrayList<>();

    private ArrayList<String> sub_board_item = new ArrayList<>();   //所有的子版块
    private ArrayList<String> cat_item = new ArrayList<>(); //所有的分类

    private int current_sub_board_select = 0; //当前选中的子版块
    private int current_cat_select = 0;   //当前选中的分类

    private int current_board_id; //当前板块id
    private int current_cat_id;  //当前板块下的分类id

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        boardListBean = (ForumListBean.BoardBean.BoardListBean) getIntent().getSerializableExtra(Constants.Key.DATA);

        appBarLayout = findViewById(R.id.board_app_bar);
        coordinatorLayout = findViewById(R.id.board_coorlayout);
        collapsingToolbarLayout = findViewById(R.id.board_toolbar_layout);
        toolbar = findViewById(R.id.board_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        magicIndicator = findViewById(R.id.board_indicator);
        viewPager = findViewById(R.id.board_viewpager);
        board_background = findViewById(R.id.board_background);
        board_icon = findViewById(R.id.board_icon);
        board_today_post = findViewById(R.id.board_today_post);
        board_name = findViewById(R.id.board_name);
        board_cat_tv = findViewById(R.id.board_cat_tv);
        board_cat_tv.setOnClickListener(this);
        cat_icon = findViewById(R.id.board_cat_img);
        cat_icon.setOnClickListener(this);
        sub_board_icon = findViewById(R.id.board_subboard_icon);
        sub_board_icon.setOnClickListener(this);
        board_rl1 = findViewById(R.id.board_rl1);
        create_post = findViewById(R.id.board_create_post_btn);
        create_post.setOnClickListener(this);
        webview = findViewById(R.id.board_webview);

        current_board_id = boardListBean.board_id;
        current_cat_id = 0;
        subBoardList.add(boardListBean);

        initBoardInfo(current_sub_board_select);
        setOnCtlStateListener();
        if (CommonUtil.isArrayContainsValue(Constants.RESPONSE_ERROR_500_BOARD, boardListBean.board_id)) {
            appBarLayout.setExpanded(false, false);
            ToastUtil.showSnackBar(coordinatorLayout, "板块响应为空，为您打开浏览器浏览帖子");
            agentWeb = AgentWeb.with(this)
                    .setAgentWebParent(webview, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                    .useDefaultIndicator()
                    .createAgentWeb()
                    .ready()
                    .go(Constants.Api.BOARD_URL + boardListBean.board_id);
        } else {
            initIndicator();
            getSubBoardData();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.board_subboard_icon: //选择子版块
                showSubBoardChoose();
                break;

            case R.id.board_cat_tv:  //选择分类
            case R.id.board_cat_img:
                showCatChoose();
                break;

            case R.id.board_create_post_btn:  //发表帖子
                startActivity(new Intent(BoardActivity.this, CreatePostActivity.class));
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description: 显示选择子版块对话框
     */
    private void showSubBoardChoose() {
        ChangeBoardAndCatDialog changeBoardAndCatDialog = new ChangeBoardAndCatDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Key.TYPE, ChangeBoardAndCatDialog.TYPE_SUB_BOARD);
        bundle.putInt(Constants.Key.CURRENT_SELECT, current_sub_board_select);
        bundle.putStringArrayList(Constants.Key.DATA, sub_board_item);
        changeBoardAndCatDialog.setArguments(bundle);
        changeBoardAndCatDialog.show(getSupportFragmentManager(), System.currentTimeMillis() + "");
    }

    /**
     * author: sca_tl
     * description: 显示选择分类对话框
     */
    private void showCatChoose() {
        ChangeBoardAndCatDialog changeBoardAndCatDialog = new ChangeBoardAndCatDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Key.TYPE, ChangeBoardAndCatDialog.TYPE_CAT);
        bundle.putInt(Constants.Key.CURRENT_SELECT, current_cat_select);
        bundle.putStringArrayList(Constants.Key.DATA, cat_item);
        changeBoardAndCatDialog.setArguments(bundle);
        changeBoardAndCatDialog.show(getSupportFragmentManager(), System.currentTimeMillis() + "");
    }

    /**
     * author: sca_tl
     * description: 板块信息
     */
    private void initBoardInfo(int i) {
        if (! BoardActivity.this.isFinishing()) {
            String ccc = Constants.Api.BOARD_IMAGE_500_500 + boardListBean.board_id + ".jpg";
            String ddd = Constants.Api.BOARD_IMAGE_600_400 + boardListBean.board_id + ".jpg";
            Glide.with(this).load(ccc).into(board_icon);
            Glide.with(this).load(ddd).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    collapsingToolbarLayout.setBackground(ImageUtil.bitmap2Drawable(ImageUtil.blurPhoto(BoardActivity.this, ImageUtil.drawable2Bitmap(resource), 15f)));
                }
            });
        }
        board_name.setText(subBoardList.get(i).board_name);
        board_today_post.setText(getResources().getString(R.string.board_info, subBoardList.get(i).td_posts_num));
    }

    /**
     * author: sca_tl
     * description: 初始化指示器
     */
    private void initIndicator() {

        final String[] titles = {"最新", "全部", "精华"};
        ArrayList<Fragment> fragments = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.Key.SORT_BY, BoardFragment.BOARD_SORTBY_NEW);
        bundle.putString(Constants.Key.BOARD_ID, current_board_id + "");
        bundle.putString(Constants.Key.FILTER_ID, current_cat_id + "");
        fragments.add(BoardFragment.newInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constants.Key.SORT_BY, BoardFragment.BOARD_SORTBY_ALL);
        bundle1.putString(Constants.Key.BOARD_ID, current_board_id + "");
        bundle1.putString(Constants.Key.FILTER_ID, current_cat_id + "");
        fragments.add(BoardFragment.newInstance(bundle1));

        Bundle bundle2 = new Bundle();
        bundle2.putString(Constants.Key.SORT_BY, BoardFragment.BOARD_SORTBY_ESSENCE);
        bundle2.putString(Constants.Key.BOARD_ID, current_board_id + "");
        bundle2.putString(Constants.Key.FILTER_ID, current_cat_id + "");
        fragments.add(BoardFragment.newInstance(bundle2));

        viewPager.setOffscreenPageLimit(2);

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new CommonPagerAdapter(fragmentManager, titles, fragments));
        viewPager.setCurrentItem(0);

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() { return titles.length; }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                ColorTransitionPagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(titles[i]);
                simplePagerTitleView.setTextSize(17);
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(CommonUtil.getAttrColor(BoardActivity.this, R.attr.colorPrimary));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(i);
                    }
                });
                return simplePagerTitleView;

            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setLineHeight(7);
                indicator.setXOffset(20);
                indicator.setRoundRadius(10);
                indicator.setYOffset(10);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(CommonUtil.getAttrColor(BoardActivity.this, R.attr.colorPrimary));

                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    /**
     * author: sca_tl
     * description: 通知fragment数据更新
     */
    private void sendEventMsg() {
        BaseEvent.BoardChange boardChange = new BaseEvent.BoardChange();
        boardChange.boardId = current_board_id;
        boardChange.catId = current_cat_id;
        EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.BOARD_CHANGED, boardChange));
    }

    /**
     * author: sca_tl
     * description: 获取该板块下的所有子版块
     */
    private void getSubBoardData() {
        Map<String, String> map = new HashMap<>();
        map.put("fid", boardListBean.board_id + "");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_FORUM_LIST, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                resolveSubBoardData(response);
            }
        });
    }

    /**
     * author: sca_tl
     * description: 所有的子版块信息
     */
    private void resolveSubBoardData(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。

        subBoardList.clear();
        subBoardList.add(boardListBean); //母板块信息

        if (rs == 1) {
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            if (jsonArray != null && jsonArray.size() == 1) {
                JSONArray jsonArray1 = jsonArray.getJSONObject(0).getJSONArray("board_list");
                if (jsonArray1 != null) {
                    List<ForumListBean.BoardBean.BoardListBean> listBeans =
                            JSON.parseObject(jsonArray1.toString(),
                            new TypeReference<List<ForumListBean.BoardBean.BoardListBean>>(){});
                    subBoardList.addAll(listBeans);
                }
            }

            sub_board_item.clear();
            for (int k = 0; k < subBoardList.size(); k ++) { sub_board_item.add(subBoardList.get(k).board_name); }

        }
    }

    /**
     * author: sca_tl
     * description: 监听collapsingToolbarLayout状态
     */
    private void setOnCtlStateListener() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float alpha = 1 - (1.0f * (- i)) / scrollRange;
                board_rl1.setAlpha(alpha);
                toolbar.setTitle(scrollRange == (-i) ? subBoardList.get(current_sub_board_select).board_name : " ");
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unchecked")
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.BOARD_CAT_INIT) {
            List<ForumListBean.BoardBean.BoardListBean.BoardCatBean> boardCatBeanList
                    = (List<ForumListBean.BoardBean.BoardListBean.BoardCatBean>)baseEvent.eventData;
            this.boardCatBeanList = boardCatBeanList;
            cat_item.clear();
            for (int j = 0; j < boardCatBeanList.size(); j ++) {cat_item.add(boardCatBeanList.get(j).classificationType_name);}
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.BOARD_CAT_CHANGE) {
            int cat_select = (int)baseEvent.eventData;
            current_cat_select = cat_select;
            current_cat_id = boardCatBeanList.get(cat_select).classificationType_id;
            board_cat_tv.setText(boardCatBeanList.get(cat_select).classificationType_name);
            sendEventMsg();
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.SUB_BOARD_CHANGE) {
            int sub_board_select = (int)baseEvent.eventData;
            current_sub_board_select = sub_board_select;
            current_cat_select = 0;
            board_cat_tv.setText("全部");

            current_board_id = subBoardList.get(sub_board_select).board_id;
            current_cat_id = 0;

            initBoardInfo(current_sub_board_select);
            sendEventMsg();
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (agentWeb != null && agentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }


}
