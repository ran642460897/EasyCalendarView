package com.mxjapp.calendarview.helper;

import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.mxjapp.calendarview.CalendarPage;
import com.mxjapp.calendarview.EasyCalendarView;

/**
 * user: Jason Ran
 * date: 2018/8/4.
 */
public class ScrollHelper {
    private static final int SCROLL_TYPE_NONE=0;
    private static final int SCROLL_TYPE_TO_TOP=1;
    private static final int SCROLL_TYPE_TO_BOTTOM=2;
    private EasyCalendarView calendarView;
    private NestedScrollView targetView;
    private int finalCalendarDownY,finalTargetDownY,finalCalendarUpY,finalTargetUpY;
//    private int initTargetHeight=0,initCalendarHeight=0;
    private boolean prepared=false;
    private boolean scrollable=true;
    private int firstScrollType=SCROLL_TYPE_NONE;

    public void init(EasyCalendarView child, View target){
            if (calendarView == null) calendarView = child;
            if (targetView == null && target instanceof NestedScrollView)
                targetView = (NestedScrollView) target;
            if (calendarView.getType() == CalendarPage.TYPE_MONTH) {
                firstScrollType = SCROLL_TYPE_TO_TOP;
            } else {
                firstScrollType = SCROLL_TYPE_TO_BOTTOM;
                calendarView.switchCurrentPageToMonth();
            }

            finalCalendarDownY = (int) child.getInitY(); //下滑calendar最终位置
            finalTargetDownY = (int) (child.getInitY() + child.getMaxHeight());//下滑target最终位置
            finalCalendarUpY = (int) (child.getInitY() - child.getUpperHeight());//上滑calendar最终位置
            finalTargetUpY = (int) (child.getInitY() + child.getItemHeight());//上滑target最终位置
//            initCalendarHeight = (int) calendarView.getY();//calendar初始位置
//            initTargetHeight = (int) targetView.getY();//child初始位置
    }
    public boolean scroll(float dy){
        boolean consumed=false;
        if(dy<0) {//下拉
            int targetOffsetY=(int) (targetView.getY()-calendarView.getY()-calendarView.getUpperHeight()-calendarView.getItemHeight());
            int targetAllOffsetY=(int)calendarView.getUnderHeight();

            if(targetOffsetY<targetAllOffsetY){
                if(targetOffsetY+ dy<targetAllOffsetY){
                    targetView.setTranslationY(targetView.getTranslationY()-dy);
                }else{
                    targetView.setTranslationY(targetView.getTranslationY()-targetOffsetY+targetAllOffsetY);
                }
                consumed=true;
            }
            else if(calendarView.getY()<finalCalendarDownY){
                if(calendarView.getY()-dy>finalCalendarDownY){
                    calendarView.setTranslationY(finalCalendarDownY);
                }else{
                    calendarView.setTranslationY(calendarView.getTranslationY()-dy);
                }
                consumed=true;
            }
        }else if(dy>0 ){//上滑
            if(calendarView.getY()>finalCalendarUpY) {
                if(calendarView.getY()-dy<finalCalendarUpY) {
                    calendarView.setTranslationY(finalCalendarUpY);
                }else{
                    calendarView.setTranslationY(calendarView.getTranslationY()-dy);
                }
                consumed=true;
            }else if(targetView.getY()>finalTargetUpY){
                if(targetView.getY()-dy<finalTargetUpY){
                    targetView.setTranslationY(finalTargetUpY);
                }else {
                    targetView.setTranslationY(targetView.getTranslationY()-dy);
                }
                consumed=true;
            }
        }
        return consumed;
    }

    public void translateY(boolean forceToBottom){
        if(forceToBottom){
            scrollable=false;
            translateToBottom();
        }
        else if (targetView.getScrollY()==0) {
            scrollable=false;//不可滑动
            int calendarDistance,targetDistance;
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
        }else {
            translateToTop();
        }
    }
    private void translateToTop(){
        if((int)calendarView.getY()>finalCalendarUpY){
            translateCalendar(finalCalendarUpY-calendarView.getY(),true);
        }else{
            translateTarget(finalTargetUpY-targetView.getY(),false);
        }
    }
    private void translateToBottom(){
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
                calendarView.switchToWeek();
                break;
            case SCROLL_TYPE_TO_BOTTOM:
                calendarView.switchOtherPagesToMonth();
                break;
        }
        firstScrollType=SCROLL_TYPE_NONE;
        scrollable=true;
        calendarView.setScrollableX(true);
    }
    public boolean dispatchScroll(){
        return targetView.getScrollY()==0&&(firstScrollType==SCROLL_TYPE_TO_TOP||firstScrollType==SCROLL_TYPE_TO_BOTTOM);
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public boolean isPrepared() {
        return prepared;
    }


    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }

}
