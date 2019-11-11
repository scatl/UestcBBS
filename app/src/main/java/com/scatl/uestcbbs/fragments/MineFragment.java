package com.scatl.uestcbbs.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.EmoticonActivity;
import com.scatl.uestcbbs.activities.PostDraftActivity;
import com.scatl.uestcbbs.activities.SettingsActivity;
import com.scatl.uestcbbs.activities.UserDetailActivity;
import com.scatl.uestcbbs.activities.UserPostActivity;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.dialogs.LoginDialog;
import com.scatl.uestcbbs.entities.LoginBean;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MineFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "MineFragment";

    private CircleImageView user_icon;
    private TextView user_name;
    private ImageView right_arrow;
    private Switch night_mode;
    private RelativeLayout favorite_rl, post_rl, reply_rl, settings_rl, theme_rl, exit_rl, draft_rl, emotion_rl;
    private CardView mine_cardview1;

    public static MineFragment newInstance(Bundle bundle) {
        MineFragment mineFragment = new MineFragment();
        mineFragment.setArguments(bundle);
        return mineFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void init() {
        user_icon = view.findViewById(R.id.mine_user_icon);
        user_name = view.findViewById(R.id.mine_user_name);
        right_arrow = view.findViewById(R.id.mine_right_arrow);
        night_mode = view.findViewById(R.id.mine_night_mode_switch);
        mine_cardview1 = view.findViewById(R.id.mine_cardview1);
        mine_cardview1.setOnClickListener(this);

        favorite_rl = view.findViewById(R.id.mine_favorite_rl);
        favorite_rl.setOnClickListener(this);
        post_rl = view.findViewById(R.id.mine_post_rl);
        post_rl.setOnClickListener(this);
        reply_rl = view.findViewById(R.id.mine_reply_rl);
        reply_rl.setOnClickListener(this);
        settings_rl = view.findViewById(R.id.mine_settings_rl);
        settings_rl.setOnClickListener(this);
        theme_rl = view.findViewById(R.id.mine_theme_rl);
        theme_rl.setOnClickListener(this);
        exit_rl = view.findViewById(R.id.mine_exit_rl);
        exit_rl.setOnClickListener(this);
        draft_rl = view.findViewById(R.id.mine_draft_rl);
        draft_rl.setOnClickListener(this);
        emotion_rl = view.findViewById(R.id.mine_emotion_rl);
        emotion_rl.setOnClickListener(this);

        initNightMode();
        initUserInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_favorite_rl: //我的收藏
                Intent intent = new Intent(mActivity, UserPostActivity.class);
                intent.putExtra("type", UserPostActivity.TYPE_USER_FAVORITE);
                startActivity(intent);
                break;

            case R.id.mine_post_rl:  //我的发表
                Intent intent1 = new Intent(mActivity, UserPostActivity.class);
                intent1.putExtra("type", UserPostActivity.TYPE_USER_POST);
                startActivity(intent1);
                break;

            case R.id.mine_reply_rl: //我的回复
                Intent intent2 = new Intent(mActivity, UserPostActivity.class);
                intent2.putExtra("type", UserPostActivity.TYPE_USER_REPLY);
                startActivity(intent2);
                break;

            case R.id.mine_settings_rl: //设置
                Intent intent3 = new Intent(mActivity, SettingsActivity.class);
                startActivity(intent3);
                break;

            case R.id.mine_theme_rl:  //主题设置
                showThemeColorPickDialog();
                break;

            case R.id.mine_exit_rl:  //退出登陆
                logout();
                break;

            case R.id.mine_draft_rl:
                Intent intent4 = new Intent(mActivity, PostDraftActivity.class);
                startActivity(intent4);
                break;

            case R.id.mine_emotion_rl:
                Intent intent5 = new Intent(mActivity, EmoticonActivity.class);
                startActivity(intent5);
                break;

            case R.id.mine_cardview1:
                onLoginClick();
            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description:
     */
    private void onLoginClick() {
        if (SharePrefUtil.isLogin(mActivity)) {
            Intent intent = new Intent(mActivity, UserDetailActivity.class);
            intent.putExtra(Constants.Key.USER_ID, SharePrefUtil.getId(mActivity));
            startActivity(intent);
        } else {
            LoginDialog loginDialog = new LoginDialog();
            loginDialog.show(getChildFragmentManager(), TimeUtil.getStringMs());
        }
    }

    /**
     * author: sca_tl
     * description: 初始化用户信息
     */
    private void initUserInfo() {
        if (SharePrefUtil.isLogin(mActivity)) {
            String icon = SharePrefUtil.getAvatar(mActivity);
            String name = SharePrefUtil.getName(mActivity);
            user_name.setText(name);
            Glide.with(mActivity).load(icon).into(user_icon);
            exit_rl.setVisibility(View.VISIBLE);
        } else {
            user_name.setText("请登录");
            Glide.with(mActivity).load(R.drawable.ic_default_avatar).into(user_icon);
            exit_rl.setVisibility(View.GONE);
        }
    }

    /**
     * author: sca_tl
     * description: 退出登陆
     */
    private void logout() {
        final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setTitle("退出登陆")
                .setMessage("确认要退出登陆吗？")
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharePrefUtil.setLogin(mActivity, false, new LoginBean());
                        initUserInfo();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     * author: sca_tl
     * description: 初始化夜间模式
     */
    private void initNightMode() {
        night_mode.setChecked(SharePrefUtil.isNightMode(mActivity));
        night_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (! compoundButton.isPressed()) return;
                int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (mode == Configuration.UI_MODE_NIGHT_YES) {
                    SharePrefUtil.setNightMode(mActivity, false);
                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.NIGHT_MODE_NO));
                } else {
                    SharePrefUtil.setNightMode(mActivity, true);
                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.NIGHT_MODE_YES));
                }
            }
        });
    }

    /**
     * author: sca_tl
     * description: 颜色选择对话框
     */
    private void showThemeColorPickDialog() {
        ColorPickerDialogBuilder
                .with(mActivity)
                .setTitle("选择主题色")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("更改", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors) {
                        //判断一下是不是夜间模式，若是则不改变statusbar和toolbar的颜色
                        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                        if (mode == Configuration.UI_MODE_NIGHT_YES) {  //是夜间模式

                        } else {  //不是夜间模式

                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .build()
                .show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCEED)
            initUserInfo();
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
