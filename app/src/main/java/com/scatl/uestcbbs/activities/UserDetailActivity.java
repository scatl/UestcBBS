package com.scatl.uestcbbs.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.CommonPagerAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.dialogs.ReportDialog;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.UserDetailBean;
import com.scatl.uestcbbs.fragments.UserPostFragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class UserDetailActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "UserDetailActivity";

    private CircleImageView user_icon;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private ImageView user_background;
    private TextView user_name, user_sign, user_follow, user_followed, user_gender, user_level, user_error;
    private Button favorite_user; //关注用户
    private ImageButton mail_to_user;  //私信用户
    private LinearLayout user_detail_indicator_rl;
    private MagicIndicator user_detail_indicator;
    private ViewPager user_detail_viewpager;
    private RelativeLayout user_detail_rl1;
    private Toolbar toolbar;

    private UserDetailBean userDetailBean = new UserDetailBean();
    private List<UserDetailBean.BodyBean.CreditBean> creditBeans = new ArrayList<>();
    private List<UserDetailBean.BodyBean.ProfileBean> profileBeans = new ArrayList<>();
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        init();
    }

    /**
     * author: TanLei
     * description: 初始化
     */
    private void init() {
        user_id = getIntent().getIntExtra(Constants.Key.USER_ID, Integer.MAX_VALUE);

        user_icon = findViewById(R.id.user_detail_user_icon);
        coordinatorLayout = findViewById(R.id.user_detail_coorlayout);
        collapsingToolbarLayout = findViewById(R.id.user_detail_toolbar_layout);
        appBarLayout = findViewById(R.id.user_detail_app_bar);
        user_background = findViewById(R.id.user_detail_user_background);
        user_name = findViewById(R.id.user_detail_user_name);
        user_name.setOnClickListener(this);
        user_sign = findViewById(R.id.user_detail_user_sign);
        user_sign.setOnClickListener(this);
        user_follow = findViewById(R.id.user_detail_follow_num);
        user_follow.setOnClickListener(this);
        user_followed = findViewById(R.id.user_detail_followed_num);
        user_followed.setOnClickListener(this);
        user_gender = findViewById(R.id.user_detail_user_gender);
        user_level = findViewById(R.id.user_detail_user_level);
        favorite_user = findViewById(R.id.user_detail_favorite_btn);
        favorite_user.setOnClickListener(this);
        mail_to_user = findViewById(R.id.user_detail_mail_btn);
        mail_to_user.setOnClickListener(this);
        user_detail_indicator_rl = findViewById(R.id.user_detail_indicator_rl);
        user_detail_indicator = findViewById(R.id.user_detail_indicator);
        user_detail_viewpager = findViewById(R.id.user_detail_viewpager);
        user_detail_rl1 = findViewById(R.id.user_detail_rl1);
        user_error = findViewById(R.id.user_detail_error_text);

        toolbar = findViewById(R.id.user_detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (user_id == SharePrefUtil.getId(this)) {
            favorite_user.setVisibility(View.GONE);
            mail_to_user.setVisibility(View.GONE);
        }

        setOnCtlStateListener();
        getUserData();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_detail_favorite_btn:  //关注用户
                favoriteUser();
                break;

            case R.id.user_detail_follow_num:  //用户关注的
                Intent intent = new Intent(UserDetailActivity.this, UserFollowActivity.class);
                intent.putExtra(Constants.Key.TYPE, UserFollowActivity.TYPE_FOLLOW);
                intent.putExtra(Constants.Key.USER_ID, user_id);
                intent.putExtra(Constants.Key.USER_NAME, userDetailBean.name);
                startActivity(intent);
                break;

            case R.id.user_detail_followed_num:  //关注用户的
                Intent intent1 = new Intent(UserDetailActivity.this, UserFollowActivity.class);
                intent1.putExtra(Constants.Key.TYPE, UserFollowActivity.TYPE_FOLLOWED);
                intent1.putExtra(Constants.Key.USER_ID, user_id);
                intent1.putExtra(Constants.Key.USER_NAME, userDetailBean.name);
                startActivity(intent1);
                break;

            case R.id.user_detail_user_name:
                CommonUtil.clipToClipBoard(UserDetailActivity.this, userDetailBean.name);
                ToastUtil.showSnackBar(coordinatorLayout, "复制用户名成功");
                break;

            case R.id.user_detail_user_sign:
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(userDetailBean.name + "的签名")
                        .setMessage(userDetailBean.sign)
                        .setPositiveButton("复制", null)
                        .setNegativeButton("取消", null)
                        .create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        p.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CommonUtil.clipToClipBoard(UserDetailActivity.this, userDetailBean.sign);
                                ToastUtil.showSnackBar(coordinatorLayout, "复制签名成功");
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.show();
                break;

            case R.id.user_detail_mail_btn:
                Intent intent2 = new Intent(UserDetailActivity.this, PrivateChatActivity.class);
                intent2.putExtra(Constants.Key.USER_ID, user_id);
                intent2.putExtra(Constants.Key.USER_NAME, userDetailBean.name);
                startActivity(intent2);
                break;

            default:
                break;
        }
    }

    /**
     * author: TanLei
     * description: 关注用户
     */
    private void favoriteUser() {
        Map<String, String> map = new HashMap<>();
        map.put("uid", user_id + "");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());
        map.put("type", userDetailBean.is_follow == 1 ? "unfollow" : "follow");

        HttpRequestUtil.post(Constants.Api.FOLLOW_USER, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(coordinatorLayout, userDetailBean.is_follow == 1 ? "取消关注失败" : "关注失败");
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                JSONObject jsonObject = JSONObject.parseObject(response);
                int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
                if (rs == 1) {
                    favorite_user.setText(userDetailBean.is_follow == 1 ? "关注" : "已关注");
                    userDetailBean.is_follow = userDetailBean.is_follow == 1 ? 0 : 1;
                }

                ToastUtil.showSnackBar(coordinatorLayout, jsonObject.getString("errcode"));
            }
        });

    }

    /**
     * author: TanLei
     * description: 获取用户数据
     */
    private void getUserData() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", user_id + "");
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_USER_INFO, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                ToastUtil.showSnackBar(coordinatorLayout, getResources().getString(R.string.request_error));
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                resolveUserData(response);
            }
        });
    }

    /**
     * author: TanLei
     * description: 解析用户数据
     */
    private void resolveUserData(String data) {

        JSONObject jsonObject = JSONObject.parseObject(data);
        userDetailBean = JSON.parseObject(data, new TypeReference<UserDetailBean>(){});
        if (userDetailBean.rs == 1) {
            creditBeans = JSON.parseObject(jsonObject.getJSONObject("body").getJSONArray("creditList").toString(),
                    new TypeReference<List<UserDetailBean.BodyBean.CreditBean>>(){});

            profileBeans = JSON.parseObject(jsonObject.getJSONObject("body").getJSONArray("profileList").toString(),
                    new TypeReference<List<UserDetailBean.BodyBean.ProfileBean>>(){});
            setUserData();
        } else {
            String err = jsonObject.getString("errcode");
            appBarLayout.setVisibility(View.GONE);
            user_error.setVisibility(View.VISIBLE);
            user_error.setText(err);
        }
    }

    /**
     * author: TanLei
     * description: 设置用户数据
     */
    private void setUserData() {

        user_detail_rl1.setVisibility(View.VISIBLE);

        if (! UserDetailActivity.this.isFinishing()) {
            Glide.with(this).load(userDetailBean.icon).into(user_icon);
            Glide.with(this).load(userDetailBean.icon).into(new SimpleTarget<Drawable>() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    if (resource instanceof GifDrawable) {
                        Bitmap bitmap = ((GifDrawable) resource).getFirstFrame();
                        collapsingToolbarLayout
                                .setBackground(ImageUtil.bitmap2Drawable
                                        (ImageUtil.blurPhoto(UserDetailActivity.this, bitmap, 25f)));
                    } else {
                        collapsingToolbarLayout
                                .setBackground(ImageUtil.bitmap2Drawable
                                        (ImageUtil.blurPhoto(UserDetailActivity.this, ImageUtil.drawable2Bitmap(resource), 25f)));
                    }
                }
            });

        }
        user_name.setText(userDetailBean.name);
        user_follow.setText("关注：" + userDetailBean.friend_num);
        user_followed.setText("粉丝：" + userDetailBean.follow_num);
        favorite_user.setText(userDetailBean.is_follow == 1 ? "已关注" : "关注");

        if (userDetailBean.gender == 0) {  //保密
            user_gender.setVisibility(View.GONE);
            user_sign.setText(userDetailBean.sign == null || userDetailBean.sign.length() == 0 ? "Ta还未设置签名" : userDetailBean.sign);

        } else if (userDetailBean.gender == 1) {  //男
            user_gender.setVisibility(View.VISIBLE);
            user_gender.setText("♂");
            user_gender.setBackgroundResource(R.drawable.shape_textview_gender_male);
            user_sign.setText(userDetailBean.sign == null || userDetailBean.sign.length() == 0 ? "他还未设置签名" : userDetailBean.sign);

        } else if (userDetailBean.gender == 2) { //女
            user_gender.setVisibility(View.VISIBLE);
            user_gender.setText("♀");
            user_gender.setBackgroundResource(R.drawable.shape_textview_gender_female);
            user_sign.setText(userDetailBean.sign == null || userDetailBean.sign.length() == 0 ? "她还未设置签名" : userDetailBean.sign);

        }

        if (userDetailBean.userTitle != null || userDetailBean.userTitle.length() != 0 ) {
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(userDetailBean.userTitle);
            if (matcher.find()) {
                String level = matcher.group(2);
                user_level.setText(level);

            } else {
                user_level.setText(userDetailBean.userTitle);
            }
            user_level.setBackgroundResource(R.drawable.shape_textview_level);
        }

        initIndicator();
    }

    /**
     * author: sca_tl
     * description: 指示器
     */
    private void initIndicator() {
        String t1 = "发表(" + userDetailBean.topic_num + ")";
        String t2 = "回复(" + userDetailBean.reply_posts_num + ")";
        final String[] titles = {t1, t2};
        ArrayList<Fragment> fragments = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.Key.TYPE, UserPostFragment.TYPE_POST);
        bundle.putInt(Constants.Key.USER_ID, user_id);
        fragments.add(UserPostFragment.newInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constants.Key.TYPE, UserPostFragment.TYPE_REPLY);
        bundle1.putInt(Constants.Key.USER_ID, user_id);
        fragments.add(UserPostFragment.newInstance(bundle1));

        user_detail_viewpager.setOffscreenPageLimit(2);

        FragmentManager fragmentManager = getSupportFragmentManager();
        user_detail_viewpager.setAdapter(new CommonPagerAdapter(fragmentManager, titles, fragments));
        user_detail_viewpager.setCurrentItem(0);

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
                simplePagerTitleView.setSelectedColor(CommonUtil.getAttrColor(UserDetailActivity.this, R.attr.colorPrimary));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user_detail_viewpager.setCurrentItem(i);
                    }
                });
                return simplePagerTitleView;


            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setLineHeight(7);
                indicator.setXOffset(50);
                indicator.setRoundRadius(10);
                indicator.setYOffset(10);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(CommonUtil.getAttrColor(UserDetailActivity.this, R.attr.colorPrimary));

                return indicator;
            }
        });

        user_detail_indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(user_detail_indicator, user_detail_viewpager);
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
                user_gender.setAlpha(alpha);  //0完全透明，看不见；1完全不透明
                user_level.setAlpha(alpha);
                user_follow.setAlpha(alpha);
                user_followed.setAlpha(alpha);
                user_sign.setAlpha(alpha);
                user_name.setAlpha(alpha);
                user_icon.setAlpha(alpha);

                toolbar.setTitle(scrollRange == (-i) ? userDetailBean.name : " ");

            }
        });
    }


    /**
     * author: TanLei
     * description: 用户基本信息
     */
    private void shoeUserInfo() {
        final View user_info_view = LayoutInflater.from(this).inflate(R.layout.dialog_user_info, new RelativeLayout(this));
        TextView basic_text = user_info_view.findViewById(R.id.dialog_user_info_basic_info);
        TextView property_text = user_info_view.findViewById(R.id.dialog_user_info_property_info);

        basic_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        property_text.setMovementMethod(ScrollingMovementMethod.getInstance());

        AlertDialog user_info_dialog = new AlertDialog.Builder(this)
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .setView(user_info_view)
                .setTitle(user_id == SharePrefUtil.getId(this) ? "我的资料" : userDetailBean.name + "的资料")
                .create();
        for (int i = 0; i < profileBeans.size(); i ++) {
            String title = profileBeans.get(i).title;
            String data = profileBeans.get(i).data;
            basic_text.append(title + "：" + data + "\n");
        }
        for (int i = 0; i < creditBeans.size(); i ++) {
            String title = creditBeans.get(i).title;
            int data = creditBeans.get(i).data;
            property_text.append(title + "：" + data + "\n");
        }
        user_info_dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_user_detail_user_detail:  //用户详细资料
                shoeUserInfo();
                break;

            case R.id.menu_user_detail_add_to_black:  //拉黑
                if (user_id == SharePrefUtil.getId(this)) {
                    ToastUtil.showSnackBar(coordinatorLayout, "哈哈，你真逗");
                } else {
                    Intent intent = new Intent(this, BlackUserActivity.class);
                    intent.putExtra(Constants.Key.USER_ID, user_id);
                    startActivity(intent);
                }

                break;

            case R.id.menu_user_detail_report_user:  //举报
                if (user_id == SharePrefUtil.getId(this)) {
                    ToastUtil.showSnackBar(coordinatorLayout, "哈哈，你真逗");
                } else {
                    ReportDialog.show(this, Constants.Api.REPORT_TYPE_USER, user_id);
                }

                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
