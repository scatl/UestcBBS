package com.scatl.uestcbbs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.custom.dialogs.UpdateDialog;
import com.scatl.uestcbbs.entities.UpdateBean;
import com.scatl.uestcbbs.fragments.ForumListFragment;
import com.scatl.uestcbbs.fragments.MineFragment;
import com.scatl.uestcbbs.fragments.NotificationFragment;
import com.scatl.uestcbbs.fragments.homepage.HomePageFragment;
import com.scatl.uestcbbs.fragments.homepage1.HomePageFragment1;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.services.HeartMsgService;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.ServiceUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.TimeUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private ForumListFragment forumListFragment;
    private HomePageFragment homePageFragment;
    private HomePageFragment1 homePageFragment1;
    private MineFragment mineFragment;
    private NotificationFragment notificationFragment;

    private AHBottomNavigation navigationBar;
    private CoordinatorLayout coordinatorLayout;

    private String[] fragment_tags = {"homePage", "forumList", "notification", "mine", "homepage1"};
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        FragmentManager fm = getSupportFragmentManager();
        if (homePageFragment != null) { fm.putFragment(outState, fragment_tags[0], homePageFragment); }
        if (forumListFragment != null) { fm.putFragment(outState, fragment_tags[1], forumListFragment); }
        if (notificationFragment != null) { fm.putFragment(outState, fragment_tags[2], notificationFragment); }
        if (mineFragment != null) { fm.putFragment(outState, fragment_tags[3], mineFragment); }
        if (homePageFragment1 != null) {fm.putFragment(outState, fragment_tags[4], homePageFragment1);}

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);
    }

    /**
     * author: sca_tl
     * description: 初始化
     */
    private void init(Bundle savedInstanceState) {
        navigationBar = findViewById(R.id.bottom_navigation_bar);
        coordinatorLayout = findViewById(R.id.main_coorlayout);

        initBottomBar();

        if (savedInstanceState != null) {
            FragmentManager fm = getSupportFragmentManager();
            homePageFragment = (HomePageFragment) fm.getFragment(savedInstanceState, fragment_tags[0]);
            forumListFragment = (ForumListFragment) fm.getFragment(savedInstanceState, fragment_tags[1]);
            notificationFragment = (NotificationFragment) fm.getFragment(savedInstanceState, fragment_tags[2]);
            mineFragment = (MineFragment) fm.getFragment(savedInstanceState, fragment_tags[3]);
            homePageFragment1 = (HomePageFragment1) fm.getFragment(savedInstanceState, fragment_tags[4]);

            if (homePageFragment != null) fragmentList.add(homePageFragment);
            if (forumListFragment != null) fragmentList.add(forumListFragment);
            if (notificationFragment != null) fragmentList.add(notificationFragment);
            if (mineFragment != null) fragmentList.add(mineFragment);
            if (homePageFragment1 != null) fragmentList.add(homePageFragment1);

            navigationBar.setCurrentItem(0);

        } else {
            int selected = getIntent().getIntExtra("selected", 0);
            switch (selected) {
                case 0:
                    if (SharePrefUtil.getHomeStyle(this) == 0) {
                        homePageFragment1 = HomePageFragment1.newInstance(null);
                        addFragment(homePageFragment1);
                        showFragment(homePageFragment1);
                    }
                    if (SharePrefUtil.getHomeStyle(this) == 1) {
                        homePageFragment = HomePageFragment.newInstance(null);
                        addFragment(homePageFragment);
                        showFragment(homePageFragment);
                    }

                    break;

                case 1:
                    forumListFragment = ForumListFragment.newInstance(null);
                    addFragment(forumListFragment);
                    showFragment(forumListFragment);
                    break;

                case 2:
                    notificationFragment = NotificationFragment.newInstance(null);
                    addFragment(notificationFragment);
                    showFragment(notificationFragment);
                    break;

                case 3:
                    mineFragment = MineFragment.newInstance(null);
                    addFragment(mineFragment);
                    showFragment(mineFragment);
                    break;

                default:
                    break;
            }

            navigationBar.setCurrentItem(selected, false);
        }

        startHeartMsgService();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUpdate();
            }
        }, 1000);
    }

    /**
     * author: sca_tl
     * description: 初始化底栏
     */
    private void initBottomBar() {
        if (SharePrefUtil.isNightMode(this)) {
            navigationBar.setDefaultBackgroundColor(getResources().getColor(R.color.background_dark));
        }

        navigationBar.setNotificationBackgroundColor(getResources().getColor(R.color.colorPrimary));
        navigationBar.setAccentColor(getResources().getColor(R.color.colorPrimary));
        navigationBar.addItem(new AHBottomNavigationItem("首页", R.drawable.ic_home));
        navigationBar.addItem(new AHBottomNavigationItem("板块", R.drawable.ic_forumlist));
        navigationBar.addItem(new AHBottomNavigationItem("通知", R.drawable.ic_notification_fill));
        navigationBar.addItem(new AHBottomNavigationItem("我的", R.drawable.ic_mine));

        navigationBar.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case 0:
                        if (SharePrefUtil.getHomeStyle(MainActivity.this) == 0) {
                            if (homePageFragment1 == null) { homePageFragment1 = HomePageFragment1.newInstance(null); }
                            addFragment(homePageFragment1);
                            showFragment(homePageFragment1);
                        }
                        if (SharePrefUtil.getHomeStyle(MainActivity.this) == 1) {
                            if (homePageFragment == null) { homePageFragment = HomePageFragment.newInstance(null); }
                            addFragment(homePageFragment);
                            showFragment(homePageFragment);
                        }

                        break;

                    case 1:
                        if (forumListFragment == null) { forumListFragment = ForumListFragment.newInstance(null); }
                        addFragment(forumListFragment);
                        showFragment(forumListFragment);
                        break;

                    case 2:
                        if (notificationFragment == null) { notificationFragment = NotificationFragment.newInstance(null); }
                        addFragment(notificationFragment);
                        showFragment(notificationFragment);
                        break;

                    case 3:
                        if (mineFragment == null){ mineFragment = MineFragment.newInstance(null); }
                        addFragment(mineFragment);
                        showFragment(mineFragment);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void addFragment(Fragment fragment) {
        if (! fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
            fragmentList.add(fragment);
        }
    }

    private void showFragment(Fragment fragment) {
        for (Fragment fragment1 : fragmentList) {
            if (fragment1 != fragment) {
                getSupportFragmentManager().beginTransaction().hide(fragment1).commit();
            }
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    /**
     * author: sca_tl
     * description: 开启消息提醒服务
     */
    private void startHeartMsgService() {
        if (SharePrefUtil.isLogin(this)) {
            if (! ServiceUtil.isServiceRunning(this, HeartMsgService.serviceName)) {
                Intent intent = new Intent(this, HeartMsgService.class);
                startService(intent);
            }
        }
    }

    /**
     * author: sca_tl
     * description: 检查更新
     */
    private void checkUpdate() {
        HttpRequestUtil.get(Constants.Api.UPDATE_URL, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                if (JSON.isValidObject(response)) {
                    UpdateBean updateBean = JSON.parseObject(response, new TypeReference<UpdateBean>(){});
                    if (updateBean.versionCode > CommonUtil.getVersionCode(getApplicationContext())) {
                        UpdateDialog updateDialog = new UpdateDialog();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.Key.DATA, updateBean);
                        updateDialog.setArguments(bundle);
                        updateDialog.show(getSupportFragmentManager(), TimeUtil.getStringMs());
                    }
                }

            }
        });
    }

    /**
     * author: sca_tl
     * description: 两次返回退出
     */
    private long t = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if(System.currentTimeMillis() - t > 2000){
                ToastUtil.showSnackBar(coordinatorLayout, "再按一次返回键退出程序");
                t = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return true;
//        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.NIGHT_MODE_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            finish();
            Intent intent = new Intent( MainActivity.this, MainActivity.class);
            intent.putExtra("selected", 3);
            startActivity(intent);
            overridePendingTransition(R.anim.switch_night_mode_fade_in, R.anim.switch_night_mode_fade_out);
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.NIGHT_MODE_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            finish();
            Intent intent = new Intent( MainActivity.this, MainActivity.class);
            intent.putExtra("selected", 3);
            startActivity(intent);
            overridePendingTransition(R.anim.switch_night_mode_fade_in, R.anim.switch_night_mode_fade_out);
        }

        if (baseEvent.eventCode == BaseEvent.EventCode.SET_MSG_COUNT) {
            int msg_count = HeartMsgService.at_me_msg_count +
                    HeartMsgService.private_me_msg_count +
                    HeartMsgService.reply_me_msg_count;
            if (msg_count != 0) {
                navigationBar.setNotification(msg_count + "", 2);
            } else {
                navigationBar.setNotification("", 2);
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
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        stopService(new Intent(this, HeartMsgService.class));
        //System.exit(0);  //TODO
    }

}
