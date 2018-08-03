package com.mxjapp.easycalendarview.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mxjapp.calendarview.EasyCalendarView;

public class CalendarViewBehavior extends CoordinatorLayout.Behavior<EasyCalendarView> {
    private EasyCalendarView calendarView;
    private RecyclerView recyclerView;
    private int finalCalendarUnderY,finalRecyclerUnderY,finalCalendarUpperY,finalRecyclerUpperY;
    float initRecyclerHeight=0,initCalendarHeight=0;
    public CalendarViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    boolean scrolling=false;
    boolean toTop=false;

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull EasyCalendarView child, @NonNull MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                Log.i("sssssssssssss","behavior up");
                break;
        }
        return true;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.i("sssssssssssssssss","onStartNestedScroll");
        if((axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0){
            scrolling=true;
            if(calendarView==null) calendarView=child;
            if(recyclerView==null&& target instanceof RecyclerView) recyclerView=(RecyclerView)target;
            finalCalendarUnderY=(int)child.getInitY();
            finalRecyclerUnderY=(int)(child.getInitY()+child.getUnderHeight()+child.getItemHeight());
            finalCalendarUpperY=(int) (child.getInitY()-child.getUpperHeight());
            finalRecyclerUpperY=(int)(child.getInitY()+child.getItemHeight());
            initCalendarHeight=calendarView.getY();
            initRecyclerHeight=recyclerView.getY();
            return true;
        }else return false;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        Log.i("sssssssssssssssss","onNestedPreScroll");
        if (target instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) target;
            if(recyclerView.getLayoutManager()==null||!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) return;
            int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if(pos==0){
                if(dy<0) {
                    if(recyclerView.getY()<finalRecyclerUnderY){
                        if(recyclerView.getY()-dy>finalRecyclerUnderY){
                            recyclerView.offsetTopAndBottom((int)(finalRecyclerUnderY-recyclerView.getY()));
                        }else{
                            recyclerView.offsetTopAndBottom(-dy);
                        }
                        consumed[1] = dy;
                    }
                    else if(child.getY()<finalCalendarUnderY){
                        if(child.getY()-dy>finalCalendarUnderY){
                            child.offsetTopAndBottom((int)(finalCalendarUnderY-child.getY()));
                        }else{
                            child.offsetTopAndBottom(-dy);
                        }
                        consumed[1] = dy;
                    }
                }else if(dy>0 ){

//                    Log.i("ssssssssssssssssss", "child y:" + child.getY());
//                    Log.i("ssssssssssssssssss", "child init y:" + child.getInitY());
//                    Log.i("ssssssssssssssssss", "child last y:" + finalCalendarUpperY);
//                    Log.i("ssssssssssssssssss", "recycler y:" + recyclerView.getY());
//                    Log.i("ssssssssssssssssss", "recycler last y:" + finalRecyclerUpperY);
//                    Log.i("ssssssssssssssssss", "dy:" + dy);
                    if(child.getY()>finalCalendarUpperY) {
                        if(child.getY()-dy<finalCalendarUpperY) {
                            child.offsetTopAndBottom((int) (finalCalendarUpperY-child.getY()));
                        }else{
                            child.offsetTopAndBottom(-dy);
                        }
                        consumed[1] = dy;
                    }else if(recyclerView.getY()>finalRecyclerUpperY){
                        if(recyclerView.getY()-dy<finalRecyclerUpperY){
                            recyclerView.offsetTopAndBottom((int)(finalRecyclerUpperY-recyclerView.getY()));
                        }else {
                            recyclerView.offsetTopAndBottom(-dy);
                        }
                        consumed[1] = dy;
                    }
                }
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        Log.i("sssssssssssssssss","onNestedScroll");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int type) {
        Log.i("sssssssssssssssss","bbb onStopNestedScroll");
        animationY();
//        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }
    private void animationY(){
        if(initCalendarHeight!=calendarView.getY()||initRecyclerHeight!=recyclerView.getY()) {
            if (recyclerView.getLayoutManager() == null || !(recyclerView.getLayoutManager() instanceof LinearLayoutManager))
                return;
            int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            int calendarDistance = (int) (calendarView.getInitY() - calendarView.getY());
            int recycleDistance = (int) (calendarView.getY() + calendarView.getMaxHeight() - recyclerView.getY());
            if (pos == 0) {
                Log.i("ssssssssssssss", "calendar distance:" + calendarDistance);
                Log.i("ssssssssssssss", "recycler distance:" + recycleDistance);
                Log.i("ssssssssssssss", "upper height:" + calendarView.getUpperHeight());
                Log.i("ssssssssssssss", "under height:" + calendarView.getUnderHeight());
                if (calendarDistance + recycleDistance > calendarView.getItemHeight() * 2) {
                    if (calendarDistance < (int) calendarView.getUpperHeight())
                        animationCalendar(finalCalendarUpperY - calendarView.getY(), true);
                    else {
                        animationRecycler(finalRecyclerUpperY - recyclerView.getY(), false);
                    }
                } else {
                    if (recycleDistance < (int) calendarView.getUnderHeight()) {
                        Log.i("ssssssssssssssss", "aaaaa");
                        if(recycleDistance<(int)calendarView.getUnderHeight()) {
                            animationRecycler(recycleDistance, true);
                        }else{

                        }
                    }
                }
            }
        }
    }
    private void animationCalendar(float y,final boolean mContinue){
        calendarView.animate().translationYBy(y).setDuration(y>0?(int)y:-(int)y).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(mContinue) animationRecycler(-calendarView.getUnderHeight(),false);
                else scrolling=false;
            }
        });
    }
    private void animationRecycler(float y, final boolean mContinue){
        recyclerView.animate().translationYBy(y).setDuration(y>0?(int)y:-(int)y).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(mContinue) animationCalendar(calendarView.getUpperHeight(),false);
                else scrolling=false;
            }
        });
    }
}
