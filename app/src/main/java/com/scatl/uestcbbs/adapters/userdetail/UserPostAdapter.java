package com.scatl.uestcbbs.adapters.userdetail;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.UserPostBean;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/19 12:21
 */
public class UserPostAdapter extends BaseQuickAdapter<UserPostBean.ListBean, BaseViewHolder> {

    private Context context;

    private String type;

    //有的帖子列表的last_reply_date实际上是帖子发表时间（例如用户的回复和发表）
    public static final String TYPE_POST_DATE = "post_date"; //last_reply_date是帖子发表时间
    public static final String TYPE_REPLY_DATE = "reply_date";//last_reply_date是最新回复时间

    public UserPostAdapter(Context context, String type, int layoutResId) {
        super(layoutResId);
        this.context = context;
        this.type = type;
    }

    public void addPostListData(List<UserPostBean.ListBean> data, boolean refresh) {
        if (refresh) {setNewData(data);} else {addData(data);}
    }

    @Override
    protected void convert(BaseViewHolder helper, UserPostBean.ListBean item) {
        helper.setText(R.id.item_post_user_name, item.user_nick_name)
                .setText(R.id.item_post_board_name, item.board_name)
                .setText(R.id.item_post_title, item.title)
                .setText(R.id.item_post_comments_count, String.valueOf(item.replies + "评论"))
                .setText(R.id.item_post_content, String.valueOf(item.subject))
                .setText(R.id.item_post_view_count, String.valueOf(item.hits + "阅读"))
                .setText(R.id.item_post_time,
                        TimeUtil.timeFormat1(item.last_reply_date,
                                type.equals(TYPE_REPLY_DATE) ? R.string.reply_time : R.string.post_time,
                                context))
                .addOnClickListener(R.id.item_post_user_avatar);
        //CommonUtil.setVectorColor(context, (ImageView) helper.getView(R.id.item_post_poll_icon), R.drawable.ic_poll, R.color.colorPrimary);
        helper.getView(R.id.item_post_poll_rl).setVisibility(View.GONE);

        Glide.with(context).load(item.userAvatar).into((CircleImageView)helper.getView(R.id.item_post_user_avatar));

    }
}
