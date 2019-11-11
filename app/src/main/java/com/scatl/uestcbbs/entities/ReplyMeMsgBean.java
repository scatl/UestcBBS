package com.scatl.uestcbbs.entities;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/3 17:05
 */
public class ReplyMeMsgBean {

    public int rs;
    public String errcode;
    public int page;
    public int has_next;
    public int total_num;
    public int searchid;

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
        public List<ListBean> listBeanList;
        public static class ExternBean {
            public String padding;
        }

        public static class ListBean {
            public String board_name;
            public int board_id;
            public int topic_id;
            public String topic_subject;
            public String topic_content;
            public String topic_url;
            public String reply_content;
            public String reply_url;
            public int reply_remind_id;
            public String user_name;
            public int user_id;
            public String icon;
            public int is_read;
            public String replied_date;
            public String type;
            public String mod;
        }
    }

}
