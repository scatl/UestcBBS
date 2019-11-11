package com.scatl.uestcbbs.entities;

import java.util.List;

/**
 * author: TanLei
 * description: 用户关注与粉丝
 */
public class UserFollowBean {
    public int rs;
    public String errcode;
    public int page;
    public int has_next;
    public int total_num;

    public HeadBean headBean;
    public BodyBean bodyBean;
    public List<ListBean> listBeans;

    public static class HeadBean {
        public int errCode;
        public String errInfo;
        public String version;
        public int alert;
    }

    public static class BodyBean {
        ExternBean externBean;
        public static class ExternBean {
            public String padding;
        }
    }

    public static class ListBean {
        public String distance;
        public String location;
        public int is_friend;
        public int isFriend;
        public int isFollow;
        public int uid;
        public String name;
        public int status;
        public int is_black;
        public int gender;
        public String icon;
        public int level;
        public String userTitle;
        public List verify;
        public String lastLogin;
        public String dateline;
        public String signature;
        public String credits;
    }
}
