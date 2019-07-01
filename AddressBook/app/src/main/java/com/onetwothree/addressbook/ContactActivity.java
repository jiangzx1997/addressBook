package com.onetwothree.addressbook;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private ArrayList<PhoneBean> list_all;
    /**
     * 显示名字集合
     */
    private ArrayList<PhoneBean> list_show;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_search = (EditText) findViewById(R.id.edit_search);
        listView = (PinnedSectionListView) findViewById(R.id.phone_listview);
        letterIndexView = (LetterIndexView) findViewById(R.id.phone_LetterIndexView);
        txt_center = (TextView) findViewById(R.id.phone_txt_center);

        initView();
        initData();
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
                        PhoneBean bean = list_all.get(i);
                        //中文字符匹配首字母和英文字符匹配首字母
                        if (!map_IsHead.containsKey(bean.getHeadChar())) {// 如果不包含就添加一个标题
                            PhoneBean bean1 = new PhoneBean();
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
                        PhoneBean bean = list_all.get(i);
                        //中文字符匹配首字母和英文字符匹配首字母
                        if (bean.getName().indexOf(search) != -1|| bean.getName_en().indexOf(search) != -1) {
                            if (!map_IsHead.containsKey(bean.getHeadChar())) {// 如果不包含就添加一个标题
                                PhoneBean bean1 = new PhoneBean();
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
                    Toast.makeText(ContactActivity.this,list_show.get(i).getName(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // 设置标题部分有阴影
        // listView.setShadowVisible(true);
    }

    protected void initData() {
        list_all = new ArrayList<PhoneBean>();
        list_show = new ArrayList<PhoneBean>();
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

            for (int i = 0; i < str.length; i++) {
                PhoneBean cityBean = new PhoneBean();
                cityBean.setName(str[i]);
                list_all.add(cityBean);
            }
            //按拼音排序
            MemberSortUtil sortUtil = new MemberSortUtil();
            Collections.sort(list_all, sortUtil);

            // 初始化数据，顺便放入把标题放入map集合
            for (int i = 0; i < list_all.size(); i++) {
                PhoneBean cityBean = list_all.get(i);
                if (!map_IsHead.containsKey(cityBean.getHeadChar())) {// 如果不包含就添加一个标题
                    PhoneBean cityBean1 = new PhoneBean();
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

    public class MemberSortUtil implements Comparator<PhoneBean> {
        /**
         * 按拼音排序
         */
        @Override
        public int compare(PhoneBean lhs, PhoneBean rhs) {
            Comparator<Object> cmp = Collator
                    .getInstance(java.util.Locale.CHINA);
            return cmp.compare(lhs.getName_en(), rhs.getName_en());
        }
    }

}
