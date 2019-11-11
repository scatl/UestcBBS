package com.scatl.uestcbbs.adapters.notification;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.SystemMsgBean;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 16:00
 */
public class SystemMsgAdapter extends BaseQuickAdapter<SystemMsgBean.BodyBean.DataBean, BaseViewHolder> {

    private Context context;

    public SystemMsgAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addSystemMeMsgData(List<SystemMsgBean.BodyBean.DataBean> data, boolean refresh) {
        if (refresh) { setNewData(data); } else { addData(data); }
    }

    @Override
    protected void convert(BaseViewHolder helper, SystemMsgBean.BodyBean.DataBean item) {
        helper.setText(R.id.item_system_msg_user_name, item.user_name)
                .setText(R.id.item_system_msg_content, item.note)
                .setText(R.id.item_system_msg_date,
                        TimeUtil.timeFormat1(item.replied_date, R.string.post_time1, context))
                .addOnClickListener(R.id.item_system_msg_user_icon);
        Glide.with(context).load(item.icon).into((CircleImageView)helper.getView(R.id.item_system_msg_user_icon));

        if (item.has_action) {
            helper.getView(R.id.item_system_action_btn).setVisibility(View.VISIBLE);
            helper.setText(R.id.item_system_action_btn, item.actionBean.get(0).title);
        } else {
            helper.getView(R.id.item_system_action_btn).setVisibility(View.GONE);
        }
    }
}
