package com.peacecorps.malaria.adapter;

import com.peacecorps.malaria.fragment.HomeScreenFragment;
import com.peacecorps.malaria.fragment.SecondAnalyticFragment;
import com.peacecorps.malaria.fragment.FirstAnalyticFragment;
import com.viewpagerindicator.IconPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**Usd for creating sliding Fragment Screen after the the Home Screen**/

public class FragmentAdapter extends FragmentStatePagerAdapter implements
        IconPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public int getIconResId(int index) {

        return 0;
    }

    @Override
    public Fragment getItem(int position) {
        //Deciding the fragment on the basis of position no.
        Fragment fragment = new HomeScreenFragment();
        switch (position) {
            case 0:
                fragment = new HomeScreenFragment();
                break;
            case 1:
                fragment = new FirstAnalyticFragment();
                break;

            case 2:
                fragment = new SecondAnalyticFragment();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        //total no. of fragments
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}