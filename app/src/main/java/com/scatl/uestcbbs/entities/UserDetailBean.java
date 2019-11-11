package com.scatl.uestcbbs.entities;

import java.util.List;

public class UserDetailBean {

    public int rs;
    public String errcode;

    public int flag;
    public int is_black; //是否是黑名单
    public int is_follow; //是否关注他
    public int isFriend;  //是否是好友
    public String icon;
    public String level_url;
    public String name;
    public String email;
    public int gender;  //0:保密  1:男  2:女
    public int score;  //积分
    public int credits;
    public int gold_num;  //水滴
    public int topic_num;  //发布的主题数
    public int photo_num;
    public int reply_posts_num;  //回复的帖子数
    public int essence_num;
    public int friend_num;  //该用户关注的用户数量，并不是好友数量
    public int follow_num;  //关注该用户的数量
    public int level;
    public String sign;  //签名
    public String userTitle;
    public List verify;
    public String mobile;
    public List info;

    public HeadBean headBean;
    public BodyBean bodyBean;

    public static class HeadBean {
        public int errCode;
        public String errInfo;
        public String version;
        public int alert;
    }

    public static class BodyBean {
        public ExternBean externBean;
        public RepeatBean repeatBean;
        public List<ProfileBean> profileBeanList;
        public List<CreditBean> creditBeanList;
        public List<CreditShowBean> creditShowBeanList;

        public static class ExternBean {
            public String padding;
        }

        public static class RepeatBean { }

        public static class ProfileBean {  //基本数据
            public String type;
            public String title;
            public String data;
        }

        public static class CreditBean {  //财富数据
            public String type;
            public String title;
            public int data;
        }

        public static class CreditShowBean {
            public String type;
            public String title;
            public int data;
        }
    }

}
