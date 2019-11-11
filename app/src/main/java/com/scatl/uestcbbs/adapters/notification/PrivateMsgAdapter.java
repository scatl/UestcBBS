package com.scatl.uestcbbs.adapters.notification;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.PrivateMsgBean;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 15:45
 */
public class PrivateMsgAdapter extends BaseQuickAdapter<PrivateMsgBean.BodyBean.ListBean, BaseViewHolder> {

    private Context context;

    public PrivateMsgAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addPrivateMsgData(List<PrivateMsgBean.BodyBean.ListBean> data, boolean refresh) {
        if (refresh) { setNewData(data); } else { addData(data); }
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateMsgBean.BodyBean.ListBean item) {
        helper.setText(R.id.item_private_msg_user_name, item.toUserName)
                .setText(R.id.item_private_msg_content, item.lastSummary.length() == 0 ? "[图片]" : item.lastSummary)
                .setText(R.id.item_private_msg_date,
                        TimeUtil.timeFormat1(item.lastDateline, R.string.post_time1, context))
                .addOnClickListener(R.id.item_private_msg_user_icon);
        Glide.with(context).load(item.toUserAvatar).into((CircleImageView)helper.getView(R.id.item_private_msg_user_icon));

        if (item.isNew == 1) {
            helper.getView(R.id.item_private_msg_unread).setVisibility(View.VISIBLE);
            CommonUtil.setVectorColor(context,
                    (ImageView) helper.getView(R.id.item_private_msg_unread),
                    R.drawable.ic_new,
                    R.color.colorPrimary);
        } else {
            helper.getView(R.id.item_private_msg_unread).setVisibility(View.GONE);
        }

    }
}
