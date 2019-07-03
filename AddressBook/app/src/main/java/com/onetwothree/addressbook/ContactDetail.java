package com.onetwothree.addressbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContactDetail extends AppCompatActivity {
    //控件
    TextView name;
    TextView number;
    TextView birth;
    TextView email;
    Button delete;
    //对话框
    AlertDialog alertDialog = null;
    AlertDialog.Builder dialogBuilder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("联系人");
        actionBar.setSubtitle("详细信息");
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        birth = (TextView) findViewById(R.id.birth);
        email = (TextView) findViewById(R.id.email);
        delete = (Button) findViewById(R.id.delete);
        //设置显示内容
        name.setText(getIntent().getStringExtra("name"));
        number.setText(getIntent().getStringExtra("number"));
        birth.setText(getIntent().getStringExtra("birth"));
        email.setText(getIntent().getStringExtra("email"));
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_view_delete();
            }
        });
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support_contact_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent re = new Intent();
                re.putExtra("delete", false);
                re.putExtra("position", getIntent().getStringExtra("position"));
                setResult(1, re);
                finish();
                break;
            case R.id.menu1:
                Toast.makeText(this, "扩展功能", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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
                        Intent re = new Intent();
                        re.putExtra("delete", true);
                        re.putExtra("position", getIntent().getStringExtra("position"));
                        setResult(1, re);
                        finish();
                    }
                })
                .create();             // 创建AlertDialog对象
        alertDialog.show();
    }
}
