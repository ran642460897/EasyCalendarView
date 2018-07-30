package com.mxjapp.easycalendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;


import java.util.Calendar;

/**
 * user: Jason Ran
 * date: 2018/7/24.
 */
public class CalendarPage extends View implements NestedScrollingChild{
    private int type=TYPE_MONTH;
    private final static int TYPE_MONTH=0;
    private final static int TYPE_WEEK=1;
    private Paint p;
    private Paint.FontMetricsInt fm;
    private float width,maxHeight;
    private float horizontalSpace,verticalSpace;
    private float offsetY=0;
    private int textColor=Color.parseColor("#333333");
    private int textDimColor=Color.parseColor("#999999");
    private int backgroundSelectedColor=Color.parseColor("#FFEEE1");
    private int textSelectedColor=Color.parseColor("#FF6B00");
    private int textSize=12;//dp
    private float itemWidth,itemHeight;
    private RectF selectedRectF;
    private OnItemClickListener onItemClickListener;
    private OnScrollYListener onScrollYListener;
    private Calendar selectedCalendar;
    private Calendar calendar;
    private int currentMonth; //月模式使用
    private int currentLine;//周模式使用
    public CalendarPage(Context context) {
        this(context,null);
    }

    public CalendarPage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CalendarPage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViewData();
        initCalendarData();
    }

    private void initViewData(){
        horizontalSpace= UnitsUtil.dip2px(getContext(),6);
        verticalSpace= UnitsUtil.dip2px(getContext(),6);
        p=new Paint();
        p.setTextSize(UnitsUtil.dip2px(getContext(),textSize));
        p.setAntiAlias(true);
        fm=p.getFontMetricsInt();
        selectedRectF=new RectF(0,0,0,0);
    }
    private void initCalendarData(){
        if(selectedCalendar==null) selectedCalendar=Calendar.getInstance();
        if(calendar==null) calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedCalendar.getTimeInMillis());
        switch (type){
            case TYPE_MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为月第一天
                currentMonth = calendar.get(Calendar.MONTH);
                calendar.add(Calendar.DAY_OF_MONTH, 1 - calendar.get(Calendar.DAY_OF_WEEK));//如果第一天不为周日，移动到周日
                break;
            case TYPE_WEEK:
                calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                break;
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("ssssssssss","绘制");
        int maxLine=0;
        switch (type){
            case TYPE_MONTH:
                maxLine=6;
                break;
            case TYPE_WEEK:
                maxLine=1;
                break;
        }
        for(int i=0;i<maxLine;i++) {
            canvas.save();
            for (int j = 0; j < 7; j++) {

                    if (calendar.get(Calendar.MONTH) != currentMonth&&type==TYPE_MONTH) p.setColor(textDimColor);//修改非本月颜色
                    else p.setColor(textColor);

                if(calendar.getTimeInMillis()/24/3600==selectedCalendar.getTimeInMillis()/24/3600) {
                    p.setColor(backgroundSelectedColor);
                    selectedRectF.right=itemWidth;
                    selectedRectF.bottom=itemHeight;
                    canvas.drawRoundRect(selectedRectF,20,20,p);//画选中背景
                    p.setColor(textSelectedColor);//修改选中字体颜色
                }
                String s=calendar.get(Calendar.DAY_OF_MONTH)+"";
                canvas.drawText(s,(itemWidth-p.measureText(s))/2,(itemHeight+fm.bottom-fm.top)/2-fm.descent,p);


                canvas.translate(horizontalSpace + itemWidth, 0);
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }
            canvas.restore();
            canvas.translate(0,verticalSpace+itemHeight);
        }
        calendar.add(Calendar.DAY_OF_MONTH,-7*maxLine);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width=getMeasuredWidth();
        itemWidth=(width-6*horizontalSpace)/7;
        itemHeight=(width-6*horizontalSpace)/7;
//        itemHeight=(height-5*verticalSpace)/6;
        maxHeight=itemHeight*6+verticalSpace*5;
        float height=type==TYPE_MONTH? maxHeight:itemHeight;
        setMeasuredDimension((int)width,(int)height);
        Log.i("sssssssssssssss","inward w:"+width);
        Log.i("sssssssssssssss","inward h:"+maxHeight);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private float preX=0;
    private float preY=0;
    private final int ACT_SCROLL_X=1;
    private final int ACT_SCROLL_Y=2;
    private final int ACT_NONE=0;
    private int act=ACT_NONE;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("sssssssssssss","ACTION_DOWN");
                act=ACT_NONE;
                preX=event.getX();
                preY=event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(act==ACT_NONE) {
                    if (event.getY() - preY > 2 || event.getY() - preY < -2) act = ACT_SCROLL_Y;
                    else if(event.getX()-preX>2||event.getX()-preX<-2) act=ACT_SCROLL_X;
                }
                if(act==ACT_SCROLL_Y) moveY(event.getY()-preY);
                break;
            case MotionEvent.ACTION_UP:
                Log.i("sssssssssssss","ACTION_UP");
                if(act==ACT_NONE) {
                    if (preX - event.getX() < 10 && preX - event.getX() > -10 &&
                            preY - event.getY() < 10 && preY - event.getY() > -10) {
                        if (onItemClickListener != null) {
                            int i = (int) (event.getX() / (itemWidth + horizontalSpace));
                            int j = (int) (event.getY() / (itemHeight + verticalSpace));
                            selectedCalendar.setTimeInMillis(calendar.getTimeInMillis());
                            selectedCalendar.add(Calendar.DAY_OF_MONTH, i + j * 7);
                            int distance=selectedCalendar.get(Calendar.MONTH)-currentMonth;
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(selectedCalendar.getTimeInMillis());
                            switch (type) {
                                case TYPE_MONTH:
                                    if (distance == 0) {
                                        onItemClickListener.onClickCurrent(c);
                                    } else if (distance == 1 || distance == -11) {
                                        selectedCalendar.add(Calendar.MONTH, -1);
                                        onItemClickListener.onClickNext(c);
                                    } else if (distance == -1 || distance == 11) {
                                        selectedCalendar.add(Calendar.MONTH, +1);
                                        onItemClickListener.onClickPrevious(c);
                                    }
                                    break;
                                case TYPE_WEEK:
                                    onItemClickListener.onClickCurrent(c);
                                    break;
                            }
                            invalidate();
                        }
                    }
                    performClick();
                }else{
                    if(onScrollYListener!=null) onScrollYListener.onStop(this);
//                    animationY();
                }
//                Log.i("sssssssssss","pre_x:"+preX);
//                Log.i("sssssssssss","pre_y:"+preY);
//                Log.i("sssssssssss","_x:"+event.getX());
//                Log.i("sssssssssss","_y:"+event.getY());
                break;
        }
        return true;
    }
    private void moveY(float y){
//        if(y<0) {
//            this.setTranslationY(y);
//        ViewGroup.LayoutParams layoutParams=getLayoutParams();

//            this.offsetTopAndBottom((int) y);
            offsetY=offsetY+y;
            if(onScrollYListener!=null) onScrollYListener.onScroll(this,y);
//            setY(20+y);
            Log.i("sssssssssssss","y:"+y);
            Log.i("sssssssssssss","offset:"+offsetY);
            Log.i("sssssssssssss","preY:"+preY);
            Log.i("sssssssssssss","scrollY:"+getScrollY());
//            getParent()
//        }
//        this.scrollTo(0,(int)y);


//        long distance=selectedCalendar.getTimeInMillis()/24/3600-calendar.getTimeInMillis()/24/3000;

//        Log.i("ssssssssssss","y:"+y);
    }

    public void setOnScrollYListener(OnScrollYListener onScrollYListener) {
        this.onScrollYListener = onScrollYListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedCalendar(Calendar selectedCalendar) {
        this.selectedCalendar = selectedCalendar;
        initCalendarData();
        postInvalidate();
    }
    public void setSelected(Calendar c){
        switch (type){
            case TYPE_MONTH:
                int day=c.get(Calendar.DAY_OF_MONTH);
                if(day>selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) day=selectedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                this.selectedCalendar.set(Calendar.DAY_OF_MONTH,day);
                break;
            case TYPE_WEEK:
                int position=c.get(Calendar.DAY_OF_WEEK);
                this.selectedCalendar.set(Calendar.DAY_OF_WEEK,position);
                Log.i("ssssssssssss","position:"+position);
                break;
        }
        postInvalidate();
    }
    public int getMonth(){
        return selectedCalendar.get(Calendar.MONTH)+1;
    }
    public int getDay(){
        return selectedCalendar.get(Calendar.DAY_OF_MONTH);
    }
    public int getLine(){
        Log.i("sssssssssssss","selected:"+selectedCalendar.get(Calendar.MONTH)+" "+selectedCalendar.get(Calendar.DAY_OF_MONTH));
        Log.i("sssssssssssss","calendar:"+calendar.get(Calendar.MONTH)+" "+calendar.get(Calendar.DAY_OF_MONTH));
        long interval=(selectedCalendar.getTimeInMillis()-calendar.getTimeInMillis())/24/3600/1000;
        return (int) interval/7;

    }
    public Calendar getSelectedCalendar(){
        return selectedCalendar;
    }
    public void add(int add){
        switch (type){
            case TYPE_MONTH:
                CalendarUtil.addMonth(this.selectedCalendar,add);
                break;
            case TYPE_WEEK:
                selectedCalendar.add(Calendar.WEEK_OF_MONTH,add);
                break;
        }
        initCalendarData();
        postInvalidate();
    }

    public void switchToMonth(){

    }
    public void switchToWeek(Calendar selectedCalendar){
        this.type=TYPE_WEEK;
        this.selectedCalendar.setTimeInMillis(selectedCalendar.getTimeInMillis());
        initCalendarData();
        requestLayout();
        postInvalidate();
    }

    public float getItemHeight() {
        return itemHeight;
    }

    public float getItemWidth() {
        return itemWidth;
    }

    public float getHorizontalSpace() {
        return horizontalSpace;
    }

    public float getVerticalSpace() {
        return verticalSpace;
    }

    public interface OnItemClickListener{
        void onClickCurrent(Calendar calendar);
        void onClickPrevious(Calendar calendar);
        void onClickNext(Calendar calendar);
    }
    public interface OnScrollYListener{
        void onScroll(View view, float y);
        void onStop(View view);
    }
}
