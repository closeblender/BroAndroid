package com.closestudios.bro.util;

import android.support.v4.view.ViewPager;

import java.util.ArrayList;

/**
 * Created by closestudios on 11/24/15.
 */
public class TabController implements ViewPager.OnPageChangeListener, TabGroup.TabGroupSelectedListener {

    ArrayList<TabGroup> tabs = new ArrayList<>();
    ViewPager viewPager;

    public TabController() {
    }

    public void addTab(TabGroup tab) {
        tab.setListener(this);
        tabs.add(tab);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
    }

    public void setActiveTab(int position) {
        for(int i=0;i<tabs.size();i++) {
            tabs.get(i).setActive(position == i);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setActiveTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabGroup tabGroup) {
        viewPager.setCurrentItem(tabs.indexOf(tabGroup));
    }

}
