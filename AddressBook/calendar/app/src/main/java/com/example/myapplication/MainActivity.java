package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements
        CalendarView.OnDateSelectedListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener{

    Calendar mCalendar;
    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;
    RecyclerView mRecyclerView;
    DateToFestivalsUtil mDateToFestivalsUtil = new DateToFestivalsUtil();


    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        setStatusBarDarkMode();
        mTextMonthDay = (TextView) findViewById(R.id.tv_month_day);
        mTextYear = (TextView) findViewById(R.id.tv_year);
        mTextLunar = (TextView) findViewById(R.id.tv_lunar);
        mRelativeTool = (RelativeLayout) findViewById(R.id.rl_tool);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mTextCurrentDay = (TextView) findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarView.showYearSelectLayout(mYear);
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView.setOnDateSelectedListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
    }

    @Override
    public void onClick(View v) {

    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800,"假");
        calendar.addScheme(0xFF008800,"节");
        return calendar;
    }

    @Override
    protected void initData() {
        List<Calendar> schemes = new ArrayList<>();
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();
        int day = mCalendarView.getCurDay();


        String Festival=mDateToFestivalsUtil.getLunarDate(year,month,day,false);


       /* schemes.add(getSchemeCalendar(year, month, 3, 0xFF40db25,"假"));

        schemes.add(getSchemeCalendar(year, month, 6, 0xFFe69138,"事"));
        schemes.add(getSchemeCalendar(year, month, 9, 0xFFdf1356,"议"));
        schemes.add(getSchemeCalendar(year, month, 13, 0xFFedc56d,"记"));
        schemes.add(getSchemeCalendar(year, month, 14, 0xFFedc56d,"记"));
        schemes.add(getSchemeCalendar(year, month, 15, 0xFFaacc44,"假"));
        schemes.add(getSchemeCalendar(year, month, 18, 0xFFbc13f0,"记"));
        schemes.add(getSchemeCalendar(year, month, 25, 0xFF13acf0,"假"));
        schemes.add(getSchemeCalendar(year, month, 27, 0xFF13acf0,"多"));*/

        schemes.add(getSchemeCalendar(year, month, day, 0xFFaacc44,"今"));
        mCalendarView.setSchemeDate(schemes);



        List<String> stringList=new ArrayList<>();
        stringList.add("今天是" + month + "月" + day + "日");
        if(Festival!=null){
            stringList.add("今天是" + Festival + "节,记得给家人打电话哦" );
        }



        //mListView = (ListView) findViewById(R.id.listView);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stringList);//新建并配置ArrayAapeter
       // mListView.setAdapter(adapter);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecycleAdapterDome(this,stringList));

    }





    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }



    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

   /* public boolean isfestival(int year,int month,int day){
        String str = null;

    }*/

   /* public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        Context context;
        List<String> stringList;

        public MyAdapter(Context context, List<String> stringList) {
            this.context = context;
            this.stringList = stringList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(context).inflate(R.layout.item_string,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.textView.setText(stringList.get(position));
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            public MyViewHolder(View itemView) {
                super(itemView);
                textView= (TextView) itemView.findViewById(R.id.string);
            }
        }
    }*/


}


