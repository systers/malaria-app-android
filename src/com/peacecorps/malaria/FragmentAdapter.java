package com.peacecorps.malaria;

import com.viewpagerindicator.IconPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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

		return 3;
	}
}