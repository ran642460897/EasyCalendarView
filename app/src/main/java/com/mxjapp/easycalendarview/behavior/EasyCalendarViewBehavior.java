package com.mxjapp.easycalendarview.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.mxjapp.easycalendarview.EasyCalendarView;

/**
 * user: Jason Ran
 * date: 2018/7/30.
 */
public class EasyCalendarViewBehavior extends CoordinatorLayout.Behavior<EasyCalendarView> {
    private float offsetY;
    public EasyCalendarViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, EasyCalendarView child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, EasyCalendarView child, View dependency) {
//        child.offsetTopAndBottom(-20);
        return super.onDependentViewChanged(parent, child, dependency);
    }
    private float preY=0;
    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, EasyCalendarView child, MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                preY=ev.getY();
                Log.i("ssssssssssssssssss","down");
                break;
            case MotionEvent.ACTION_MOVE:
                moveY(child,ev.getY()-preY);
                break;
            case MotionEvent.ACTION_UP:
                Log.i("ssssssssssssssssss","up");
                animationY(child);
                break;
        }
        return true;
    }
    private void moveY(View v,float y){
        v.offsetTopAndBottom((int) y);
        offsetY=offsetY+y;
    }
    private void animationY(View view){
        float start=offsetY;
        float end=0;
        Log.i("sssssssssssss","offset:"+offsetY);
        Animation translateAnimation = new TranslateAnimation(view.getX(),view.getX(),start,end);//平移动画  从0,0,平移到100,100
        translateAnimation.setDuration(500);
//        translateAnimation.setFillEnabled(true);
//        translateAnimation.setFillAfter(true);
//        layout((int) getX(),0,(int)maxWidth,(int)(end+maxHeight));
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                offsetY=0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setAnimation(translateAnimation);
        view.startAnimation(translateAnimation);
    }
}
