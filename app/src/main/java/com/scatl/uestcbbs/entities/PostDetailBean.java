package com.scatl.uestcbbs.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/07 20:38
 */
public class PostDetailBean {

    /**
     * author: sca_tl
     * description: 帖子的基本信息
     */
    public static class PostBasicBean {
        public int page;
        public int has_next;  //1:还有数据  0：无数据
        public int total_num;
        public String forumName;
        public int boardId;
        public String forumTopicUrl;

        public int topic_id;
        public String title;
        public String type;
        public int special;
        public int sortId;
        public int user_id;
        public String user_nick_name;
        public int replies;
        public int hits;
        public int essence;
        public int vote;
        public int hot;
        public int top;
        public int is_favor;
        public String create_date;  //服务器返回的时间是String
        public String icon;
        public int level;
        public String userTitle;
        public String userColor;
        public int isFollow;

        public List<PostContentBean> postContentBeanList = new ArrayList<>();
    }

    /**
     * author: sca_tl
     * description: 帖子内容
     */
    public static class PostContentBean {
        public String infor;
        public int type;
        public String url = "";
        public String originalInfo;
        public int aid;
        public String desc;
    }

    /**
     * author: sca_tl
     * description: 帖子投票信息
     */
    public static class PostPollBasicBean {
        public String deadline;
        public int is_visible;
        public int voters;
        public int type;
        public int poll_status;

        public static class PostPollContentBean {
            public String name;
            public int poll_item_id;
            public int total_num;
            public String percent;
        }

    }

    /**
     * author: sca_tl
     * description: 帖子回复信息
     */
    public static class PostReplyBean {
        public int reply_id;
        public String reply_type;
        public String reply_name;
        public int reply_posts_id;
        public int poststick;
        public int position;
        public String posts_date;  //服务器返回的时间是String
        public String icon;
        public int level;
        public String userTitle;
        public String userColor;
        public String location;
        public String mobileSign;
        public int reply_status;
        public int status;
        public int role_num;
        public String title;
        public int gender;
        public int is_quote;
        public int quote_pid;
        public String quote_content;
        public String quote_user_name;
        public boolean delThread;

        public List<PostContentBean> postContentBeanList = new ArrayList<>();
    }
}
