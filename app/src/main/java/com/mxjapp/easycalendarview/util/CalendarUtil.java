package com.mxjapp.easycalendarview.util;

import java.util.Calendar;

public class CalendarUtil {
    public static void addMonth(Calendar calendar,int month){
        calendar.add(Calendar.MONTH,month);
    }
}