package com.scatl.uestcbbs.adapters.notification;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.AtMeMsgBean;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/14 21:02
 */
public class AtMeMsgAdapter extends BaseQuickAdapter<AtMeMsgBean.BodyBean.DataBean, BaseViewHolder> {

    private Context context;

    public AtMeMsgAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addAtMeMsgData(List<AtMeMsgBean.BodyBean.DataBean> data, boolean refresh) {
        if (refresh) {setNewData(data);} else {addData(data);}

    }

    @Override
    protected void convert(BaseViewHolder helper, AtMeMsgBean.BodyBean.DataBean item) {
        helper.setText(R.id.item_at_me_name, item.user_name)
                .setText(R.id.item_at_me_reply_content, "提及内容：" + item.reply_content.replaceAll("\r\n", ""))
                .setText(R.id.item_at_me_board_name, "板块：" + item.board_name)
                .setText(R.id.item_at_me_subject, "主题：" + item.topic_subject)
                .setText(R.id.item_at_me_content, "主题内容：" + item.topic_content.trim())
                .setText(R.id.item_at_me_time,
                        "时间：" + TimeUtil.timeFormat1(item.replied_date, R.string.post_time1, context))
                .addOnClickListener(R.id.item_at_me_icon);

        Glide.with(context).load(item.icon).into((CircleImageView)helper.getView(R.id.item_at_me_icon));

    }
}
