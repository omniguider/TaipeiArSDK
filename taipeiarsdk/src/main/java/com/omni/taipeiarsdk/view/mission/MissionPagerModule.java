package com.omni.taipeiarsdk.view.mission;

import com.omni.taipeiarsdk.R;

public enum MissionPagerModule {

    MISSION(R.string.info_pager_title_mission),
    REWARD(R.string.info_pager_title_reward);

    private int mTitleResId;

    MissionPagerModule(int titleResId) {
        mTitleResId = titleResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }


}
