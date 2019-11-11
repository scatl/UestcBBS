package com.scatl.uestcbbs.entities;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/15 16:29
 */
public class PrivateChatBean {

    public static class PrivateChatHisBean {
        //发送方信息
        public int fromUid;
        public String name;
        public String avatar;
    }

    public static class PrivateChatListBean {
        public int sender;
        public int mid;
        public String content;
        public String type;
        public String time;
    }
}
