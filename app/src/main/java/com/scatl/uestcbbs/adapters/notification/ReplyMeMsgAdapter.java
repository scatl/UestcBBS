package com.scatl.uestcbbs.adapters.notification;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.ReplyMeMsgBean;
import com.scatl.uestcbbs.services.HeartMsgService;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 15:51
 */
public class ReplyMeMsgAdapter extends BaseQuickAdapter<ReplyMeMsgBean.BodyBean.ListBean, BaseViewHolder> {

    private Context context;

    public ReplyMeMsgAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;

    }

    public void addReplyMeMsgData(List<ReplyMeMsgBean.BodyBean.ListBean> data, boolean refresh) {
        if (refresh) { setNewData(data); } else { addData(data); }
    }

    @Override
    protected void convert(BaseViewHolder helper, ReplyMeMsgBean.BodyBean.ListBean item) {
        helper.setText(R.id.item_reply_me_user_name, item.user_name)
                .setText(R.id.item_reply_me_reply_content, (item.reply_content == null ? "" : item.reply_content)
                        .replace("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n", "\n")
                        .replace("\r\n", ""))
                .setText(R.id.item_reply_me_quote_content, "我在主题《" + item.topic_subject + "》中的评论：\n" + item.topic_content)
                .setText(R.id.item_reply_me_reply_date,
                        TimeUtil.timeFormat1(item.replied_date, R.string.post_time1, context))
                .addOnClickListener(R.id.item_reply_me_user_icon)
                .addOnClickListener(R.id.item_reply_me_quote_rl)
                .addOnClickListener(R.id.item_reply_me_reply_btn);
        Glide.with(context).load(item.icon).into((CircleImageView)helper.getView(R.id.item_reply_me_user_icon));

        //显示未读标志
        if (helper.getLayoutPosition() < HeartMsgService.reply_me_msg_count) {
            helper.getView(R.id.item_reply_me_new_msg_img).setVisibility(View.VISIBLE);
            CommonUtil.setVectorColor(context,
                    (ImageView) helper.getView(R.id.item_reply_me_new_msg_img),
                    R.drawable.ic_new,
                    R.color.colorPrimary);
        } else {
            helper.getView(R.id.item_reply_me_new_msg_img).setVisibility(View.GONE);
        }

    }
}
