package com.example.kwy2868.finalproject.Adapter;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kwy2868.finalproject.View.DistanceFragment;
import com.example.kwy2868.finalproject.View.DistrictFragment;
import com.example.kwy2868.finalproject.View.SearchFragment;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private String[] tabTitles = new String[]{"지역별", "거리순", "검색"};

    private Fragment[] fragmentArray;

    private static final int ITEM_COUNT = 3;

    private Location location;

    public ViewPagerAdapter(FragmentManager fm, Location location) {
        super(fm);
        this.location = location;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    // 현재 포지션의 프래그먼트를 반환함.
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                DistrictFragment districtFragment = new DistrictFragment();
                return districtFragment;
            case 1:
                DistanceFragment distanceFragment = DistanceFragment.newInstance(location);
                return distanceFragment;
            case 2:
                SearchFragment searchFragment = new SearchFragment();
                return searchFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}
