package com.scatl.uestcbbs.entities;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/19 12:53
 */
public class HomePage2MultipleItem implements MultiItemEntity {

    private int itemType;

    public HomePage2MultipleItem(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }


}
