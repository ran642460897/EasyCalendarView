package com.mxjapp.easycalendarview.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mxjapp.easycalendarview.EasyCalendarView;


/**
 * user: Jason Ran
 * date: 2018/7/25.
 */
public class RecyclerViewBehavior1 extends CoordinatorLayout.Behavior<RecyclerView> {
    private int initOffset = -1;
    private int minOffset = -1;
    private Context context;
    private boolean initiated = false;
    boolean hidingTop = false;
    boolean showingTop = false;


    public RecyclerViewBehavior1(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
        EasyCalendarView monthPager = getEasyCalendarView(parent);
        initMinOffsetAndInitOffset(parent, child, monthPager);
        return true;
    }

    private void initMinOffsetAndInitOffset(CoordinatorLayout parent,
                                            RecyclerView child,
                                            EasyCalendarView view) {
        if (view.getBottom() > 0 && initOffset == -1) {
            initOffset = (int)view.getViewHeight();
            saveTop(initOffset);
        }
        if (!initiated) {
            initOffset =(int) view.getViewHeight();
            saveTop(initOffset);
            initiated = true;
        }
//        child.offsetTopAndBottom(Utils.loadTop());
//        minOffset = getEasyCanlendarView(parent).getCellHeight();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        EasyCalendarView view = getEasyCalendarView(coordinatorLayout);
        view.setScrollable(false);
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        child.setVerticalScrollBarEnabled(true);
        EasyCalendarView view=getEasyCalendarView(coordinatorLayout);
        if (view.getPageScrollState() != ViewPager.SCROLL_STATE_IDLE) {
            consumed[1] = dy;
            Log.w("ldf", "onNestedPreScroll: MonthPager dragging");
//            Toast.makeText(context, "loading month data", Toast.LENGTH_SHORT).show();
            return;
        }
        // 上滑，正在隐藏顶部的日历
        hidingTop = dy > 0 && child.getTop() <= initOffset
                && child.getTop() > view.getItemHeight();
        // 下滑，正在展示顶部的日历
        showingTop = dy < 0 && !ViewCompat.canScrollVertically(target, -1);

//        if (hidingTop || showingTop) {
//            consumed[1] = Utils.scroll(child, dy,
//                    view.getItemHeight(),
//                    view.getViewHeight());
//            saveTop(child.getTop());
//        }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        EasyCalendarView view=getEasyCalendarView(coordinatorLayout);
        view.setScrollable(true);
//        if (!Utils.isScrollToBottom()) {
//            if (initOffset - Utils.loadTop() > Utils.getTouchSlop(context) && hidingTop) {
//                Utils.scrollTo(parent, child, getMonthPager(parent).getCellHeight(), 500);
//            } else {
//                Utils.scrollTo(parent, child, getMonthPager(parent).getViewHeight(), 150);
//            }
//        } else {
//            if (Utils.loadTop() - minOffset > Utils.getTouchSlop(context) && showingTop) {
//                Utils.scrollTo(parent, child, getMonthPager(parent).getViewHeight(), 500);
//            } else {
//                Utils.scrollTo(parent, child, getMonthPager(parent).getCellHeight(), 150);
//            }
//        }
    }


    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.d("ldf", "onNestedFling: velocityY: " + velocityY);
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, float velocityX, float velocityY) {
        // 日历隐藏和展示过程，不允许RecyclerView进行fling
        if (hidingTop || showingTop) {
            return true;
        } else {
            return false;
        }
    }

    private EasyCalendarView getEasyCalendarView(CoordinatorLayout coordinatorLayout) {
        EasyCalendarView easyCalendarView=null;
        for(int i=0;i<coordinatorLayout.getChildCount();i++){
            if(coordinatorLayout.getChildAt(i) instanceof EasyCalendarView) {
                easyCalendarView = (EasyCalendarView) coordinatorLayout.getChildAt(i);
                break;
            }
        }
        return easyCalendarView;
    }

    private void saveTop(int top) {
//        Utils.saveTop(top);
//        if (Utils.loadTop() == initOffset) {
//            Utils.setScrollToBottom(false);
//        } else if (Utils.loadTop() == minOffset) {
//            Utils.setScrollToBottom(true);
//        }
    }

}
