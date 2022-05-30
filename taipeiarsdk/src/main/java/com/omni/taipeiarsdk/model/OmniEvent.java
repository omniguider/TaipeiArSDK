package com.omni.taipeiarsdk.model;

import android.os.Bundle;

public class OmniEvent {
    public static final int TYPE_USER_AR_LOCATION = 1;
    public static final int TYPE_USER_AR_INTERACTIVE_TEXT = 2;
    public static final int TYPE_SHOW_AR_PATTERNS = 3;
    public static final int TYPE_OPEN_AR_RECOGNIZE = 4;
    public static final int TYPE_OPEN_AR_GUIDE = 5;
    public static final int TYPE_SHOW_AR_MODEL = 6;
    public static final int TYPE_SEARCH_FILTER = 7;
    public static final int TYPE_OPEN_AR_MISSION = 8;
    public static final int TYPE_MISSION_COMPLETE = 9;
    public static final int TYPE_REWARD_COMPLETE = 10;

    private int mType;
    private String mContent;
    private Object mObj;
    private Bundle mArgs;

    public OmniEvent(int type, String content) {
        mType = type;
        mContent = content;
    }

    public OmniEvent(int type, Object obj) {
        mType = type;
        mObj = obj;
    }

    public OmniEvent(int type, Bundle args) {
        mType = type;
        mArgs = args;
    }

    public int getType() {
        return mType;
    }

    public String getContent() {
        return mContent;
    }

    public Object getObj() {
        return mObj;
    }

    public Bundle getArguments() { return mArgs; }
}
