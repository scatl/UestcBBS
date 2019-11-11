package com.scatl.uestcbbs.adapters.forumlist;

import android.content.Context;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entities.ForumListBean;
import com.scatl.uestcbbs.utils.CommonUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 16:16
 */
public class ForumListLeftAdapter extends BaseQuickAdapter<ForumListBean.BoardBean, BaseViewHolder> {

    private Context context;
    private int selected = 0;

    public ForumListLeftAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addData(List<ForumListBean.BoardBean> beans){
        setNewData(beans);
        notifyDataSetChanged();
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, ForumListBean.BoardBean item) {
        TextView name = helper.getView(R.id.forum_list_left_text);
        if (helper.getLayoutPosition() == selected) {
            name.setTextSize(18f);
            name.setTextColor(CommonUtil.getAttrColor(context, R.attr.colorPrimary));
        } else {
            name.setTextSize(15f);
            name.setTextColor(context.getResources().getColor(R.color.text_color));
        }
        helper.setText(R.id.forum_list_left_text, item.board_category_name);
    }
}
