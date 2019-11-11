package com.scatl.uestcbbs.entities;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 18:39
 */
public class SearchUserBean {

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
            public int uid;
            public String icon;
            public int isFriend;
            public int is_black;
            public int gender;
            public String name;
            public int status;
            public int level;
            public int credits;
            public int isFollow;
            public String dateline;
            public String signture;
            public String location;
            public String distance;
            public String userTitle;
        }
    }

}
