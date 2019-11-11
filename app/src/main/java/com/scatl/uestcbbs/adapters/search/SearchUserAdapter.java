package com.scatl.uestcbbs.adapters.search;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.SearchUserBean;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 19:40
 */
public class SearchUserAdapter extends BaseQuickAdapter<SearchUserBean.BodyBean.ListBean, BaseViewHolder> {
    private Context context;

    public SearchUserAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addSearchUserData(List<SearchUserBean.BodyBean.ListBean> data, boolean refresh) {
        if (refresh) { setNewData(data); } else { addData(data); }
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchUserBean.BodyBean.ListBean item) {
        helper.setText(R.id.search_user_name, item.name)
                .setText(R.id.search_user_last_login,
                        TimeUtil.timeFormat1(item.dateline, R.string.last_login_time, context));
        Glide.with(context).load(item.icon).into((CircleImageView)helper.getView(R.id.search_user_icon));

    }
}
