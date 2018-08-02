package com.mxjapp.easycalendarview.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mxjapp.calendarview.EasyCalendarView;

public class CalendarViewBehavior extends CoordinatorLayout.Behavior<EasyCalendarView> {
    public CalendarViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if (target instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) target;
            int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if(pos==0&&dy<0){
                if(child.getY()==child.getInitY()) {
                    child.switchToMonth();
                    child.offsetTopAndBottom(-(int)(child.getMaxHeight()-child.getItemHeight()));
//                    child.offsetTopAndBottom(-dy);
                    consumed[1] = dy;
                }
            }
        }
    }
}
