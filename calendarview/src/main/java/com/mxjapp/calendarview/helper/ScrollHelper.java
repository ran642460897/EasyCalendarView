package com.mxjapp.calendarview.helper;

import android.support.v4.widget.NestedScrollView;

import com.mxjapp.calendarview.EasyCalendarView;

/**
 * user: Jason Ran
 * date: 2018/8/4.
 */
public class ScrollHelper {
    public static final int SCROLL_TYPE_NONE=0;
    public static final int SCROLL_TYPE_TO_TOP=1;
    public static final int SCROLL_TYPE_TO_BOTTOM=2;
    private EasyCalendarView calendarView;
    private NestedScrollView targetView;
    private int finalCalendarDownY,finalTargetDownY,finalCalendarUpY,finalTargetUpY;
    int initTargetHeight=0,initCalendarHeight=0;
}
