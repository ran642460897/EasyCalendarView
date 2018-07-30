package com.mxjapp.easycalendarview.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mxjapp.easycalendarview.EasyCalendarView;

/**
 * user: Jason Ran
 * date: 2018/7/30.
 */
public class RecyclerViewBehavior extends CoordinatorLayout.Behavior<RecyclerView> {
    public RecyclerViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency) {
        return dependency instanceof EasyCalendarView;
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int type) {
        boolean similar=target instanceof EasyCalendarView;
        Log.i("ssssssssssssssss","onStopNestedScroll");
        Log.i("ssssssssssssssss","target:"+similar);
        super.onStopNestedScroll(coordinatorLayout, child, target, type);

    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency) {
        Log.i("sssssssssssssssss","onDependentViewChanged");
        EasyCalendarView view=(EasyCalendarView)dependency;
        child.setY(view.getY()+view.getViewHeight());
//        child.setY();
        return true;
    }
}
