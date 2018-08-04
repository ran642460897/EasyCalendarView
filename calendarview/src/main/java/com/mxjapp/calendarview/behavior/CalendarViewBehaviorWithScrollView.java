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

import com.mxjapp.calendarview.CalendarPage;
import com.mxjapp.calendarview.EasyCalendarView;

public class CalendarViewBehaviorWithScrollView extends CoordinatorLayout.Behavior<EasyCalendarView> {
    public static final int SCROLL_TYPE_NONE=0;
    public static final int SCROLL_TYPE_TO_TOP=1;
    public static final int SCROLL_TYPE_TO_BOTTOM=2;
    private EasyCalendarView calendarView;
    private NestedScrollView targetView;
    private int finalCalendarDownY,finalTargetDownY,finalCalendarUpY,finalTargetUpY;
    int initTargetHeight=0,initCalendarHeight=0;
    public CalendarViewBehaviorWithScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean prepared=false;
    private boolean scrollable=true;
    private int firstScrollType=SCROLL_TYPE_NONE;

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View directTargetTarget, @NonNull View target, int axes, int type) {
        Log.i("sssssssssssssssss","onStartNestedScroll");
        if(axes == ViewCompat.SCROLL_AXIS_VERTICAL&&scrollable){
            Log.i("ssssssssssssssssss","start start");
            if(!prepared&&firstScrollType==SCROLL_TYPE_NONE) {
                
                if (calendarView == null) calendarView = child;
                if (targetView == null && target instanceof NestedScrollView) targetView = (NestedScrollView) target;
                if(calendarView.getType()== CalendarPage.TYPE_MONTH){
                    firstScrollType = SCROLL_TYPE_TO_TOP;
                }else{
                    firstScrollType = SCROLL_TYPE_TO_BOTTOM;
                    calendarView.switchCurrentPageToMonth();
//                    targetView.setTranslationY(child.getInitY()+child.getItemHeight());
                }

                finalCalendarDownY = (int) child.getInitY(); //下滑calendar最终位置
                finalTargetDownY = (int) (child.getInitY() + child.getMaxHeight());//下滑target最终位置
                finalCalendarUpY = (int) (child.getInitY() - child.getUpperHeight());//上滑calendar最终位置
                finalTargetUpY = (int) (child.getInitY() + child.getItemHeight());//上滑target最终位置
                initCalendarHeight = (int) calendarView.getY();//calendar初始位置
                initTargetHeight = (int) targetView.getY();//child初始位置

                int calendarY = (int) calendarView.getY();
                int childY = (int) targetView.getY();
            Log.i("ssssssssssss","calendar y:"+calendarY);
            Log.i("ssssssssssss","child y:"+childY);
            Log.i("ssssssssssss","calendar up y:"+finalCalendarUpY);
            Log.i("ssssssssssss","child up y:"+finalTargetUpY);
            Log.i("ssssssssssss","calendar down y:"+finalCalendarDownY);
            Log.i("ssssssssssss","child down y:"+finalTargetDownY);
                prepared=true;
                return false;
            }else{
                prepared=false;
                return true;
            }
        }else return false;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
            if(canScroll()){
                if(dy<0) {//下拉
                    int targetOffsetY=(int) (targetView.getY()-calendarView.getY()-calendarView.getUpperHeight()-calendarView.getItemHeight());
                    int targetAllOffsetY=(int)calendarView.getUnderHeight();

                    if(targetOffsetY<targetAllOffsetY){
                        if(targetOffsetY+ dy<targetAllOffsetY){
                            targetView.setTranslationY(targetView.getTranslationY()-dy);
                        }else{
                            targetView.setTranslationY(targetView.getTranslationY()-targetOffsetY+targetAllOffsetY);
                        }
                        consumed[1] = dy;
                    }
                    else if(calendarView.getY()<finalCalendarDownY){
                        if(calendarView.getY()-dy>finalCalendarDownY){
                            calendarView.setTranslationY(finalCalendarDownY);
                        }else{
                            calendarView.setTranslationY(calendarView.getTranslationY()-dy);
                        }
                        consumed[1] = dy;
                    }
                }else if(dy>0 ){//上滑
                    if(calendarView.getY()>finalCalendarUpY) {
                        if(calendarView.getY()-dy<finalCalendarUpY) {
                            calendarView.setTranslationY(finalCalendarUpY);
                        }else{
                            calendarView.setTranslationY(calendarView.getTranslationY()-dy);
                        }
                        consumed[1] = dy;
                    }else if(targetView.getY()>finalTargetUpY){
                        if(targetView.getY()-dy<finalTargetUpY){
                            targetView.setTranslationY(finalTargetUpY);
                        }else {
                            targetView.setTranslationY(targetView.getTranslationY()-dy);
                        }
                        consumed[1] = dy;
                    }
                }
            }

    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, int type) {
        Log.i("sssssssssssssssss","bbb onStopNestedScroll");
        translateY();
//        super.onStopNestedScroll(coordinatorLayout, child, target, type);
    }
    private boolean canScroll(){
        Log.i("ssssssssssssssssssssss","scroll view:"+targetView.getScrollY());
        Log.i("ssssssssssssssssssssss","scroll type:"+firstScrollType);
        if(targetView.getScrollY()==0&&(firstScrollType==SCROLL_TYPE_TO_TOP||firstScrollType==SCROLL_TYPE_TO_BOTTOM)) {
            return true;
        }
        else return false;
    }
    private void translateY(){
            if (canScroll()) {
                scrollable=false;//不可滑动
                int calendarDistance=0,targetDistance=0;
                switch (firstScrollType){
                    case SCROLL_TYPE_TO_TOP:
                        calendarDistance=finalCalendarDownY-(int) calendarView.getY();
                        targetDistance=finalTargetDownY-(int)targetView.getY();
                        if(calendarDistance+targetDistance>calendarView.getItemHeight()) translateToTop();
                        else translateToBottom();
                        break;
                    case SCROLL_TYPE_TO_BOTTOM:
                        calendarDistance=(int) calendarView.getY()-finalCalendarUpY;
                        targetDistance=(int)targetView.getY()-finalTargetUpY;
                        if(calendarDistance+targetDistance>calendarView.getItemHeight()) translateToBottom();
                        else translateToTop();
                        break;
                }
//                Log.i("ssssssssssssss", "calendar distance:" + calendarDistance);
//                Log.i("ssssssssssssss", "child distance:" + targetDistance);
//                Log.i("ssssssssssssss", "upper height:" + calendarView.getUpperHeight());
//                Log.i("ssssssssssssss", "under height:" + calendarView.getUnderHeight());
            }
    }
    private void translateToTop(){
        Log.i("ssssssssssssss","translate top");
        if((int)calendarView.getY()>finalCalendarUpY){
            translateCalendar(finalCalendarUpY-calendarView.getY(),true);
        }else{
            translateTarget(finalTargetUpY-targetView.getY(),false);
        }
    }
    private void translateToBottom(){
        Log.i("ssssssssssssss","translate bottom");
        int targetOffsetY=(int) (targetView.getY()-calendarView.getY()-calendarView.getUpperHeight()-calendarView.getItemHeight());
        int targetAllOffsetY=(int)calendarView.getUnderHeight();
        if(targetOffsetY<targetAllOffsetY){
            translateTarget(targetAllOffsetY-targetOffsetY,true);
        }else{
            translateCalendar(finalCalendarDownY-calendarView.getY(),false);
        }
    }
    private void translateCalendar(float y,final boolean mContinue){
        calendarView.animate().translationYBy(y).setDuration(y>0?(int)y:-(int)y).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(mContinue) translateTarget(finalTargetUpY-targetView.getY(),false);//向上
                else{
                    endTranslate(SCROLL_TYPE_TO_BOTTOM);
                }
            }
        });
    }
    private void translateTarget(float y, final boolean mContinue){
        targetView.animate().translationYBy(y).setDuration(y>0?(int)y:-(int)y).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(mContinue) translateCalendar(finalCalendarDownY-calendarView.getY(),false);//向下
                else {
                    endTranslate(SCROLL_TYPE_TO_TOP);
                }
            }
        });
    }

    private void endTranslate(int endScrollType){
        switch (endScrollType){
            case SCROLL_TYPE_TO_TOP:
//                calendarView.setY(finalCalendarUpY);
//                targetView.setY(finalTargetUpY);
                calendarView.switchToWeek();
                break;
            case SCROLL_TYPE_TO_BOTTOM:
                calendarView.switchOtherPagesToMonth();
                break;
        }
        firstScrollType=SCROLL_TYPE_NONE;
        scrollable=true;
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, float velocityX, float velocityY) {
        Log.i("ssssssssssssssss","onNestedPreFling");
//        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
        return true;
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull EasyCalendarView child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.i("ssssssssssssssss","onNestedFling");
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }
}
