package com.closestudios.bro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by closestudios on 11/24/15.
 */
public class MainMenuSlideAdapter extends FragmentStatePagerAdapter {
    
    BroFragment broFragment;
    LeaderboardFragment leaderboardFragment;
    BroMapFragment broMapFragment;
    
    public MainMenuSlideAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return getBroFragment();
            case 1:
                return getBroMapFragment();
            case 2:
                return getLeaderboardFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
    
    public BroFragment getBroFragment() {
        if(broFragment == null) {
            broFragment = BroFragment.newInstance();
        }
        return broFragment;
    }

    public LeaderboardFragment getLeaderboardFragment() {
        if(leaderboardFragment == null) {
            leaderboardFragment = LeaderboardFragment.newInstance();
        }
        return leaderboardFragment;
    }

    public BroMapFragment getBroMapFragment() {
        if(broMapFragment == null) {
            broMapFragment = new BroMapFragment();
        }
        return broMapFragment;
    }
}