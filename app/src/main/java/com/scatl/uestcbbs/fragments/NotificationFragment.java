package com.scatl.uestcbbs.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.AtMeMsgActivity;
import com.scatl.uestcbbs.activities.PrivateMsgActivity;
import com.scatl.uestcbbs.activities.ReplyMeActivity;
import com.scatl.uestcbbs.activities.SystemMsgActivity;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.interfaces.OnRefresh;
import com.scatl.uestcbbs.services.HeartMsgService;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.utils.RefreshUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class NotificationFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "NotificationFragment";

    private SmartRefreshLayout refreshLayout;
    private CardView cardView1, cardView2, cardView3, cardView4;
    private TextView textView8, textView9, textView10, textView11;
    private TextView system_msg_count, at_msg_count, reply_msg_count, private_msg_count;

    public static NotificationFragment newInstance(Bundle bundle) {
        NotificationFragment notificationFragment = new NotificationFragment();
        notificationFragment.setArguments(bundle);
        return notificationFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_notification;
    }

    @Override
    protected void init() {
        cardView1 = view.findViewById(R.id.notification_cardview1);
        cardView2 = view.findViewById(R.id.notification_cardview2);
        cardView3 = view.findViewById(R.id.notification_cardview3);
        cardView4 = view.findViewById(R.id.notification_cardview4);

        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);

        textView8 = view.findViewById(R.id.text8);
        textView9 = view.findViewById(R.id.text9);
        textView10 = view.findViewById(R.id.text10);
        textView11 = view.findViewById(R.id.text11);
//        textView8.getPaint().setFakeBoldText(true);
//        textView9.getPaint().setFakeBoldText(true);
//        textView10.getPaint().setFakeBoldText(true);
//        textView11.getPaint().setFakeBoldText(true);

        system_msg_count = view.findViewById(R.id.notification_system_msg_count);
        at_msg_count = view.findViewById(R.id.notification_at_msg_count);
        reply_msg_count = view.findViewById(R.id.notification_reply_msg_count);
        private_msg_count = view.findViewById(R.id.notification_private_msg_count);

        refreshLayout = view.findViewById(R.id.notification_refresh);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);

        setOnRefreshListener();
        initUnreadMsg();
        initView();
    }

    /**
     * author: sca_tl
     * description: 初始化未读消息数目
     */
    private void initUnreadMsg() {
        system_msg_count.setText(mActivity.getResources().getString(R.string.click_to_view));
        system_msg_count.setTextColor(mActivity.getResources().getColor(R.color.text_color_dark));

        if (HeartMsgService.at_me_msg_count == 0) {
            at_msg_count.setText(mActivity.getResources().getString(R.string.no_new_message));
            at_msg_count.setTextColor(mActivity.getResources().getColor(R.color.text_color_dark));
        } else {
            at_msg_count.setText(mActivity.getResources()
                    .getString(R.string.new_at_message, HeartMsgService.at_me_msg_count));
            at_msg_count.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
        }

        if (HeartMsgService.reply_me_msg_count == 0) {
            reply_msg_count.setText(mActivity.getResources().getString(R.string.no_new_message));
            reply_msg_count.setTextColor(mActivity.getResources().getColor(R.color.text_color_dark));
        } else {
            reply_msg_count.setText(mActivity.getResources()
                    .getString(R.string.new_reply_message, HeartMsgService.reply_me_msg_count));
            reply_msg_count.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
        }

        if (HeartMsgService.private_me_msg_count == 0) {
            private_msg_count.setText(mActivity.getResources().getString(R.string.no_new_message));
            private_msg_count.setTextColor(mActivity.getResources().getColor(R.color.text_color_dark));
        } else {
            private_msg_count.setText(mActivity.getResources()
                    .getString(R.string.new_private_message, HeartMsgService.private_me_msg_count));
            private_msg_count.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
        }


    }


    private void initView() {
        refreshLayout.autoRefresh(0, 0, 0, false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notification_cardview1:  //系统通知
                Intent intent = new Intent(mActivity, SystemMsgActivity.class);
                startActivity(intent);
                break;

            case R.id.notification_cardview2:  //提到我的
                Intent intent1 = new Intent(mActivity, AtMeMsgActivity.class);
                startActivity(intent1);
                break;

            case R.id.notification_cardview3:  //回复我的
                Intent intent2 = new Intent(mActivity, ReplyMeActivity.class);
                startActivity(intent2);
                break;

            case R.id.notification_cardview4:  //私信
                Intent intent3 = new Intent(mActivity, PrivateMsgActivity.class);
                startActivity(intent3);
                break;

            default:
                break;

        }
    }

    /**
     * author: sca_tl
     * description: 刷新监听
     */
    private void setOnRefreshListener() {
        RefreshUtil.setOnRefreshListener(mActivity, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) { }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) { }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.LOGIN_SUCCEED
                || baseEvent.eventCode == BaseEvent.EventCode.LOGOUT_SUCCEED) {
            initView();
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_MSG_COUNT) {
            initUnreadMsg();
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
