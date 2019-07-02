package com.onetwothree.addressbook;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Jiang Zixiao on 2019/6/28.
 */

public class WelcomePage extends AppCompatActivity {
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("通讯录");
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call_log_button:
                startActivity(new Intent(WelcomePage.this, CallLog.class));
                break;
            case R.id.contact_button:
                Toast.makeText(this, "contact", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WelcomePage.this, ContactActivity.class));
                break;
            case R.id.reminder_button:
                startActivity(new Intent(WelcomePage.this, Reminder.class));
                Toast.makeText(this, "reminder", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
