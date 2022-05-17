package com.omni.taipeiarsdk.view.mission;

import android.content.Context;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MissionPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public MissionPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        mContext = context;
    }

    @Override
    public int getCount() {
        return MissionPagerModule.values().length;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == MissionPagerModule.MISSION.ordinal()) {
            return MissionListFragment.newInstance();
        } else if (position == MissionPagerModule.REWARD.ordinal()) {
            return RewardListFragment.newInstance();
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(MissionPagerModule.values()[position].getTitleResId());
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
