package com.scatl.uestcbbs.entities;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 18:29
 */
public class PostListBean implements Serializable, MultiItemEntity {
    public int board_id;
    public String board_name;
    public int topic_id;
    public String type;
    public String title;
    public int user_id;
    public String user_nick_name;
    public String user_avatar;
    public long last_reply_date;
    public int vote;
    public int hot;
    public int hits;
    public int replies;
    public int essence;
    public int top;
    public int status;
    public String subject;
    //public String summary;
    public String pic_path;
    public int ratio;
    public int gender;
    public String user_title;
    public String recommendAdd;
    public int special;
    public int isHasRecommendAdd;
    public String imageList;
    public String sourceWebUrl;
    public List virify;


    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
