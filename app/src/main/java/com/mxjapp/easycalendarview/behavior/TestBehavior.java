package com.mxjapp.easycalendarview.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mxjapp.easycalendarview.EasyCalendarView;
import com.mxjapp.easycalendarview.util.UnitsUtil;


/**
 * user: Jason Ran
 * date: 2018/7/27.
 */
public class TestBehavior extends CoordinatorLayout.Behavior<TextView>{
    private Context context;
    public TestBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof EasyCalendarView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        Log.i("ssssssssssssss","onDependentViewChanged");
        child.setY(dependency.getY()+ UnitsUtil.dip2px(context,360));
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
