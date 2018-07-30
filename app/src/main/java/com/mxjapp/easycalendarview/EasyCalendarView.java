package com.mxjapp.easycalendarview;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;


import com.mxjapp.easycalendarview.adapter.CalendarViewPageAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * user: Jason Ran
 * date: 2018/7/25.
 */
public class EasyCalendarView extends ViewPager{
    private OnItemClickListener onItemClickListener;
//    private ViewPager viewPager;
    private List<CalendarPage> calendarPages;
    private int curPosition;
    private float offsetY=0;
    private float viewWidth,viewHeight;
    private float itemHeight,itemWidth;
    private float verticalSpace,horizontalSpace;
    private float initX,initY;
    private boolean scrollable=false;
    private int pageScrollState = ViewPager.SCROLL_STATE_IDLE;
    private boolean slideX=true;
    private int time=0;
    private float remainderHeight=0;
    public EasyCalendarView(@NonNull Context context) {
        this(context,null);
    }

    public EasyCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

//    public EasyCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initView();
//    }
    private void initView(){


//        viewPager=new ViewPager(getContext());
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
                if(onItemClickListener!=null) onItemClickListener.onClick(calendar);
            }

            @Override
            public void onClickPrevious(Calendar calendar) {
                calendarPages.get((curPosition-1)%3).setSelected(calendar);
                setCurrentItem(curPosition-1,true);
                if(onItemClickListener!=null) onItemClickListener.onClick(calendar);
            }

            @Override
            public void onClickNext(Calendar calendar) {
                calendarPages.get((curPosition+1)%3).setSelected(calendar);
                setCurrentItem(curPosition+1,true);
                if(onItemClickListener!=null) onItemClickListener.onClick(calendar);
            }
        };
        CalendarPage.OnScrollYListener onScrollYListener=new CalendarPage.OnScrollYListener() {
            @Override
            public void onScroll(View v,float y) {
//                offsetTopAndBottom((int)y);
                slideX=false;
                setY(getY()+y);
                offsetY=offsetY+y;
                Log.i("ssssssssssssss","view y:"+getY());
            }

            @Override
            public void onStop(View view) {
                animationY();
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
                Log.i("ssssssssssssss",calendarPages.get(curPosition%3).getMonth()+"月");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        addView(viewPager);
        setCurrentItem(1000,false);
//        viewPager.setOffscreenPageLimit(3);
        curPosition=getCurrentItem();
//        viewPager.setOffscreenPageLimit(3);
        Log.i("ssssssssssssss","easy view init");

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("sssssssssssss","slide x:"+slideX);
        if(slideX)
        return super.onInterceptTouchEvent(ev);
        else return false;
    }

    private void animationY(){
        final int line=6-calendarPages.get(getCurrentItem()%3).getLine();
        Log.i("sssssssssssssssss","line :"+line);
        float distance=((line-1)*verticalSpace+(line)*itemHeight-viewHeight)-offsetY;
        this.animate().translationYBy(distance).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(line>1) {
                    remainderHeight=(line-1)*verticalSpace+(line-1)*itemHeight;
                    animationOtherView();
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
    private void animationOtherView(){
        ViewGroup viewGroup=(ViewGroup) getParent();
        if(viewGroup!=null&&viewGroup.getChildCount()>1){
            View view=viewGroup.getChildAt(1);
            view.animate().translationYBy(-remainderHeight).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    offsetY=0;
                    slideX=true;
                    remainderHeight=0;
                    switchToWeek();
                }
            });
        }
    }

    private void switchToWeek(){
        setY(0);
        CalendarPage page=calendarPages.get(curPosition%3);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(page.getSelectedCalendar().getTimeInMillis());
        page.switchToWeek(calendar); //更新当前页面

        calendar.add(Calendar.DAY_OF_MONTH,-7);//更新前一个页面
        calendarPages.get((curPosition-1)%3).switchToWeek(calendar);

        calendar.add(Calendar.DAY_OF_MONTH,14);//更新后一个页面
        calendarPages.get((curPosition+1)%3).switchToWeek(calendar);
//        for(CalendarPage page:calendarPages){
//            page.switchToWeek();
//        }
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
        initX=getX();
        initY=getY();
        setMeasuredDimension((int)viewWidth,(int) viewHeight);
        Log.i("sssssssssssss","outward x:"+getX());
        Log.i("sssssssssssss","outward y:"+getY());
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

    public int getPageScrollState() {
        return pageScrollState;
    }

    public float getItemHeight() {
        return itemHeight;
    }

    public float getItemWidth() {
        return itemWidth;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(Calendar calendar);
    }
}
