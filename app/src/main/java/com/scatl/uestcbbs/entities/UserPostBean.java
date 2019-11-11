package com.scatl.uestcbbs.entities;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/20 19:17
 */
public class UserPostBean {

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
        public String pic_path;
        public int board_id;
        public String board_name;
        public int topic_id;
        public int type_id;
        public int sort_id;
        public String title;
        public String subject;
        public int user_id;
        public String last_reply_date;
        public String user_nick_name;
        public int hits;
        public int replies;
        public int top;
        public int status;
        public int essence;
        public int hot;
        public String userAvatar;
        public int special;
    }

}
