package com.mxjapp.calendarview.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.mxjapp.calendarview.CalendarPage;
import com.mxjapp.calendarview.EasyCalendarView;
import com.mxjapp.calendarview.helper.ScrollHelper;

import org.w3c.dom.Text;

public class CalendarViewBehaviorWithScrollView extends CoordinatorLayout.Behavior<EasyCalendarView> {
    private ScrollHelper helper;
    public CalendarViewBehaviorWithScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, EasyCalendarView child, MotionEvent ev) {
//        Log.i("sssssssssssssss","ssssssssssssss");
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, EasyCalendarView child, MotionEvent ev) {
        return helper!=null&&!helper.isScrollable();
//        return false;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View directTargetTarget, @NonNull View target, int axes, int type) {
        boolean touch=type==ViewCompat.TYPE_TOUCH;
        if(helper==null) helper=child.getScrollHelper();
//        Log.i("sssssssssssssss","onStartNestedScroll "+touch+" scrollable:"+helper.isScrollable());
        if(axes == ViewCompat.SCROLL_AXIS_VERTICAL&&type==ViewCompat.TYPE_TOUCH&&helper.startNestedScroll()){
            helper.init(child,target);
            child.setScrollableX(false);
            return true;
        }else return false;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);//dy向上正,向下负
            if(helper.dispatchScroll()){
                if(helper.scroll(dy)) consumed[1]=dy;
            }

    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int type) {
//        Log.i("ssssssssssssssss","onStopNestedScroll");
        helper.translateY(false);
//        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, float velocityX, float velocityY) {
//        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
//        Log.i("sssssssssssssss","onNestedPreFling");
        boolean interceptFling=target.getScrollY()==0;
        if(!interceptFling) {
            helper.setScrollable(false);
            helper.setBlock(false);
        }
        return interceptFling;
//        return true;
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }
}
