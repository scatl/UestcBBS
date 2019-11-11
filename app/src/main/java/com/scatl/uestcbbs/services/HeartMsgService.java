package com.scatl.uestcbbs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.utils.NotificationUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class HeartMsgService extends Service {

    public static final String serviceName = "com.scatl.uestcbbs.services.HeartMsgService";

    private HeartMsgThread heartMsgThread;

    private boolean iamgroot;

    public static int at_me_msg_count = 0;
    public static int reply_me_msg_count = 0;
    public static int private_me_msg_count = 0;

    public HeartMsgService() { }

    @Override
    public void onCreate() {
        super.onCreate();
        if (! EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        heartMsgThread = new HeartMsgThread();
        iamgroot = true;
        heartMsgThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private class HeartMsgThread extends Thread {
        @Override
        public void run() {
            while (iamgroot) {
                getHeartMsg();
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * author: sca_tl
     * description: 获取消息提醒
     */
    private void getHeartMsg() {
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", SharePrefUtil.getAccessToken(this));
        map.put("accessSecret", SharePrefUtil.getAccessSecret(this));
        map.put("apphash", CommonUtil.getAppHashValue());

        HttpRequestUtil.post(Constants.Api.GET_HEART_MSG, map, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) { }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                resolveData(response);
            }
        });
    }


    /**
     * author: sca_tl
     * description: 解析数据，推送消息
     */
    private void resolveData(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        int rs = jsonObject.getIntValue("rs"); //通常 1 表示成功，0 表示失败。
        if (rs == 1) {
            JSONObject jsonObject1 = jsonObject.getJSONObject("body");

            int reply_me_count = jsonObject1.getJSONObject("replyInfo").getIntValue("count");
            int at_me_count = jsonObject1.getJSONObject("atMeInfo").getIntValue("count");
            int private_msg_count = jsonObject1.getJSONArray("pmInfos").size();

            if (reply_me_count != 0 && reply_me_count != reply_me_msg_count) {  //新回复
                reply_me_msg_count = reply_me_count;
                NotificationUtil.showNotification(HeartMsgService.this,
                        BaseEvent.EventCode.NEW_REPLY_MSG,
                        "10001",
                        "new_reply",
                        "新回复提醒",
                        "你收到了" + reply_me_count + "条新回复，点击查看");
            }

            if (at_me_count != 0 && at_me_count != at_me_msg_count) {  //新at
                at_me_msg_count = at_me_count;
                NotificationUtil.showNotification(HeartMsgService.this,
                        BaseEvent.EventCode.NEW_AT_MSG,
                        "10010",
                        "new_at",
                        "新at提醒",
                        "你收到了" + at_me_count + "条at消息，点击查看");
            }

            if (private_msg_count != 0 && private_msg_count != private_me_msg_count) {  //新私信
                private_me_msg_count = private_msg_count;
                NotificationUtil.showNotification(HeartMsgService.this,
                        BaseEvent.EventCode.NEW_PRIVATE_MSG,
                        "10086",
                        "new_private",
                        "新私信提醒",
                        "你收到了" + private_msg_count + "条新私信，点击查看");
            }

            //通知通知页面更新未读条数
            EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SET_MSG_COUNT));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * author: sca_tl
     * description: 接收event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgReceived(BaseEvent baseEvent) {
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_NEW_AT_COUNT_ZERO) {
            at_me_msg_count = 0;
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_NEW_REPLY_COUNT_ZERO) {
            reply_me_msg_count = 0;
        }
        if (baseEvent.eventCode == BaseEvent.EventCode.SET_NEW_PRIVATE_COUNT_SUBTRACT) {
            if (private_me_msg_count != 0) {
                private_me_msg_count = private_me_msg_count - 1;
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iamgroot) iamgroot = false;
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
