package com.scatl.uestcbbs.entities;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/3 10:27
 */
public class SystemMsgBean {

    public int rs;
    public String errcode;
    public int page;
    public int has_next;
    public int total_num;

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
        public List<DataBean> dataBean;

        public static class ExternBean {
            public String padding;
        }

        public static class DataBean {

            public List<ActionBean> actionBean;

            public String replied_date;
            public String type;
            public String icon;
            public String user_name;
            public String user_id; //注意该字段有可能是int
            public String mod;
            public String note;
            public String is_read;

            public boolean has_action = false;

            public static class ActionBean {
                public String redirect;
                public String title;
                public String type;
            }
        }

    }
}
