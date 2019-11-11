package com.scatl.uestcbbs.entities;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/19 12:55
 */
public class DailyPicBean implements MultiItemEntity {
    public String copy_right;
    public String remote_url;
    public String hsh;

    private int itemType;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

}
