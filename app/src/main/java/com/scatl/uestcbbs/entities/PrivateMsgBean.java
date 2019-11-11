package com.scatl.uestcbbs.entities;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/2 20:22
 */
public class PrivateMsgBean {

    public int rs;
    public String errcode;

    public HeadBean headBean;
    public BodyBean bodyBean;

    public static class HeadBean {
        public int errCode;
        public String errInfo;
        public String version;
        public int alert;
    }

    public static class BodyBean {

        public int has_next;
        public int count;

        public ExternBean externBean;
        public List<ListBean> listBeanList;
        public static class ExternBean {
            public String padding;
        }

        public static class ListBean {
            public int plid;
            public int pmid;
            public int lastUserId;
            public String lastUserName;
            public String lastSummary;  //私信内容
            public String lastDateline; //时间
            public int toUserId;  //对方id
            public String toUserAvatar; //对方icon
            public String toUserName; //对方昵称
            public int toUserIsBlack;
            public int isNew;  //当有新消息时，用户获取获取与对方的消息记录时isNew=0
        }


    }

}
