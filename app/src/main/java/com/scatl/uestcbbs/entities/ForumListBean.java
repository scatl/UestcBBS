package com.scatl.uestcbbs.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/21 13:08
 */
public class ForumListBean implements Serializable {

    public static class BoardBean implements Serializable{
        public int board_category_id;
        public String board_category_name;
        public int board_category_type;

        public List<BoardListBean> boardListBeans = new ArrayList<>();

        public static class BoardListBean implements Serializable {
            public int board_id;
            public String board_name;
            public String description;
            public int board_child;
            public String board_img;
            public String last_posts_date; //注意是string
            public String board_content;
            public String forumRedirect;
            public int favNum;
            public int td_posts_num;
            public int topic_total_num;
            public int posts_total_num;
            public int is_focus;


            public static class BoardCatBean implements Serializable {
                public int classificationType_id;
                public String classificationType_name;
            }
        }

    }


}
