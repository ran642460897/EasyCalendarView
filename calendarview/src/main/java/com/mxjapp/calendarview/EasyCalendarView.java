package com.mxjapp.calendarview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;


import com.mxjapp.calendarview.adapter.CalendarViewPageAdapter;
import com.mxjapp.calendarview.entity.StyleAttr;
import com.mxjapp.calendarview.helper.ScrollHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * user: Jason Ran
 * date: 2018/7/25.
 */
public class EasyCalendarView extends ViewPager{
    private OnDateChangedListener onDateChangedListener;
//    private ViewPager viewPager;
    private List<CalendarPage> calendarPages;
    private int curPosition;
    private float itemWidth;
    private float initY;
    private boolean initPosition=false;
    private boolean scrollableY=true;
    private boolean scrollableX=true;
    private int currentLine=0;
    private int upperHeight,underHeight,itemHeight,maxHeight,minHeight,verticalSpace,viewHeight;
    private View otherView;
    private boolean frozen=false;
    private ScrollHelper scrollHelper=new ScrollHelper();
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
            public void onClickCurrent(Calendar calendar,int mark) {
                calendarPages.get((curPosition-1)%3).setSelected(calendar);
                calendarPages.get((curPosition+1)%3).setSelected(calendar);
                calculateLine();
                if(onDateChangedListener!=null) onDateChangedListener.onDateChanged(calendar,mark);
            }

            @Override
            public void onClickPrevious(Calendar calendar,int mark) {
                calendarPages.get((curPosition-1)%3).setSelected(calendar);
                setCurrentItem(curPosition-1,true);
            }

            @Override
            public void onClickNext(Calendar calendar,int mark) {
                calendarPages.get((curPosition+1)%3).setSelected(calendar);
                setCurrentItem(curPosition+1,true);
            }
        };
        CalendarPage.OnScrollYListener onScrollYListener=new CalendarPage.OnScrollYListener() {
            @Override
            public void onPreScroll(CalendarPage view) {
                otherView=getOtherView();
                scrollHelper.init(EasyCalendarView.this,otherView);
            }

            @Override
            public void onScroll(CalendarPage v,float y,int act) {
                if(act==CalendarPage.ACT_SCROLL_MONTH_TO_WEEK) {
                    scrollableX=false;
                    scrollHelper.scroll(y);
                }else if(act==CalendarPage.ACT_SCROLL_WEEK_TO_MONTH){
                    scrollHelper.init(EasyCalendarView.this,otherView);
                }
            }

            @Override
            public void onStopScroll(CalendarPage view,int act) {
                if(act==CalendarPage.ACT_SCROLL_MONTH_TO_WEEK)
                    scrollHelper.translateY(false);
                else if(act==CalendarPage.ACT_SCROLL_WEEK_TO_MONTH){
                    scrollHelper.translateY(true);
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
        setStyleAttr(new StyleAttr());
        CalendarViewPageAdapter pagerAdapter=new CalendarViewPageAdapter(calendarPages);
        setAdapter(pagerAdapter);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(curPosition==0) return;
                if(curPosition!=position) {
                    if (position - curPosition > 0) calendarPages.get((position - 2) % 3).add(3);
                    else if (position - curPosition < 0) calendarPages.get((position + 2) % 3).add(-3);

                    curPosition = position;
                    Calendar calendar = calendarPages.get(curPosition % 3).getSelectedCalendar();
                    calendarPages.get((curPosition + 1) % 3).setSelected(calendar);
                    calendarPages.get((curPosition - 1) % 3).setSelected(calendar);
                    calculateLine();
                    if(onDateChangedListener!=null) onDateChangedListener.onDateChanged(calendar,calendarPages.get(curPosition % 3).getSelectedMark());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                frozen=false;
            }
        });
        setCurrentItem(1000,false);
        curPosition=getCurrentItem();

    }
    private void calculateLine(){
        currentLine=calendarPages.get(curPosition%3).getLine();
        upperHeight=currentLine*(itemHeight+verticalSpace);
        underHeight=(5-currentLine)*(itemHeight+verticalSpace);
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
    public void switchToWeek(){
        setTranslationY(initY);
        CalendarPage page=calendarPages.get(curPosition%3);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(page.getSelectedCalendar().getTimeInMillis());
        page.switchToWeek(calendar); //更新当前页面

        calendar.add(Calendar.DAY_OF_MONTH,-7);//更新前一个页面
        calendarPages.get((curPosition-1)%3).switchToWeek(calendar);

        calendar.add(Calendar.DAY_OF_MONTH,14);//更新后一个页面
        calendarPages.get((curPosition+1)%3).switchToWeek(calendar);
    }
    public void switchCurrentPageToMonth(){
        frozen=true;
        setTranslationY(initY-upperHeight);
        CalendarPage page=calendarPages.get(curPosition%3);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(page.getSelectedCalendar().getTimeInMillis());
        page.switchToMonth(calendar);
    }
    public void switchOtherPagesToMonth(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(calendarPages.get(curPosition%3).getSelectedCalendar().getTimeInMillis());

        calendar.add(Calendar.MONTH,-1);
        calendarPages.get((curPosition-1)%3).switchToMonth(calendar);

        calendar.add(Calendar.MONTH,2);
        calendarPages.get((curPosition+1)%3).switchToMonth(calendar);
    }
    public void switchToMonth(){
        setTranslationY(initY-upperHeight);
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
        return scrollableX&&scrollableY&&super.onInterceptTouchEvent(ev);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight=calendarPages.get(curPosition%3).getMeasuredHeight();
        itemHeight=calendarPages.get(curPosition%3).getItemHeight();
        itemWidth=calendarPages.get(curPosition%3).getItemWidth();
        verticalSpace=calendarPages.get(curPosition%3).getVerticalSpace();
        minHeight=itemHeight;
        maxHeight=itemHeight*6+verticalSpace*5;
        calculateLine();
        setMeasuredDimension(getMeasuredWidth(),viewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(!initPosition) {
            initY = getY();
            initPosition=true;
        }
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public void setInitType(int type){
        if(type==CalendarPage.TYPE_WEEK) switchToWeek();
    }

    public void setDate(Calendar calendar){
        for(int i=0;i<calendarPages.size();i++){
            calendarPages.get(i).setSelectedCalendar(calendar);
        }
        calendarPages.get(curPosition%3).notifyChanged();
        calendarPages.get((curPosition-1)%3).add(-1);
        calendarPages.get((curPosition+1)%3).add(1);
    }
    public Calendar getDate(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(calendarPages.get(curPosition%3).getSelectedCalendar().getTimeInMillis());
        return calendar;
    }
    public void addMarks(Map<String,Integer> map){
        for(int i=0;i<calendarPages.size();i++){
            calendarPages.get(i).addMarks(map);
        }
    }
    public void setStyleAttr(StyleAttr attr){
        for(int i=0;i<calendarPages.size();i++){
            calendarPages.get(i).setAttr(attr);
        }
    }
    public int getItemHeight() {
        return itemHeight;
    }

    public float getItemWidth() {
        return itemWidth;
    }

    public float getInitY() {
        return initY;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public float getVerticalSpace() {
        return verticalSpace;
    }

    public int getCurrentLine() {
        return currentLine;
    }



    public float getUpperHeight() {
        return upperHeight;
    }

    public float getUnderHeight() {
        return underHeight;
    }
    public int getType(){
        return calendarPages.get(curPosition%3).getType();
    }

    public void setScrollableX(boolean scrollableX) {
        this.scrollableX = scrollableX;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.onDateChangedListener = onDateChangedListener;
    }

    public interface OnDateChangedListener{
        void onDateChanged(Calendar calendar,int mark);
    }

    public ScrollHelper getScrollHelper() {
        return scrollHelper;
    }
}
