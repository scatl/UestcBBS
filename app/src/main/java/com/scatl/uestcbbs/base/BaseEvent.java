package com.scatl.uestcbbs.base;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/26 16:47
 */
public class BaseEvent<T> {
    public int eventCode;
    public T eventData;

    public BaseEvent(int code, T data) {
        this.eventCode = code;
        this.eventData = data;
    }

    public BaseEvent(int code) {
        this.eventCode = code;
    }

    public static class BoardChange {
        public int boardId;
        public int catId;
    }

    public static class BoardSelected {
        public String board_name;
        public int board_id;
        public String cat_name;
        public int cat_id;
    }

    public static class EventCode {

        public static final String NEW_REPLY_MSG = "newReplyMsg";   //新回复消息
        public static final String NEW_AT_MSG = "newAtMsg";      //新at消息
        public static final String NEW_PRIVATE_MSG = "newPrivateMsg"; //新私信消息

        public static final int NIGHT_MODE_YES = 1;    //夜间模式
        public static final int NIGHT_MODE_NO = 2;     //日间模式
        public static final int LOGIN_SUCCEED = 3;     //登陆成功
        public static final int LOGOUT_SUCCEED = 4;    //注销登陆成功
        public static final int SET_MSG_COUNT = 5;     //消息数目
        public static final int BOARD_CHANGED = 6;     //切换板块
        public static final int SET_NEW_REPLY_COUNT_ZERO = 7;  //新回复消息数目置零
        public static final int SET_NEW_AT_COUNT_ZERO = 8;     //新at消息数目置零
        public static final int SET_NEW_PRIVATE_COUNT_SUBTRACT = 9;  //新私信消息数目减1
        public static final int READ_PRIVATE_CHAT_MSG = 10;    //读取了私信内容
        public static final int SEND_REPLY_SUCCEED = 11;       //发送回复成功
        public static final int BOARD_CAT_INIT = 12;       //板块分类初始化
        public static final int BOARD_CAT_CHANGE = 13;     //板块分类变化
        public static final int SUB_BOARD_CHANGE = 14;     //子版块变化
        public static final int SELECTED_BOARD = 15;       //发表帖子时选择的板块
        public static final int INSERT_EMOTION = 16;


    }

}
