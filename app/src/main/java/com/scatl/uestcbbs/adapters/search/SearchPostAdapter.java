package com.scatl.uestcbbs.adapters.search;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.SearchPostBean;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/4 18:32
 */
public class SearchPostAdapter extends BaseQuickAdapter<SearchPostBean.ListBean, BaseViewHolder> {

    private Context context;

    public SearchPostAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addSearchPostData(List<SearchPostBean.ListBean> data, boolean refresh) {
        if (refresh) {
            setNewData(data);
        } else {
            addData(data);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchPostBean.ListBean item) {
        helper.setText(R.id.item_post_title, item.title)
                .setText(R.id.item_post_content, item.subject)
                .setText(R.id.item_post_user_name, item.user_nick_name)
                .setText(R.id.item_post_comments_count, context.getResources().getString(R.string.comment_count, item.replies))
                .setText(R.id.item_post_view_count, context.getResources().getString(R.string.view_count, item.hits))
                .setText(R.id.item_post_time,
                        TimeUtil.timeFormat1(item.last_reply_date, R.string.reply_time, context))
                .addOnClickListener(R.id.item_post_user_avatar);

		CommonUtil.setVectorColor(context, (ImageView) helper.getView(R.id.item_post_poll_icon), R.drawable.ic_poll, R.color.colorPrimary);
        helper.getView(R.id.item_post_poll_rl).setVisibility(item.vote == 1 ? View.VISIBLE : View.GONE);
        
		String icon = context.getResources().getString(R.string.icon_url, item.user_id);
        Glide.with(context).load(icon).into((CircleImageView)helper.getView(R.id.item_post_user_avatar));

    }
}
