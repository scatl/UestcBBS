package com.scatl.uestcbbs.entities;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 18:38
 */
public class SearchPostBean {
    public int rs;
    public String errcode;
    public int page;
    public int has_next;
    public int total_num;
    public int searchid;

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
        public int board_id;
        public int topic_id;
        public int type_id;
        public int sort_id;
        public int vote;
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
        public String pic_path;
    }
}
