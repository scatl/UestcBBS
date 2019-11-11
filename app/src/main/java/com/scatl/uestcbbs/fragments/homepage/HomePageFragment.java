package com.scatl.uestcbbs.fragments.homepage;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.CommonPagerAdapter;
import com.scatl.uestcbbs.base.BaseFragment;
import com.scatl.uestcbbs.utils.Constants;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;


public class HomePageFragment extends BaseFragment {

    private static final String TAG = "HomePageFragment";

    private String[] titles = {"最新发布", "最新回复", "近期热门"};

    public static HomePageFragment newInstance(Bundle bundle) {
        HomePageFragment homePageFragment = new HomePageFragment();
        homePageFragment.setArguments(bundle);
        return homePageFragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_home_page;
    }

    @Override
    protected void init() {
        initIndicator();
    }

    /**
     * author: sca_tl
     * description: 初始化指示器
     */
    private void initIndicator(){
        ArrayList<Fragment> fragments = new ArrayList<>();

        final ViewPager viewPager = view.findViewById(R.id.home_page_viewpager);
        MagicIndicator magicIndicator = view.findViewById(R.id.home_page_indicator);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.Key.TYPE, Constants.Api.TYPE_LATEST_POST);
        fragments.add(PostListFragment.newInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constants.Key.TYPE, Constants.Api.TYPE_LATEST_REPLY);
        fragments.add(PostListFragment.newInstance(bundle1));

        Bundle bundle2 = new Bundle();
        bundle2.putString(Constants.Key.TYPE, Constants.Api.TYPE_HOT_POST);
        fragments.add(PostListFragment.newInstance(bundle2));

        viewPager.setOffscreenPageLimit(2);

        FragmentManager fragmentManager = getChildFragmentManager();//此处需注意方法
        viewPager.setAdapter(new CommonPagerAdapter(fragmentManager, titles, fragments));
        viewPager.setCurrentItem(0);

        CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() { return titles.length; }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                ColorTransitionPagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(titles[i]);
                simplePagerTitleView.setTextSize(16);//设置导航的文字大小
                simplePagerTitleView.setNormalColor(Color.WHITE);//正常状态下的标题颜色
                simplePagerTitleView.setSelectedColor(Color.WHITE);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(i);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(7);
                indicator.setLineWidth(100);
                indicator.setRoundRadius(10);
                indicator.setYOffset(20);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(Color.parseColor("#eeeeee"));

                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }


}
