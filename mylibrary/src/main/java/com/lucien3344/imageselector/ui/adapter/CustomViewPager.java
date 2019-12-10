package com.lucien3344.imageselector.ui.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 *
 * @author lsh_2012@qq.com
 * on 2019/10/27.
 */
public class CustomViewPager extends ViewPager {

    private boolean isScroll;

    public CustomViewPager(Context context) {
        super(context);
        isScroll = false;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isScroll = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isScroll) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !isScroll && super.onTouchEvent(event);
    }

    public void toggleScroll() {
        isScroll = !isScroll;
    }

    public void setScroll(boolean isScroll) {
        this.isScroll = isScroll;
    }

    public boolean isScroll() {
        return isScroll;
    }

}