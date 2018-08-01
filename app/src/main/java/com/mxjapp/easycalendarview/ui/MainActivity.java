package com.mxjapp.easycalendarview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mxjapp.calendarview.EasyCalendarView;
import com.mxjapp.calendarview.entity.CalendarHint;
import com.mxjapp.easycalendarview.R;
import com.mxjapp.easycalendarview.adapter.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        RecyclerView recyclerView=findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ItemAdapter());
        EasyCalendarView calendarView=findViewById(R.id.calendar);
        List<CalendarHint> hints=new ArrayList<>();
        for(int i=0;i<42;i++){
            hints.add(new CalendarHint("00",1));
        }
        calendarView.setHints(hints);
    }
}
