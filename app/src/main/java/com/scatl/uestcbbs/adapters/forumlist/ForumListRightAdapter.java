package com.scatl.uestcbbs.adapters.forumlist;

import android.content.Context;
import android.widget.GridView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entities.ForumListBean;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 16:31
 */
public class ForumListRightAdapter extends BaseQuickAdapter<ForumListBean.BoardBean, BaseViewHolder> {

    private Context context;

    public ForumListRightAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }


    public void addData(List<ForumListBean.BoardBean> beans){
        setNewData(beans);
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, ForumListBean.BoardBean item) {
        helper.setText(R.id.forum_list_right_title, item.board_category_name);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(context, getData().get(helper.getLayoutPosition()).boardListBeans);
        GridView gridView = helper.getView(R.id.forum_list_right_gridview);
        gridView.setAdapter(gridViewAdapter);
        gridView.requestFocus();
    }
}
