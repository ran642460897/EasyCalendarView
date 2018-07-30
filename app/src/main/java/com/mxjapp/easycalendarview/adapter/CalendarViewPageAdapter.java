package com.mxjapp.easycalendarview.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.mxjapp.easycalendarview.CalendarPage;

import java.util.List;

/**
 * user: Jason Ran
 * date: 2018/7/25.
 */
public class CalendarViewPageAdapter extends PagerAdapter {
    private List<CalendarPage> views;

    public CalendarViewPageAdapter(List<CalendarPage> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        Log.i("sssssssssssssss","instant item");
        try {
            container.addView(views.get(position % 3));
        }catch (Exception e){
//            e.printStackTrace();
        }
        return views.get(position%3);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        Log.i("ssssssssssssss","destroy item");
//        container.removeView(views.get(position%3));
    }
}
