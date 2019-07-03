package com.onetwothree.addressbook;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

public class CallLogActivity extends AppCompatActivity {
    //用于读取通讯记录的变量
    ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    SimpleAdapter adapter;
    CallRecordUtil callRecord;
    DbOpenHandler helper;
    Dbutil dbutil;
    ArrayList<CallRecord> records = new ArrayList<CallRecord>();
    ArrayList<Integer> index_records = new ArrayList<Integer>();
    ArrayList<Integer> index_records_num = new ArrayList<Integer>();
    ArrayList<Integer> index_records_date = new ArrayList<Integer>();
    Boolean date_opt = false;
    //日期统计控件
    TextView date;
    TextView call_fre1;
    TextView call_time1;
    TextView call_fre;
    TextView call_time;

    //电话号码输入框控件
    EditText number_input;

    //通话记录listview
    ListView call_log_listView;

    //对话框
    AlertDialog alertDialog = null;
    AlertDialog.Builder dialogBuilder = null;

    //长按选中view
    int choose;

    //根据日期筛选
    Date start_date;
    Date end_date;

    //根据号码筛选
    String number_phone = "";

    @Override
    protected void onResume() {
        super.onResume();
        records = dbutil.getAllCallRecord();
        search_by_number(number_phone);
        if (date_opt)
            search_by_date(start_date, end_date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("通话记录");
        //日期统计
        date = (TextView) findViewById(R.id.date);
        call_fre = (TextView) findViewById(R.id.call_frequent);
        call_time = (TextView) findViewById(R.id.call_time);
        call_fre1 = (TextView) findViewById(R.id.call_frequent1);
        call_time1 = (TextView) findViewById(R.id.call_time1);
        //通话记录数据库
        dbutil = Dbutil.getInstance();
        //获取所有通话记录
        records = dbutil.getAllCallRecord();
        index_records = new ArrayList<Integer>();
        for (int i = 0; i < records.size(); i++) {
            index_records.add(i);
            index_records_num.add(i);
            index_records_date.add(i);
        }

        number_input = (EditText) findViewById(R.id.number_input);
        //显示通话记录
        call_log_listView = (ListView) findViewById(R.id.call_log_listview);
        show_call_log();
        //防止软键盘顶控件
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //监视输入框电话号码变化
        number_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //根据输入框电话号码显示通话记录
                //Toast.makeText(getBaseContext(), number_input.getText().toString(), Toast.LENGTH_SHORT).show();
                number_phone = number_input.getText().toString();
                search_by_number(number_input.getText().toString());
            }
        });

        this.registerForContextMenu(call_log_listView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support_call_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "返回上一级界面", Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            case R.id.menu1:
                Toast.makeText(this, "根据日期筛选", Toast.LENGTH_SHORT).show();
                //使用日期筛选功能
                date_opt = true;
                //选择日期
                search_by_date_dialog();
                break;
            case R.id.menu2:
                Toast.makeText(this, "取消日期筛选", Toast.LENGTH_SHORT).show();
                //不使用日期筛选功能
                date_opt = false;
                //只显示通过号码筛选的通讯记录
                search_by_number(number_phone);
                //隐藏时间日期
                date.setVisibility(View.GONE);
                call_fre.setVisibility(View.GONE);
                call_time.setVisibility(View.GONE);
                call_fre1.setVisibility(View.GONE);
                call_time1.setVisibility(View.GONE);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //显示通话记录
    public void show_call_log() {
        list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < index_records.size(); i++) {
            Integer index = index_records.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("phone_number", utils.PhoneNumberDeformat(records.get(index).getNumber()));
            //map.put("phone_number",records.get(index).getNumber());
            map.put("date", utils.formatDate(records.get(index).getDate().getTime()));
            map.put("name", records.get(index).getName());
            map.put("type", records.get(index).getShowType());
            list.add(map);
        }
        adapter = new SimpleAdapter(this, list,
                R.layout.call_log_list_item, new String[]{"phone_number", "date", "name", "type"},
                new int[]{R.id.phone_number, R.id.date, R.id.name, R.id.type});

        call_log_listView.setAdapter(adapter);
        call_log_listView.setOnItemClickListener(new itemClick());
        call_log_listView.setOnItemLongClickListener(new itemLongClick());
        //计算通话数及通话时长总和
        count_time_fre();
    }


    //短触事件
    class itemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(parent.getContext(), CallLogDetail.class);
            intent.putExtra("name", records.get(position).getName());
            intent.putExtra("number", utils.PhoneNumberDeformat(records.get(position).getNumber()));
            intent.putExtra("type", records.get(position).getShowType());
            intent.putExtra("date", utils.formatDate(records.get(position).getDate().getTime()));
            intent.putExtra("time", utils.formatDuration(records.get(position).getTime()));
            //intent.putExtra("location", records.get(position).getLocation());
            intent.putExtra("position", String.valueOf(position));
            startActivityForResult(intent, 1);
        }
    }

    //取得activity返回结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = (boolean) data.getExtras().getBoolean("delete");
        int position = Integer.valueOf(data.getExtras().getString("position"));
        if (result) {
            delete_call_log(position);
        }
    }

    //长触事件
    class itemLongClick implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {
            choose = index_records.get(position);
            PopupMenu popup = new PopupMenu(parent.getContext(), view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.setGravity(Gravity.CENTER);
            }
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.call_number:
                            Toast.makeText(getBaseContext(),"拨打电话号码",Toast.LENGTH_SHORT).show();
                            //callPhone(utils.PhoneNumberDeformat(records.get(choose).getNumber()));
                            Toast.makeText(getBaseContext(),String.valueOf(choose),Toast.LENGTH_SHORT).show();
                            callPhone(records.get(choose).getNumber());
                            break;
                        case R.id.copy_number:
                            Toast.makeText(getBaseContext(),"复制电话号码",Toast.LENGTH_SHORT).show();
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("Label", utils.PhoneNumberDeformat(
                                    records.get(choose).getNumber()));
                            cm.setPrimaryClip(mClipData);
                            break;
                        case R.id.delete_number:
                            Toast.makeText(getBaseContext(),"删除通话记录",Toast.LENGTH_SHORT).show();
                            //删除通话记录
                            custom_view_delete();
                            break;
                    }
                    return false;
                }
            });
            popup.show();
            return true;
        }
    }

    //拨打电话号码--不跳转
    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this,"拨打电话号码失败",Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }


    //拨打电话号码--跳转
    /*public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }*/

    //电话号码前缀匹配
    public void search_by_number(String number){
        index_records_num = new ArrayList<Integer>();
        if (number.equals(""))
        {
            for(int i = 0; i < records.size(); i++){
                index_records_num.add(i);
            }

        }
        else{
            for(int i = 0; i < records.size(); i++){
                String num = utils.PhoneNumberDeformat(records.get(i).getNumber());
                Boolean flag = true;
                if (num.length() < number.length())
                    flag = false;
                else{
                    for (int j = 0; j < number.length(); j++){
                        if (number.charAt(j) != num.charAt(j)){
                            flag = false;
                        }
                    }
                }
                if (flag){
                    index_records_num.add(i);
                }
            }
        }
        index_records = new ArrayList<Integer>();
        if (date_opt){
            for (int i = 0; i < index_records_num.size(); i++){
                for (int j = 0; j < index_records_date.size(); j++){
                    if(index_records_num.get(i) == index_records_date.get(j)){
                        index_records.add(index_records_num.get(i));
                        break;
                    }
                }
            }
        }
        else{
            for (int i = 0; i < index_records_num.size(); i++){
                index_records.add(index_records_num.get(i));
            }
        }
        show_call_log();
    }
    //根据日期显示通话记录
    public void search_by_date(Date start, Date end){
        index_records_date = new ArrayList<Integer>();
        for(int i = 0; i < records.size(); i++){
            Date date = records.get(i).getDate();
            if (date.getTime() >= start.getTime() && date.getTime() <= end.getTime()){
                index_records_date.add(i);
            }
        }
        index_records = new ArrayList<Integer>();
        for (int i = 0; i < index_records_num.size(); i++){
            for (int j = 0; j < index_records_date.size(); j++){
                if(index_records_num.get(i) == index_records_date.get(j)){
                    index_records.add(index_records_num.get(i));
                    break;
                }
            }
        }
        show_call_log();
    }

    //计算通话数及通话时长总和
    public void count_time_fre(){
        long time = 0;
        Integer fre = index_records.size();
        for (int i = 0; i < index_records.size(); i++){
            time += records.get(index_records.get(i)).getTime();
        }
        call_fre.setText(fre.toString() + "个");
        call_time.setText(utils.formatDuration(time));
    }

    //根据日期筛选通话记录
    public void search_by_date_dialog(){
        Calendar c = Calendar.getInstance();
        // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
        new DoubleDatePickerDialog(CallLogActivity.this, 3, new DoubleDatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                  int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                  int endDayOfMonth) {
                String textString = String.format("开始日期：%d-%d-%d\n结束日期：%d-%d-%d\n", startYear,
                        startMonthOfYear + 1, startDayOfMonth, endYear, endMonthOfYear + 1, endDayOfMonth);
                //显示筛选结果
                start_date = utils.GenerateDate(startYear, startMonthOfYear+ 1, startDayOfMonth, 0, 0, 0);
                end_date = utils.GenerateDate(endYear, endMonthOfYear + 1, endDayOfMonth, 23, 59, 59);
                search_by_date(start_date, end_date);
                date.setText(textString);
                date.setVisibility(View.VISIBLE);
                call_fre.setVisibility(View.VISIBLE);
                call_time.setVisibility(View.VISIBLE);
                call_fre1.setVisibility(View.VISIBLE);
                call_time1.setVisibility(View.VISIBLE);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
    }

    //根据postion删除通话记录
    public void delete_call_log(int position){
        dbutil.DeleteCallRecord(records.get(position).getDate().getTime());
        records = dbutil.getAllCallRecord();
        search_by_number(number_phone);
        if (date_opt)
            search_by_date(start_date, end_date);
    }

    //删除通话记录对话框
    public void custom_view_delete() {

        TableLayout add_word_form = (TableLayout) getLayoutInflater()
                .inflate(R.layout.delete_call_log, null);
        dialogBuilder = new AlertDialog.Builder(this);
        alertDialog = dialogBuilder
                /*// 设置图标
                .setIcon(R.drawable.)*/
                // 设置对话框标题
                /*.setTitle("删除单词")
                // 设置对话框显示的View对象*/
                .setView(add_word_form)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(mContext, "点击了取消按钮", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getBaseContext(), choose.toString(), Toast.LENGTH_SHORT).show();
                        delete_call_log(choose);
                    }
                })
                .create();             // 创建AlertDialog对象
        alertDialog.show();
    }
}
