package com.scatl.uestcbbs.interfaces;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 18:54
 */
public interface OnRefresh {
    void onRefresh(RefreshLayout refreshLayout);
    void onLoadMore(RefreshLayout refreshLayout);
}
