package com.scatl.uestcbbs.adapters;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.AtUserListBean;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/18 10:46
 */
public class AtUserListAdapter extends BaseQuickAdapter<AtUserListBean, BaseViewHolder> {

    private Context context;

    public AtUserListAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addAtUserListData(List<AtUserListBean> data, boolean refresh) {
        if (refresh) { setNewData(data); } else { addData(data); }
    }

    @Override
    protected void convert(BaseViewHolder helper, AtUserListBean item) {
        helper.setText(R.id.item_at_user_list_name, item.name)
                .addOnClickListener(R.id.item_at_user_list_icon);

        String icon = context.getResources().getString(R.string.icon_url, item.uid);
        Glide.with(context).load(icon).into((CircleImageView)helper.getView(R.id.item_at_user_list_icon));
    }
}
