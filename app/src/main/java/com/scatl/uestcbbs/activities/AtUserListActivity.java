package com.scatl.uestcbbs.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.CommonPagerAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.fragments.AtUserListFragment;
import com.scatl.uestcbbs.utils.CommonUtil;
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

public class AtUserListActivity extends BaseActivity {

    private static final String TAG = "AtUserListActivity";

    private String[] titles = {"我的好友", "我的关注"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_user_list);

        Toolbar toolbar = findViewById(R.id.at_user_list_toolbar);
        toolbar.setTitle("At列表");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initIndicator();
    }

    /**
     * author: sca_tl
     * description: 初始化指示器
     */
    private void initIndicator(){
        ArrayList<Fragment> fragments = new ArrayList<>();

        final ViewPager viewPager = findViewById(R.id.at_user_list_viewpager);
        MagicIndicator magicIndicator = findViewById(R.id.at_user_list_indicator);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.Key.TYPE, AtUserListFragment.AT_LIST_TYPE_FRIEND);
        fragments.add(AtUserListFragment.newInstance(bundle));

        Bundle bundle1 = new Bundle();
        bundle1.putString(Constants.Key.TYPE, AtUserListFragment.AT_LIST_TYPE_FOLLOW);
        fragments.add(AtUserListFragment.newInstance(bundle1));
        viewPager.setOffscreenPageLimit(2);

        FragmentManager fragmentManager = getSupportFragmentManager();//此处需注意方法
        viewPager.setAdapter(new CommonPagerAdapter(fragmentManager, titles, fragments));
        viewPager.setCurrentItem(0);

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() { return titles.length; }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                ColorTransitionPagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(titles[i]);
                simplePagerTitleView.setTextSize(17);
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(CommonUtil.getAttrColor(AtUserListActivity.this, R.attr.colorPrimary));
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
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setLineHeight(7);
                indicator.setXOffset(50);
                indicator.setRoundRadius(10);
                indicator.setYOffset(10);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(CommonUtil.getAttrColor(AtUserListActivity.this, R.attr.colorPrimary));

                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
