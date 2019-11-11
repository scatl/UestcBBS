package com.scatl.uestcbbs.adapters.homepage1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.imageview.CircleImageView;
import com.scatl.uestcbbs.entities.PostListBean;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

/**
 * author: sca_tl
 * description: 主页样式2的横向recyclerview
 * date: 2019/8/4 16:42
 */
public class HotPostAdapter1 extends BaseQuickAdapter<PostListBean, BaseViewHolder> {

    private Context context;

    public HotPostAdapter1(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addPostData(List<PostListBean> list, boolean isRefresh){
        if(isRefresh) { setNewData(list); } else { addData(list); }
    }

    @Override
    protected void convert(final BaseViewHolder helper, PostListBean item) {
        helper.setText(R.id.main_page_hot_post_user_name, item.user_nick_name)
                .setText(R.id.main_page_hot_post_board_name, item.board_name)
                .setText(R.id.main_page_hot_post_post_title, item.title)
                .setText(R.id.main_page_hot_post_comments_count, context.getResources().getString(R.string.comment_count, item.replies))
                .setText(R.id.main_page_hot_post_watch_count, context.getResources().getString(R.string.view_count, item.hits))
                .setText(R.id.main_page_hot_post_post_content, String.valueOf(item.subject))
                .setText(R.id.main_page_hot_post_post_time,
                        TimeUtil.timeFormat1(String.valueOf(item.last_reply_date), R.string.post_time, context))
                .addOnClickListener(R.id.main_page_hot_post_user_avatar);
        Glide.with(context).load(item.user_avatar).into((CircleImageView)helper.getView(R.id.main_page_hot_post_user_avatar));

        Glide.with(context).asBitmap().load(item.user_avatar).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                Palette.Builder builder = Palette.from(resource);
                builder.generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {
                        if (palette != null) {

                            Palette.Swatch lightVibrantSwatch = palette.getVibrantSwatch();

                            //暗、柔和的颜色
                            int darkMutedColor = palette.getDarkMutedColor(Color.BLUE);
                            //亮、柔和
                            int lightMutedColor = palette.getLightMutedColor(Color.BLUE);
                            //暗、鲜艳
                            int darkVibrantColor = palette.getDarkVibrantColor(Color.BLUE);
                            //亮、鲜艳
                            int lightVibrantColor = palette.getLightVibrantColor(Color.BLUE);
                            //柔和
                            int mutedColor = palette.getMutedColor(Color.BLUE);
                            //鲜艳
                            int vibrantColor = palette.getVibrantColor(Color.BLUE);

//                            GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
//                                    new int[]{lightMutedColor, lightVibrantColor});
                            //drawable.setCornerRadius(10);
                            helper.getView(R.id.main_page_hot_post_rl).setBackgroundColor(getTtanslucentColor(150, mutedColor));
                        }

                    }
                });
            }
        });

    }

    private int getTtanslucentColor(float f, int rgb) {
        int blue = rgb & 0xff;
        int green = rgb >> 8 & 0xff;
        int red = rgb >> 16 & 0xff;
        int alpha = rgb >>> 24;
        alpha = Math.round(alpha * f);
        return Color.argb(alpha, red, green, blue);
    }

}
