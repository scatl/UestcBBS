package com.scatl.uestcbbs.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class CommonPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;
    private ArrayList<Fragment> fragments;

    public CommonPagerAdapter(FragmentManager manager, String[] titles, ArrayList<Fragment> fragments){
        super(manager);
        this.titles = titles;
        this.fragments = fragments;
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}