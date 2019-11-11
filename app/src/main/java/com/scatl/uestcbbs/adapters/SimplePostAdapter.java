package com.scatl.uestcbbs.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.PostListBean;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 14:50
 */
public class SimplePostAdapter extends BaseQuickAdapter<PostListBean, BaseViewHolder> {

    private Context context;

    private String type;

    //有的帖子列表的last_reply_date实际上是帖子发表时间（例如用户的回复和发表）
    public static final String TYPE_POST_DATE = "post_date"; //last_reply_date是帖子发表时间
    public static final String TYPE_REPLY_DATE = "reply_date";//last_reply_date是最新回复时间

    public SimplePostAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addPostData(List<PostListBean> data, boolean refresh) {
        if (refresh) {
            setNewData(data);
        } else {
            List<PostListBean> filter_list = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < data.size(); i ++) {
                int top_id = data.get(i).topic_id;

                for (int j = 0; j < getData().size(); j ++) {
                    int id = getData().get(j).topic_id;
                    ids.add(id);
                }

                if (! ids.contains(top_id)) { filter_list.add(data.get(i)); }
            }
            addData(filter_list);
        }
        notifyDataSetChanged();
    }

    public void deleteAllData() {
        setNewData(new ArrayList<PostListBean>());
    }

    @Override
    protected void convert(BaseViewHolder helper, PostListBean item) {

        helper.setText(R.id.item_post_user_name, item.user_nick_name)
                .setText(R.id.item_post_board_name, item.board_name)
                .setText(R.id.item_post_title, item.title)
                .setText(R.id.item_post_comments_count, context.getResources().getString(R.string.comment_count, item.replies))
                .setText(R.id.item_post_content, String.valueOf(item.subject))
                .setText(R.id.item_post_view_count, context.getResources().getString(R.string.view_count, item.hits))
                .setText(R.id.item_post_time,
                        TimeUtil.timeFormat1(String.valueOf(item.last_reply_date),
                        type.equals(TYPE_REPLY_DATE) ? R.string.reply_time : R.string.post_time, context))
                .addOnClickListener(R.id.item_post_user_avatar);

        CommonUtil.setVectorColor(context, (ImageView) helper.getView(R.id.item_post_poll_icon), R.drawable.ic_poll, R.color.colorPrimary);
        helper.getView(R.id.item_post_poll_rl).setVisibility(item.vote == 1 ? View.VISIBLE : View.GONE);

        Glide.with(context).load(item.user_avatar).into((CircleImageView)helper.getView(R.id.item_post_user_avatar));

    }
}
