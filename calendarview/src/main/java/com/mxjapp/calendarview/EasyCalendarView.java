package com.mxjapp.calendarview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.mxjapp.calendarview.adapter.CalendarViewPageAdapter;
import com.mxjapp.calendarview.entity.CalendarHint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * user: Jason Ran
 * date: 2018/7/25.
 */
public class EasyCalendarView extends ViewPager{
    private OnDateChangedListener onDateChangedListener;
//    private ViewPager viewPager;
    private List<CalendarPage> calendarPages;
    private int curPosition;
    private float offsetY=0;
    private float viewWidth,viewHeight;
    private float itemHeight,itemWidth;
    private float verticalSpace,horizontalSpace;
    private float initX,initY;
    private boolean initPosition=false;
    private boolean scrollable=false;
    private boolean slideX=true;
    private float viewMaxScrollY=0;
    private int currentLine=0;
    private View otherView;
    public EasyCalendarView(@NonNull Context context) {
        this(context,null);
    }

    public EasyCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    private void initView(){


        calendarPages=new ArrayList<>();

        CalendarPage calendarPage1=new CalendarPage(getContext());
        calendarPage1.add(-1);

        CalendarPage calendarPage2=new CalendarPage(getContext());

        CalendarPage calendarPage3=new CalendarPage(getContext());
        calendarPage3.add(1);

        CalendarPage.OnItemClickListener itemClickListener=new CalendarPage.OnItemClickListener() {
            @Override
            public void onClickCurrent(Calendar calendar) {
                calendarPages.get((curPosition-1)%3).setSelected(calendar);
                calendarPages.get((curPosition+1)%3).setSelected(calendar);
                if(onDateChangedListener!=null) onDateChangedListener.onDateChanged(calendar);
            }

            @Override
            public void onClickPrevious(Calendar calendar) {
                calendarPages.get((curPosition-1)%3).setSelected(calendar);
                setCurrentItem(curPosition-1,true);
                if(onDateChangedListener!=null) onDateChangedListener.onDateChanged(calendar);
            }

            @Override
            public void onClickNext(Calendar calendar) {
                calendarPages.get((curPosition+1)%3).setSelected(calendar);
                setCurrentItem(curPosition+1,true);
                if(onDateChangedListener!=null) onDateChangedListener.onDateChanged(calendar);
            }
        };
        CalendarPage.OnScrollYListener onScrollYListener=new CalendarPage.OnScrollYListener() {
            @Override
            public void onPreScroll(CalendarPage view) {
                currentLine=view.getLine();
                viewMaxScrollY=-(currentLine*(itemHeight+verticalSpace))+initY;
                otherView=getOtherView();
            }

            @Override
            public void onScroll(CalendarPage v,float y,int act) {
                if(act==CalendarPage.ACT_SCROLL_MONTH_TO_WEEK) {
                    if (getY() + y < 0) {
                        slideMonthToWeek(y);
                    }
                }else if(act==CalendarPage.ACT_SCROLL_WEEK_TO_MONTH){
                    if(getY()+y>5){
                        slideX=false;
                    }
                }
            }

            @Override
            public void onStopScroll(CalendarPage view,int act) {
                if(!slideX) {
                    if(act==CalendarPage.ACT_SCROLL_MONTH_TO_WEEK)
                        shrinkView();
                    else if(act==CalendarPage.ACT_SCROLL_WEEK_TO_MONTH)
                        expandView();
                }
            }
        };

        calendarPage1.setOnItemClickListener(itemClickListener);
        calendarPage2.setOnItemClickListener(itemClickListener);
        calendarPage3.setOnItemClickListener(itemClickListener);
        calendarPage1.setOnScrollYListener(onScrollYListener);
        calendarPage2.setOnScrollYListener(onScrollYListener);
        calendarPage3.setOnScrollYListener(onScrollYListener);

        calendarPages.add(calendarPage1);
        calendarPages.add(calendarPage2);
        calendarPages.add(calendarPage3);
        CalendarViewPageAdapter pagerAdapter=new CalendarViewPageAdapter(calendarPages);
        setAdapter(pagerAdapter);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(curPosition==0) return;
//
                if(position-curPosition>0) calendarPages.get((position-2)%3).add(3);
                else if(position-curPosition<0) calendarPages.get((position+2)%3).add(-3);

                curPosition=position;
                Calendar calendar=calendarPages.get(curPosition%3).getSelectedCalendar();
                calendarPages.get((curPosition+1)%3).setSelected(calendar);
                calendarPages.get((curPosition-1)%3).setSelected(calendar);
                Log.i("ssssssssssssss",calendarPages.get(curPosition%3).getSelectedCalendar().get(Calendar.MONTH)+1+"月");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setCurrentItem(1000,false);
        curPosition=getCurrentItem();

    }
    private void slideMonthToWeek(float y){
        slideX = false;
        if(getY()+y>viewMaxScrollY) {
            setY(getY() + y);
            offsetY = offsetY + y;
            Log.i("sssssssssssssssss","other view y"+getY());
            Log.i("sssssssssssssssss","other view yy"+y);
        } else{
            setY(viewMaxScrollY);
            offsetY=viewMaxScrollY-initY;
//            if(otherView!=null) {
//                otherView.offsetTopAndBottom((int) y);
                Log.i("sssssssssssssssss","other view y"+otherView.getY());
                Log.i("sssssssssssssssss","other view yy"+y);
//            }
//            offsetY=offsetY+y;
        }
    }
    private void expandView(){
        switchToMonth();
        float offset=(currentLine)*(itemHeight+verticalSpace);
        int time=(int)offset;
        setY(-offset+initY);
        this.animate().translationYBy(offset).setDuration(time).withEndAction(new Runnable() {
            @Override
            public void run() {
                slideX=true;
                offsetY=0;
            }
        });
    }

    private void shrinkView(){
        int line=6-currentLine;
        float distance=((line-1)*verticalSpace+(line)*itemHeight-viewHeight)-offsetY;
        int time=distance>0?(int)distance:-(int)distance;
        this.animate().translationYBy(distance).setDuration(time).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(currentLine<5) {
                    shrinkOtherView();
                }
                else {
                    offsetY = 0;
                    slideX = true;
                    switchToWeek();
                }
//                if(Build.VERSION.SDK_INT>=21)
//                stopNestedScroll();
            }
        });
    }
    private void shrinkOtherView(){

            float height=(5-currentLine)*(verticalSpace+itemHeight);
            int time=(int) height;
            otherView.animate().translationYBy(-height).setDuration(time).withEndAction(new Runnable() {
                @Override
                public void run() {
                    offsetY=0;
                    slideX=true;
                    switchToWeek();
//                    if(Build.VERSION.SDK_INT>=21)
//                    stopNestedScroll();
                }
            });

    }
    private View getOtherView(){
        ViewGroup viewGroup=(ViewGroup)getParent();
        if(viewGroup!=null) {
            for(int i=0;i<viewGroup.getChildCount();i++) {
                if(viewGroup.getChildAt(i) instanceof EasyCalendarView&&viewGroup.getChildCount()>i+1)
                return viewGroup.getChildAt(i+1);
            }
        }
        return null;
    }
    private void switchToWeek(){
        setY(initY);
        CalendarPage page=calendarPages.get(curPosition%3);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(page.getSelectedCalendar().getTimeInMillis());
        page.switchToWeek(calendar); //更新当前页面

        calendar.add(Calendar.DAY_OF_MONTH,-7);//更新前一个页面
        calendarPages.get((curPosition-1)%3).switchToWeek(calendar);

        calendar.add(Calendar.DAY_OF_MONTH,14);//更新后一个页面
        calendarPages.get((curPosition+1)%3).switchToWeek(calendar);
    }
    private void switchToMonth(){
        CalendarPage page=calendarPages.get(curPosition%3);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(page.getSelectedCalendar().getTimeInMillis());
        page.switchToMonth(calendar);

        calendar.add(Calendar.MONTH,-1);
        calendarPages.get((curPosition-1)%3).switchToMonth(calendar);

        calendar.add(Calendar.MONTH,2);
        calendarPages.get((curPosition+1)%3).switchToMonth(calendar);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return slideX&&super.onInterceptTouchEvent(ev);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth=getMeasuredWidth();
        viewHeight=calendarPages.get(curPosition%3).getMeasuredHeight();
        itemHeight=calendarPages.get(curPosition%3).getItemHeight();
        itemWidth=calendarPages.get(curPosition%3).getItemWidth();
        horizontalSpace=calendarPages.get(curPosition%3).getHorizontalSpace();
        verticalSpace=calendarPages.get(curPosition%3).getVerticalSpace();
        setMeasuredDimension((int)viewWidth,(int) viewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(!initPosition) {
            initX = getX();
            initY = getY();
            initPosition=true;
        }
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public float getViewHeight() {
        return viewHeight;
    }


    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public void setHints(List<CalendarHint> hints){
        calendarPages.get(curPosition%3).setHints(hints);
    }
    public float getItemHeight() {
        return itemHeight;
    }

    public float getItemWidth() {
        return itemWidth;
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.onDateChangedListener = onDateChangedListener;
    }

    public interface OnDateChangedListener{
        void onDateChanged(Calendar calendar);
    }
}
