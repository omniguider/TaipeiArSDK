package com.omni.taipeiarsdk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import com.omni.taipeiarsdk.R;

public class OmniViewPager extends ViewPager {

    private boolean mSlidePagingEnabled = true;

    public OmniViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OmniViewPager);
        mSlidePagingEnabled = typedArray.getBoolean(R.styleable.OmniViewPager_slide_paging_enabled, true);

        typedArray.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mSlidePagingEnabled) {
            return super.onTouchEvent(ev);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mSlidePagingEnabled) {
            return super.onInterceptTouchEvent(ev);
        }

        return false;
    }

    public void setSlidePagingEnabled(boolean enabled) {
        mSlidePagingEnabled = enabled;
    }
}
