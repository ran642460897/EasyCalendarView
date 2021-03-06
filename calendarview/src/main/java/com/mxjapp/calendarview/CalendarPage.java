package com.mxjapp.calendarview;

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


import com.mxjapp.calendarview.entity.StyleAttr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * user: Jason Ran
 * date: 2018/7/24.
 */
public class CalendarPage extends View implements NestedScrollingChild{
    private static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private int type=TYPE_MONTH;
    public final static int TYPE_MONTH=0;
    public final static int TYPE_WEEK=1;
    private Paint p;
    private Paint.FontMetricsInt fm;
    private float viewWidth,viewHeight;
    private float horizontalSpace;
    private float itemWidth;
    private RectF selectedRectF;
    private int itemHeight,maxHeight,verticalSpace;
    private OnItemClickListener onItemClickListener;
    private OnScrollYListener onScrollYListener;
    private Calendar selectedCalendar;
    private Calendar calendar;
    private static final int GESTURE_ERROR=5;
    private Map<String,Integer> marks=new HashMap<>();
    private StyleAttr attr;

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
        p=new Paint();
        p.setAntiAlias(true);
        selectedRectF=new RectF(0,0,0,0);
    }
    private void initAttrData(){
        if(attr!=null) {
            p.setTextSize(convertSpToPx(getContext(), attr.getTextSize()));
            fm=p.getFontMetricsInt();
            horizontalSpace = convertDpToPx(getContext(), attr.getHorizontalSpace());
            verticalSpace = convertDpToPx(getContext(), attr.getVerticalSpace());
        }
    }
    private void initCalendarData(){
        if(selectedCalendar==null) selectedCalendar=Calendar.getInstance();
        if(calendar==null) calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedCalendar.getTimeInMillis());
        switch (type){
            case TYPE_MONTH:
                calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为月第一天
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
        int currentMonth=selectedCalendar.get(Calendar.MONTH);
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
                    if (calendar.get(Calendar.MONTH) != currentMonth) p.setColor(attr.getTextDimColor());//修改非本月颜色
                    else p.setColor(attr.getTextColor());

                if(calendar.getTimeInMillis()/24/3600==selectedCalendar.getTimeInMillis()/24/3600) {
                    p.setColor(attr.getBackgroundSelectedColor());
                    selectedRectF.right=itemWidth;
                    selectedRectF.bottom=itemHeight;
                    canvas.drawRoundRect(selectedRectF,20,20,p);//画选中背景
                    p.setColor(attr.getTextSelectedColor());//修改选中字体颜色
                }
                String s=calendar.get(Calendar.DAY_OF_MONTH)+""; //画日期
                canvas.drawText(s,(itemWidth-p.measureText(s))/2,(itemHeight+fm.bottom-fm.top)/2-fm.descent,p);
                if(getMarkNumber(calendar)>0) {
                    p.setColor(attr.getMarkColor());
                    canvas.drawCircle(itemWidth - 20, 20, 6, p);
                }

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
        viewWidth=getMeasuredWidth();
        itemWidth=(viewWidth-6*horizontalSpace)/7;
        itemHeight=(int)itemWidth;
        maxHeight=itemHeight*6+verticalSpace*5;
        viewHeight=type==TYPE_MONTH? maxHeight:itemHeight;
        setMeasuredDimension((int)viewWidth,(int)viewHeight);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private float firstX=0,firstY=0,preY=0;
    public static final int ACT_SCROLL_X=1;
    public static final int ACT_SCROLL_MONTH_TO_WEEK=2;
    public static final int ACT_SCROLL_WEEK_TO_MONTH=3;
    public static final int ACT_NONE=0;
    private int act=ACT_NONE;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                Log.i("sssssssssssss","ACTION_DOWN");
                act=ACT_NONE;
                firstX=event.getX();
                firstY=event.getY();
                preY=event.getRawY();
                if(type==TYPE_MONTH&&onScrollYListener!=null) onScrollYListener.onPreScroll(this);
                break;
            case MotionEvent.ACTION_MOVE:
                if(act==ACT_NONE) {
                    float distanceY=event.getY()-firstY;
                    float distanceX=event.getX()-firstX;
                    if (distanceY<-GESTURE_ERROR && type==TYPE_MONTH) act = ACT_SCROLL_MONTH_TO_WEEK;
                    else if(distanceY>GESTURE_ERROR&&type==TYPE_WEEK) act=ACT_SCROLL_WEEK_TO_MONTH;
                    else if(distanceX>GESTURE_ERROR||distanceX<-GESTURE_ERROR) act=ACT_SCROLL_X;
                }
                if(act==ACT_SCROLL_WEEK_TO_MONTH||act==ACT_SCROLL_MONTH_TO_WEEK) moveY(preY-event.getRawY());
                preY=event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
//                Log.i("sssssssssssss","ACTION_UP");
                if(act==ACT_NONE) {
                    if (firstX - event.getX() < 10 && firstX - event.getX() > -10 &&
                            firstY - event.getY() < 10 && firstY - event.getY() > -10) {
                        if (onItemClickListener != null) {
                            int currentMonth=selectedCalendar.get(Calendar.MONTH);
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
                                        onItemClickListener.onClickCurrent(c,getMarkNumber(c));
                                    } else if (distance == 1 || distance == -11) {
                                        selectedCalendar.add(Calendar.MONTH, -1);
                                        onItemClickListener.onClickNext(c,getMarkNumber(c));
                                    } else if (distance == -1 || distance == 11) {
                                        selectedCalendar.add(Calendar.MONTH, +1);
                                        onItemClickListener.onClickPrevious(c,getMarkNumber(c));
                                    }
                                    break;
                                case TYPE_WEEK:
                                    onItemClickListener.onClickCurrent(c,getMarkNumber(c));
                                    break;
                            }
                            invalidate();
                        }
                    }
                    performClick();
                }else{
                    if(onScrollYListener!=null) onScrollYListener.onStopScroll(this,act);
                }
                break;
        }
        return true;
    }
    private void moveY(float y){
        if(onScrollYListener!=null) onScrollYListener.onScroll(this,(int)y,act);
    }

    public void setOnScrollYListener(OnScrollYListener onScrollYListener) {
        this.onScrollYListener = onScrollYListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
                break;
        }
        postInvalidate();
    }

    public void addMarks(Map<String,Integer> marks) {
        this.marks.putAll(marks);
        postInvalidate();
    }
    public int getLine(){
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(selectedCalendar.getTimeInMillis());
        int dayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH,1);
        int firstDayWeek=c.get(Calendar.DAY_OF_WEEK);
        int interval=dayOfMonth-1+firstDayWeek-1;
        return interval/7;
    }
    public Calendar getSelectedCalendar(){
        return selectedCalendar;
    }
    public void add(int add){
        switch (type){
            case TYPE_MONTH:
                selectedCalendar.add(Calendar.MONTH,add);
//                CalendarUtil.addMonth(this.selectedCalendar,add);
                break;
            case TYPE_WEEK:
                selectedCalendar.add(Calendar.WEEK_OF_MONTH,add);
                break;
        }
        notifyChanged();
    }
    public void notifyChanged(){
        initCalendarData();
        postInvalidate();
    }
    public void setSelectedCalendar(Calendar selectedCalendar) {
        this.selectedCalendar.setTimeInMillis(selectedCalendar.getTimeInMillis());
    }
    public void switchToMonth(Calendar selectedCalendar){
        this.type=TYPE_MONTH;
        this.selectedCalendar.setTimeInMillis(selectedCalendar.getTimeInMillis());
        initCalendarData();
        requestLayout();
//        postInvalidate();
    }
    public void switchToWeek(Calendar selectedCalendar){
        this.type=TYPE_WEEK;
        this.selectedCalendar.setTimeInMillis(selectedCalendar.getTimeInMillis());
        initCalendarData();
        requestLayout();
//        postInvalidate();
    }
    private int convertDpToPx(Context context, float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    private int convertSpToPx(Context context, float spValue){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    private int getMarkNumber(Calendar c){
        Integer integer=marks.get(dateFormat.format(c.getTime()));
        return integer==null?0:integer;
    }
    public int getSelectedMark(){
        return getMarkNumber(selectedCalendar);
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public float getItemWidth() {
        return itemWidth;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public float getHorizontalSpace() {
        return horizontalSpace;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public void setAttr(StyleAttr attr) {
        this.attr = attr;
        initAttrData();
    }

    public interface OnItemClickListener{
        void onClickCurrent(Calendar calendar,int mark);
        void onClickPrevious(Calendar calendar,int mark);
        void onClickNext(Calendar calendar,int mark);
    }
    public interface OnScrollYListener{
        void onPreScroll(CalendarPage view);
        void onScroll(CalendarPage view, float y, int act);
        void onStopScroll(CalendarPage view, int act);
    }
}
