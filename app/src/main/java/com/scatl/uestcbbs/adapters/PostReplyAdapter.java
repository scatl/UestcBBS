package com.scatl.uestcbbs.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.custom.postview.ContentView;
import com.scatl.uestcbbs.entities.PostDetailBean;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/5 14:54
 */
public class PostReplyAdapter extends BaseQuickAdapter<PostDetailBean.PostReplyBean, BaseViewHolder> {

    private Context context;
    private onImageClickListener onImageClickListener;
    private int author_id;

    public PostReplyAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void setAuthorId (int id) {
        this.author_id = id;
    }

    public void addPostReplyData(List<PostDetailBean.PostReplyBean> list, boolean isRefresh){
        if( isRefresh) { setNewData(list);} else { addData(list); }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void convert(BaseViewHolder helper, PostDetailBean.PostReplyBean item) {
        helper.setText(R.id.post_reply_author_name, item.reply_name)
                .setText(R.id.post_reply_author_time,
                        TimeUtil.timeFormat1(item.posts_date, R.string.post_time1, context))
                .setText(R.id.post_reply_floor, context.getResources().getString(R.string.reply_floor, helper.getLayoutPosition() + 1))
                .addOnClickListener(R.id.reply_button)
                .addOnClickListener(R.id.post_reply_author_avatar);

        Glide.with(context).load(item.icon).into((CircleImageView)helper.getView(R.id.post_reply_author_avatar));

        if (item.reply_id == author_id) {
            helper.getView(R.id.post_reply_author_iamauthor).setVisibility(View.VISIBLE);
            helper.getView(R.id.post_reply_author_iamauthor).setBackgroundResource(R.drawable.shape_textview_level_reply);
        } else {
            helper.getView(R.id.post_reply_author_iamauthor).setVisibility(View.GONE);
        }

        if (item.userTitle != null && item.userTitle.length() != 0 ) {
            Matcher matcher = Pattern.compile("(.*?)\\((Lv\\..*)\\)").matcher(item.userTitle);
            if (matcher.find()) {
                helper.setText(R.id.post_reply_author_level, matcher.group(2));
            } else {
                helper.setText(R.id.post_reply_author_level, item.userTitle);
            }
            helper.getView(R.id.post_reply_author_level).setBackgroundResource(R.drawable.shape_textview_level_reply);
        } else if (item.userTitle != null) {
            helper.setText(R.id.post_reply_author_level, item.reply_name);
            helper.getView(R.id.post_reply_author_level).setBackgroundResource(R.drawable.shape_textview_level_reply);
        }

        //有引用内容
        if (item.is_quote == 1) {

            Matcher matcher = Pattern.compile("(.*?)发表于(.*?)\n(.*)").matcher(item.quote_content);
            if (matcher.find()) {
                String name = matcher.group(1).trim();
                String time = matcher.group(2).trim();
                String content = matcher.group(3);

                String time__ = TimeUtil.timeFormat1(
                        String.valueOf(TimeUtil.getMilliSecond(time, "yyyy-MM-dd HH:mm")),
                        R.string.post_time1,
                        context);

                helper.getView(R.id.reply_to_rl).setVisibility(View.VISIBLE);
                helper.setText(R.id.reply_to_rl_text, context.getResources().getString(R.string.quote_content, name, time__, content));
            } else {
                helper.getView(R.id.reply_to_rl).setVisibility(View.VISIBLE);
                helper.setText(R.id.reply_to_rl_text, item.quote_content);
            }

        } else {
            helper.getView(R.id.reply_to_rl).setVisibility(View.GONE);
        }

        ((ContentView)helper.getView(R.id.reply_from_rl)).setContentData(item.postContentBeanList);
        ((ContentView)helper.getView(R.id.reply_from_rl)).setOnImageClickListener(new ContentView.OnImageClickListener() {
            @Override
            public void onImageClick(View view, List<String> urls, int selected) {
                onImageClickListener.onImgClick(view, urls, selected);
            }
        });

    }

    public interface onImageClickListener {
        void onImgClick(View view, List<String> urls, int selected);
    }

    public void setOnImgClickListener(onImageClickListener onImageClickListener){
        this.onImageClickListener = onImageClickListener;
    }

}
