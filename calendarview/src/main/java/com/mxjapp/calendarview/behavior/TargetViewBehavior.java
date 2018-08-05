package com.mxjapp.calendarview.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mxjapp.calendarview.EasyCalendarView;


/**
 * user: Jason Ran
 * date: 2018/7/30.
 */
public class TargetViewBehavior extends CoordinatorLayout.Behavior<View> {
    public TargetViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return dependency instanceof EasyCalendarView;
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);

    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        EasyCalendarView view=(EasyCalendarView)dependency;
        if(!view.isFrozen()) {
            child.setTranslationY(view.getY() + view.getViewHeight());
        }
        return true;
    }
}
