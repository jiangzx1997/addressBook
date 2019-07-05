package com.onetwothree.addressbook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnFocusChangeListener;
import android.app.DatePickerDialog.OnDateSetListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {
    /**
     * 搜索栏
     */
    EditText edit_search;
    /**
     * 列表
     */
    PinnedSectionListView listView;
    /**
     * 右边字母列表
     */
    LetterIndexView letterIndexView;
    /**
     * 中间显示右边按的字母
     */
    TextView txt_center;

    /**
     * 所有名字集合
     */
    private ArrayList<Contacts> list_all;
    /**
     * 显示名字集合
     */
    private ArrayList<Contacts> list_show;
    /**
     * 列表适配器
     */
    private PhoneAdapter adapter;
    /**
     * 保存名字首字母
     */
    public HashMap<String, Integer> map_IsHead;
    /**
     * item标识为0
     */
    public static final int ITEM = 0;
    /**
     * item标题标识为1
     */
    public static final int TITLE = 1;

    private ActionBar actionBar;
    private Dbutil dbutil;
    private Intent intent;

    //对话框
    AlertDialog alertDialog = null;
    AlertDialog.Builder dialogBuilder = null;

    //删除对象位置
    int choose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        edit_search = (EditText) findViewById(R.id.edit_search);
        listView = (PinnedSectionListView) findViewById(R.id.phone_listview);
        letterIndexView = (LetterIndexView) findViewById(R.id.phone_LetterIndexView);
        txt_center = (TextView) findViewById(R.id.phone_txt_center);

        actionBar = getSupportActionBar();
        actionBar.setTitle("通讯录");

        dbutil = Dbutil.getInstance();
        //防止软键盘顶控件
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initView();
        initData();
    }

    //取得activity返回结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = (boolean) data.getExtras().getBoolean("delete");
        int position = Integer.valueOf(data.getExtras().getString("position"));
        if (result) {
            delete_contact(position);
        }
    }

    private void initView() {

        // 输入监听
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                list_show.clear();
                map_IsHead.clear();
                //把输入的字符改成大写
                String search = editable.toString().trim().toUpperCase();
                if (TextUtils.isEmpty(search)) {
                    for (int i = 0; i < list_all.size(); i++) {
                        Contacts bean = list_all.get(i);
                        //中文字符匹配首字母和英文字符匹配首字母
                        if (!map_IsHead.containsKey(bean.getHeadChar())) {// 如果不包含就添加一个标题
                            Contacts bean1 = new Contacts();
                            // 设置名字
                            bean1.setName(bean.getName());
                            // 设置标题type
                            bean1.setType(ContactActivity.TITLE);
                            list_show.add(bean1);
                            // map的值为标题的下标
                            map_IsHead.put(bean1.getHeadChar(),
                                    list_show.size() - 1);
                        }
                        // 设置Item type
                        bean.setType(ContactActivity.ITEM);
                        list_show.add(bean);
                    }
                } else {
                    for (int i = 0; i < list_all.size(); i++) {
                        Contacts bean = list_all.get(i);
                        //中文字符匹配首字母和英文字符匹配首字母
                        if (bean.getName().indexOf(search) != -1|| bean.getName_en().indexOf(search) != -1) {
                            if (!map_IsHead.containsKey(bean.getHeadChar())) {// 如果不包含就添加一个标题
                                Contacts bean1 = new Contacts();
                                // 设置名字
                                bean1.setName(bean.getName());
                                // 设置标题type
                                bean1.setType(ContactActivity.TITLE);
                                list_show.add(bean1);

                                // map的值为标题的下标
                                map_IsHead.put(bean1.getHeadChar(),
                                        list_show.size() - 1);
                            }
                            // 设置Item type
                            bean.setType(ContactActivity.ITEM);
                            list_show.add(bean);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

            }
        });

        // 右边字母竖排的初始化以及监听
        letterIndexView.init(new LetterIndexView.OnTouchLetterIndex() {
            //实现移动接口
            @Override
            public void touchLetterWitch(String letter) {
                // 中间显示的首字母
                txt_center.setVisibility(View.VISIBLE);
                txt_center.setText(letter);
                // 首字母是否被包含
                if (adapter.map_IsHead.containsKey(letter)) {
                    // 设置首字母的位置
                    listView.setSelection(adapter.map_IsHead.get(letter));
                }
            }
            //实现抬起接口
            @Override
            public void touchFinish() {
                txt_center.setVisibility(View.GONE);
            }
        });

        /** listview点击事件 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,int i, long l) {
                if (list_show.get(i).getType() == ContactActivity.ITEM) {// 标题点击不给操作
                    //Toast.makeText(ContactActivity.this,list_show.get(i).getName(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(adapterView.getContext(), ContactDetail.class);
                    intent.putExtra("name", list_show.get(i).getName());
                    intent.putExtra("number", list_show.get(i).getNumbersString());
                    intent.putExtra("birth", utils.formatDateBirth(list_show.get(i).getBirthday().getTime()));
                    intent.putExtra("email", list_show.get(i).getEmail());
                    intent.putExtra("position", String.valueOf(i));
                    startActivityForResult(intent, 1);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (list_show.get(position).getType() != ContactActivity.ITEM)
                {
                    return true;
                }
                PopupMenu popup = new PopupMenu(ContactActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.contact_pop, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.delete:
                                //Toast.makeText(ContactActivity.this, "删除", Toast.LENGTH_SHORT).show();
                                choose = position;
                                custom_view_delete();
                                break;
                            case R.id.update:
                                //Toast.makeText(ContactActivity.this, "修改", Toast.LENGTH_SHORT).show();
                                choose = position;
                                custom_view_change();
                                break;
                            case R.id.qrcode:
                                Intent intent = new Intent(ContactActivity.this, QRcodeActivity.class);
                                intent.putExtra("data", list_show.get(position).getName()+"\n"+list_show.get(position).getNumbersString());
                                startActivity(intent);
                                //Toast.makeText(ContactActivity.this, "导出二维码", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });

        // 设置标题部分有阴影
        // listView.setShadowVisible(true);
    }

    protected void initData() {
        list_all = new ArrayList<Contacts>();
        list_show = new ArrayList<Contacts>();
        map_IsHead = new HashMap<String, Integer>();
        adapter = new PhoneAdapter(ContactActivity.this, list_show, map_IsHead);
        listView.setAdapter(adapter);

        // 开启异步加载数据
        new Thread(runnable).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String[] str = getResources().getStringArray(R.array.phone_all);
            ArrayList<Contacts> allContacts = dbutil.getAllContacts();
            for (int i = 0; i < allContacts.size(); i++)
                list_all.add(allContacts.get(i));

//            for (int i = 0; i < str.length; i++) {
//                PhoneBean cityBean = new PhoneBean();
//                cityBean.setName(str[i]);
//                list_all.add(cityBean);
//            }
            /*for (int i = 0; i < contactsrec.size(); i++) {
                Contacts cityBean = new Contacts();

                cityBean.setName(contactsrec.get(i).getName());
                cityBean.setNumbers(contactsrec.get(i).getNumbers());
                list_all.add(cityBean);
            }*/
            //按拼音排序
            MemberSortUtil sortUtil = new MemberSortUtil();
            Collections.sort(list_all, sortUtil);

            // 初始化数据，顺便放入把标题放入map集合
            for (int i = 0; i < list_all.size(); i++) {
                Contacts cityBean = list_all.get(i);
                if (!map_IsHead.containsKey(cityBean.getHeadChar())) {// 如果不包含就添加一个标题
                    Contacts cityBean1 = new Contacts();
                    // 设置名字
                    cityBean1.setName(cityBean.getName());
                    // 设置标题type
                    cityBean1.setType(ContactActivity.TITLE);
                    list_show.add(cityBean1);

                    // map的值为标题的下标
                    map_IsHead.put(cityBean1.getHeadChar(),list_show.size() - 1);
                }
                list_show.add(cityBean);
            }

            handler.sendMessage(handler.obtainMessage());
        }
    };


    //删除通话记录对话框
    public void custom_view_delete() {

        TableLayout add_word_form = (TableLayout) getLayoutInflater()
                .inflate(R.layout.delete_contact, null);
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
                        delete_contact(choose);
                    }
                })
                .create();             // 创建AlertDialog对象
        alertDialog.show();
    }

    //删除联系人
    private void delete_contact(int position) {
        int id = list_show.get(position).get_id();
        if(id == 0)
        {
            Toast.makeText(ContactActivity.this, "不能删除字母栏!", Toast.LENGTH_SHORT).show();
            return;
        }
        dbutil.DeleteContact(id);
        ArrayList<Contacts> allContacts = dbutil.getAllContacts();
        list_all.clear();
        list_show.clear();
        map_IsHead.clear();
        for (int i = 0; i < allContacts.size(); i++)
        {
            list_all.add(allContacts.get(i));
        }
        //按拼音排序
        MemberSortUtil sortUtil = new MemberSortUtil();
        Collections.sort(list_all, sortUtil);
        // 初始化数据，顺便放入把标题放入map集合
        for (int i = 0; i < list_all.size(); i++) {
            Contacts cityBean = list_all.get(i);
            if (!map_IsHead.containsKey(cityBean.getHeadChar())) {// 如果不包含就添加一个标题
                Contacts cityBean1 = new Contacts();
                // 设置名字
                cityBean1.setName(cityBean.getName());
                // 设置标题type
                cityBean1.setType(ContactActivity.TITLE);
                list_show.add(cityBean1);
                // map的值为标题的下标
                map_IsHead.put(cityBean1.getHeadChar(),list_show.size() - 1);
            }
            list_show.add(cityBean);
        }
        listView.setAdapter(adapter);
        edit_search.setText("");
    }
    
    //添加联系人
    private void add_contact(String name, ArrayList<PhoneNumber> numbers, String email, String birth){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        if (birth.equals("")) {
            birth = "1111-1-1";
        }
        try {
            date = sdf.parse(birth);
        } catch (ParseException err) {
        }
        Contacts contacts;
        contacts = new Contacts();
        contacts.setName(name);
        if (date != null)
            contacts.setBirthday(date.getTime());
        contacts.setEmail(email);

        if (numbers.size() == 0){
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setNumber("");
            phoneNumber.setType(PhoneNumber.PhoneType.Unknow);
            contacts.addNumber(phoneNumber);
        }
        else{
            for (int i = 0; i < numbers.size(); i++){
                contacts.addNumber(numbers.get(i));
            }
        }


        dbutil.addContact(contacts);

        ArrayList<Contacts> allContacts = dbutil.getAllContacts();
        list_all.clear();
        list_show.clear();
        map_IsHead.clear();
        for (int i = 0; i < allContacts.size(); i++)
        {
            list_all.add(allContacts.get(i));
        }
        //按拼音排序
        MemberSortUtil sortUtil = new MemberSortUtil();
        Collections.sort(list_all, sortUtil);
        // 初始化数据，顺便放入把标题放入map集合
        for (int i = 0; i < list_all.size(); i++) {
            Contacts cityBean = list_all.get(i);
            if (!map_IsHead.containsKey(cityBean.getHeadChar())) {// 如果不包含就添加一个标题
                Contacts cityBean1 = new Contacts();
                // 设置名字
                cityBean1.setName(cityBean.getName());
                // 设置标题type
                cityBean1.setType(ContactActivity.TITLE);
                list_show.add(cityBean1);
                // map的值为标题的下标
                map_IsHead.put(cityBean1.getHeadChar(),list_show.size() - 1);
            }
            list_show.add(cityBean);
        }
        listView.setAdapter(adapter);
        edit_search.setText("");
    }

    //添加联系人对话框
    public void custom_view_add() {
        TableLayout add_word_form = (TableLayout) getLayoutInflater()
                .inflate(R.layout.add_contact, null);
        dialogBuilder = new AlertDialog.Builder(this);
        alertDialog = dialogBuilder
                // 设置对话框标题
                .setTitle("添加联系人")
                // 设置对话框显示的View对象
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
                        EditText name_input = (EditText)alertDialog.findViewById(R.id.name);
                        //Toast.makeText(mContext, "点击了确定按钮", Toast.LENGTH_SHORT).show();

                        EditText email_input = (EditText) alertDialog.findViewById(R.id.email);
                        EditText birth_input = (EditText) alertDialog.findViewById(R.id.birth);

                        ArrayList<EditText> ed = new ArrayList<EditText>();
                        ArrayList<Spinner> sp = new ArrayList<Spinner>();
                        EditText number_input_1 = (EditText)alertDialog.findViewById(R.id.number1);
                        Spinner sp_1 = (Spinner) alertDialog.findViewById(R.id.types1);
                        ed.add(number_input_1);
                        sp.add(sp_1);
                        EditText number_input_2 = (EditText)alertDialog.findViewById(R.id.number2);
                        Spinner sp_2 = (Spinner) alertDialog.findViewById(R.id.types2);
                        ed.add(number_input_2);
                        sp.add(sp_2);
                        EditText number_input_3 = (EditText)alertDialog.findViewById(R.id.number3);
                        Spinner sp_3 = (Spinner) alertDialog.findViewById(R.id.types3);
                        ed.add(number_input_3);
                        sp.add(sp_3);
                        ArrayList<PhoneNumber> numbers = new ArrayList<PhoneNumber>();
                        for (int i = 0; i < 3; i++){
                            String number = ed.get(i).getText().toString();
                            int index = sp.get(i).getSelectedItemPosition();
                            if (!number.equals("")){
                                PhoneNumber phoneNumber = new PhoneNumber();
                                phoneNumber.setNumber(number);
                                switch (index){
                                    case 0:
                                        phoneNumber.setType(PhoneNumber.PhoneType.Unknow);
                                        break;
                                    case 1:
                                        phoneNumber.setType(PhoneNumber.PhoneType.Home);
                                        break;
                                    case 2:
                                        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
                                        break;
                                    case 3:
                                        phoneNumber.setType(PhoneNumber.PhoneType.Company);
                                        break;
                                }
                                numbers.add(phoneNumber);
                            }
                        }

                        String name = name_input.getText().toString();
                        String email = email_input.getText().toString();
                        String birth = birth_input.getText().toString();

                        if("".equals(name)){
                            Toast.makeText(getBaseContext(), "名字没有填写,添加失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        /*if("".equals(email)){
                            Toast.makeText(getBaseContext(), "邮箱没有填写,添加失败", Toast.LENGTH_SHORT).show();
                            return;
                        }*/
                        /*if("".equals(birth)){
                            Toast.makeText(getBaseContext(), "生日没有填写,添加失败", Toast.LENGTH_SHORT).show();
                            return;
                        }*/
                        add_contact(name, numbers, email, birth);
                        }
                })
                .create();             // 创建AlertDialog对象
        alertDialog.show();

        EditText birth_input = (EditText) alertDialog.findViewById(R.id.birth);
        birth_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickDlg();
                }
            }
        });

    }

    //修改联系人
    private void change_contact(String name, ArrayList<PhoneNumber> numbers, String email, String birth){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        if (birth.equals("")) {
            birth = "1111-1-1";
        }
        try {
            date = sdf.parse(birth);
        } catch (ParseException err) {
        }
        Contacts contacts;
        contacts = new Contacts();
        contacts.setName(name);
        if (date != null)
            contacts.setBirthday(date.getTime());
        contacts.setEmail(email);

        if (numbers.size() == 0){
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setNumber("");
            phoneNumber.setType(PhoneNumber.PhoneType.Unknow);
            contacts.addNumber(phoneNumber);
        }
        else{
            for (int i = 0; i < numbers.size(); i++){
                contacts.addNumber(numbers.get(i));
            }
        }

        dbutil.addContact(contacts);

        ArrayList<Contacts> allContacts = dbutil.getAllContacts();
        list_all.clear();
        list_show.clear();
        map_IsHead.clear();
        for (int i = 0; i < allContacts.size(); i++)
        {
            list_all.add(allContacts.get(i));
        }
        //按拼音排序
        MemberSortUtil sortUtil = new MemberSortUtil();
        Collections.sort(list_all, sortUtil);
        // 初始化数据，顺便放入把标题放入map集合
        for (int i = 0; i < list_all.size(); i++) {
            Contacts cityBean = list_all.get(i);
            if (!map_IsHead.containsKey(cityBean.getHeadChar())) {// 如果不包含就添加一个标题
                Contacts cityBean1 = new Contacts();
                // 设置名字
                cityBean1.setName(cityBean.getName());
                // 设置标题type
                cityBean1.setType(ContactActivity.TITLE);
                list_show.add(cityBean1);
                // map的值为标题的下标
                map_IsHead.put(cityBean1.getHeadChar(),list_show.size() - 1);
            }
            list_show.add(cityBean);
        }
        listView.setAdapter(adapter);
        edit_search.setText("");
    }

    //修改联系人对话框
    public void custom_view_change() {
        TableLayout add_word_form = (TableLayout) getLayoutInflater()
                .inflate(R.layout.change_contact, null);
        dialogBuilder = new AlertDialog.Builder(this);
        alertDialog = dialogBuilder
                // 设置对话框标题
                .setTitle("添加联系人")
                // 设置对话框显示的View对象
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
                        EditText name_input = (EditText)alertDialog.findViewById(R.id.name);
                        //Toast.makeText(mContext, "点击了确定按钮", Toast.LENGTH_SHORT).show();

                        EditText email_input = (EditText) alertDialog.findViewById(R.id.email);
                        EditText birth_input = (EditText) alertDialog.findViewById(R.id.birth);

                        ArrayList<EditText> ed = new ArrayList<EditText>();
                        ArrayList<Spinner> sp = new ArrayList<Spinner>();
                        EditText number_input_1 = (EditText)alertDialog.findViewById(R.id.number1);
                        Spinner sp_1 = (Spinner) alertDialog.findViewById(R.id.types1);
                        ed.add(number_input_1);
                        sp.add(sp_1);
                        EditText number_input_2 = (EditText)alertDialog.findViewById(R.id.number2);
                        Spinner sp_2 = (Spinner) alertDialog.findViewById(R.id.types2);
                        ed.add(number_input_2);
                        sp.add(sp_2);
                        EditText number_input_3 = (EditText)alertDialog.findViewById(R.id.number3);
                        Spinner sp_3 = (Spinner) alertDialog.findViewById(R.id.types3);
                        ed.add(number_input_3);
                        sp.add(sp_3);

                        ArrayList<PhoneNumber> numbers = new ArrayList<PhoneNumber>();
                        for (int i = 0; i < 3; i++){
                            String number = ed.get(i).getText().toString();
                            int index = sp.get(i).getSelectedItemPosition();
                            if (!number.equals("")){
                                PhoneNumber phoneNumber = new PhoneNumber();
                                phoneNumber.setNumber(number);
                                switch (index){
                                    case 0:
                                        phoneNumber.setType(PhoneNumber.PhoneType.Unknow);
                                        break;
                                    case 1:
                                        phoneNumber.setType(PhoneNumber.PhoneType.Home);
                                        break;
                                    case 2:
                                        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
                                        break;
                                    case 3:
                                        phoneNumber.setType(PhoneNumber.PhoneType.Company);
                                        break;
                                }
                                numbers.add(phoneNumber);
                            }
                        }

                        String name = name_input.getText().toString();
                        String email = email_input.getText().toString();
                        String birth = birth_input.getText().toString();

                        if("".equals(name)){
                            Toast.makeText(getBaseContext(), "名字没有填写,修改失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        /*if("".equals(email)){
                            Toast.makeText(getBaseContext(), "邮箱没有填写,添加失败", Toast.LENGTH_SHORT).show();
                            return;
                        }*/
                        /*if("".equals(birth)){
                            Toast.makeText(getBaseContext(), "生日没有填写,添加失败", Toast.LENGTH_SHORT).show();
                            return;
                        }*/
                        dbutil.DeleteContact(list_show.get(choose).get_id());
                        change_contact(name, numbers, email, birth);
                    }
                })
                .create();             // 创建AlertDialog对象
        alertDialog.show();

        EditText name_input = (EditText)alertDialog.findViewById(R.id.name);
        EditText email_input = (EditText) alertDialog.findViewById(R.id.email);
        EditText birth_input = (EditText) alertDialog.findViewById(R.id.birth);

        ArrayList<EditText> ed = new ArrayList<EditText>();
        ArrayList<Spinner> sp = new ArrayList<Spinner>();
        EditText number_input_1 = (EditText)alertDialog.findViewById(R.id.number1);
        Spinner sp_1 = (Spinner) alertDialog.findViewById(R.id.types1);
        ed.add(number_input_1);
        sp.add(sp_1);
        EditText number_input_2 = (EditText)alertDialog.findViewById(R.id.number2);
        Spinner sp_2 = (Spinner) alertDialog.findViewById(R.id.types2);
        ed.add(number_input_2);
        sp.add(sp_2);
        EditText number_input_3 = (EditText)alertDialog.findViewById(R.id.number3);
        Spinner sp_3 = (Spinner) alertDialog.findViewById(R.id.types3);
        ed.add(number_input_3);
        sp.add(sp_3);

        Contacts cur_contact = list_show.get(choose);
        ArrayList<PhoneNumber> cur_numbers = cur_contact.getNumbers();
        for (int i = 0; i < 3; i++){
            if (i < cur_numbers.size()){
                ed.get(i).setText(cur_numbers.get(i).getNumber());
                switch (cur_numbers.get(i).getType().getName()){
                    case "Unknow":
                        sp.get(i).setSelection(0, true);
                        break;
                    case "Home":
                        sp.get(i).setSelection(1, true);
                        break;
                    case "Mobile":
                        sp.get(i).setSelection(2, true);
                        break;
                    case "Company":
                        sp.get(i).setSelection(3, true);
                        break;
                }
            }
        }


        name_input.setText(cur_contact.getName());
        email_input.setText(cur_contact.getEmail());
        birth_input.setText(utils.formatDateBirth(cur_contact.getBirthday().getTime()));


        birth_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickDlg();
                }
            }
        });


    }

    protected void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                EditText birth_input = (EditText) alertDialog.findViewById(R.id.birth);
                //Toast.makeText(getBaseContext(), year + "-" + monthOfYear + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
                birth_input.setText((year + "-" + monthOfYear + "-" + dayOfMonth).toString());
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    public class MemberSortUtil implements Comparator<Contacts> {
        /**
         * 按拼音排序
         */
        @Override
        public int compare(Contacts lhs, Contacts rhs) {
            Comparator<Object> cmp = Collator
                    .getInstance(java.util.Locale.CHINA);
            return cmp.compare(lhs.getName_en(), rhs.getName_en());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.call_log_button:
                intent = new Intent ();
                intent.putExtra("datareturn", 1);
                setResult(RESULT_OK, intent);
                this.finish();
                break;
            case R.id.reminder_button:
                intent = new Intent ();
                intent.putExtra("datareturn", 3);
                setResult(RESULT_OK, intent);
                this.finish();
                break;
            case R.id.add_contact:
                //Toast.makeText(this, "添加单词", Toast.LENGTH_SHORT).show();
                custom_view_add();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
