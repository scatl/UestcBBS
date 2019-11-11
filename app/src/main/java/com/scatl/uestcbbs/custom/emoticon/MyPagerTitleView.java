package com.scatl.uestcbbs.custom.emoticon;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.utils.CommonUtil;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

public class MyPagerTitleView extends RelativeLayout implements IPagerTitleView {

    private ImageView imageView;

    public MyPagerTitleView(Context context) {
        super(context);
        init();
    }

    public MyPagerTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyPagerTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        imageView = new ImageView(getContext());
//        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(CommonUtil.dip2px(getContext(), 25), CommonUtil.dip2px(getContext(), 25));
//        img_params.leftMargin = CommonUtil.dip2px(getContext(), 10);
//        img_params.rightMargin = CommonUtil.dip2px(getContext(), 10);
//        img_params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        imageView.setLayoutParams(img_params);
        addView(imageView);
    }

    public void setImageSource(String path) {
        Glide.with(this).load(path).into(imageView);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setColor(getResources().getColor(R.color.background_light));
        setBackground(gradientDrawable);

        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(CommonUtil.dip2px(getContext(), 27), CommonUtil.dip2px(getContext(), 27));
        img_params.leftMargin = CommonUtil.dip2px(getContext(), 10);
        img_params.rightMargin = CommonUtil.dip2px(getContext(), 10);
        img_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(img_params);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setColor(getResources().getColor(R.color.background_dark));
        setBackground(gradientDrawable);

        RelativeLayout.LayoutParams img_params = new RelativeLayout.LayoutParams(CommonUtil.dip2px(getContext(), 23), CommonUtil.dip2px(getContext(), 23));
        img_params.leftMargin = CommonUtil.dip2px(getContext(), 10);
        img_params.rightMargin = CommonUtil.dip2px(getContext(), 10);
        img_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(img_params);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {

    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

    }
}
