package com.scatl.uestcbbs.adapters.userdetail;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.UserFollowBean;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 13:47
 */
public class UserFollowAdapter extends BaseQuickAdapter<UserFollowBean.ListBean, BaseViewHolder> {

    private Context context;

    public UserFollowAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addUserFollowData(List<UserFollowBean.ListBean> data, boolean refresh) {
        if (refresh) { setNewData(data); } else { addData(data); }
    }

    @Override
    protected void convert(BaseViewHolder helper, UserFollowBean.ListBean item) {
        helper.setText(R.id.user_follow_name, item.name)
                .setText(R.id.user_follow_last_login,
                        TimeUtil.timeFormat1(item.lastLogin, R.string.last_login_time, context));
        Glide.with(context).load(item.icon).into((CircleImageView)helper.getView(R.id.user_follow_icon));
    }
}
