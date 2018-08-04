package com.mxjapp.easycalendarview.ui;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mxjapp.calendarview.CalendarPage;
import com.mxjapp.calendarview.EasyCalendarView;
import com.mxjapp.calendarview.entity.CalendarHint;
import com.mxjapp.easycalendarview.R;
import com.mxjapp.easycalendarview.adapter.ItemAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EasyCalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
//        RecyclerView recyclerView=findViewById(R.id.recycler);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new ItemAdapter());
        calendarView=findViewById(R.id.calendar);
//        calendarView.setInitType(CalendarPage.TYPE_WEEK);
        calendarView.setOnDateChangedListener(new EasyCalendarView.OnDateChangedListener() {
            @Override
            public void onDateChanged(Calendar calendar, int mark) {
                Log.i("ssssssssssssssss","date change,mark:"+mark);
            }
        });
        Map<String,Integer> map=new HashMap<>();
        map.put("2018-08-02",1);
        map.put("2018-08-03",2);
        calendarView.addMarks(map);
//        findViewById(R.id.test_set_date).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar calendar=Calendar.getInstance();
//                calendar.add(Calendar.MONTH,6);
//                calendarView.setDate(calendar);
//            }
//        });
    }
}
