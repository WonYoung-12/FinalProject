package com.example.kwy2868.finalproject.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kwy2868.finalproject.View.HospitalFragment;
import com.example.kwy2868.finalproject.View.MyInfoFragment;
import com.example.kwy2868.finalproject.View.SearchFragment;

/**
 * Created by kwy2868 on 2017-08-01.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private String[] tabTitles = new String[]{"Hospital", "Search", "My Info"};

    private static final int ITEM_COUNT = 3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
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
                HospitalFragment districtFragment = new HospitalFragment();
                return districtFragment;
            case 1:
                SearchFragment searchFragment = new SearchFragment();
                return searchFragment;
            case 2:
                MyInfoFragment myInfoFragment = new MyInfoFragment();
                return myInfoFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }
}
