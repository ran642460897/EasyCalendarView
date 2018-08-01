package com.mxjapp.library.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.mxjapp.library.CalendarPage;

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
        try {
            container.addView(views.get(position % 3));
        }catch (Exception e){
//            e.printStackTrace();
        }
        return views.get(position%3);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }
}
