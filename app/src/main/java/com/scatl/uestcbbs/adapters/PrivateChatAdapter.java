package com.scatl.uestcbbs.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.PrivateChatBean;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/15 16:51
 */
public class PrivateChatAdapter extends BaseQuickAdapter<PrivateChatBean.PrivateChatListBean, BaseViewHolder> {

    private Context context;

    private PrivateChatBean.PrivateChatHisBean privateChatHisBean;

    public PrivateChatAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addMsgData(List<PrivateChatBean.PrivateChatListBean> data, PrivateChatBean.PrivateChatHisBean hisData) {
        setNewData(data);
        this.privateChatHisBean = hisData;
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateChatBean.PrivateChatListBean item) {
        int mine_id = SharePrefUtil.getId(context);
        String mine_avatar = SharePrefUtil.getAvatar(context);

        if (item.sender == mine_id) {
            helper.addOnClickListener(R.id.item_private_chat_mine_img);
            helper.getView(R.id.item_private_chat_his_rl).setVisibility(View.GONE);
            helper.getView(R.id.item_private_chat_mine_rl).setVisibility(View.VISIBLE);
            Glide.with(context).load(mine_avatar).into((CircleImageView)helper.getView(R.id.item_private_chat_mine_icon));

            if (item.type.equals("text")) {
                helper.getView(R.id.item_private_chat_mine_content).setVisibility(View.VISIBLE);
                helper.getView(R.id.item_private_chat_mine_img).setVisibility(View.GONE);
                helper.setText(R.id.item_private_chat_mine_content, item.content);
            }
            if (item.type.equals("image")) {
                helper.getView(R.id.item_private_chat_mine_content).setVisibility(View.GONE);
                helper.getView(R.id.item_private_chat_mine_img).setVisibility(View.VISIBLE);
                Glide.with(context).load(item.content).into((ImageView) helper.getView(R.id.item_private_chat_mine_img));
            }

            helper.setText(R.id.item_private_chat_mine_time,
                    TimeUtil.timeFormat1(item.time, R.string.post_time1, context));

        } else {
            helper.addOnClickListener(R.id.item_private_chat_his_img);
            helper.getView(R.id.item_private_chat_mine_rl).setVisibility(View.GONE);
            helper.getView(R.id.item_private_chat_his_rl).setVisibility(View.VISIBLE);

            if (item.type.equals("text")) {
                helper.getView(R.id.item_private_chat_his_content).setVisibility(View.VISIBLE);
                helper.getView(R.id.item_private_chat_his_img).setVisibility(View.GONE);
                helper.setText(R.id.item_private_chat_his_content, item.content);
            }
            if (item.type.equals("image")) {
                helper.getView(R.id.item_private_chat_his_content).setVisibility(View.GONE);
                helper.getView(R.id.item_private_chat_his_img).setVisibility(View.VISIBLE);
                Glide.with(context).load(item.content).into((ImageView) helper.getView(R.id.item_private_chat_his_img));
            }

            Glide.with(context).load(privateChatHisBean.avatar).into((CircleImageView)helper.getView(R.id.item_private_chat_his_icon));

            helper.setText(R.id.item_private_chat_his_time,
                    TimeUtil.timeFormat1(item.time, R.string.post_time1, context));

        }

    }
}
