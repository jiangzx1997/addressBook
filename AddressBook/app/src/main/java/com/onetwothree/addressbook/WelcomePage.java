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
        startActivityForResult(new Intent(WelcomePage.this, CallLog.class), 1);
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
                startActivityForResult(new Intent(WelcomePage.this, CallLog.class), 1);
                break;
            case R.id.contact_button:
                startActivityForResult(new Intent(WelcomePage.this, ContactActivity.class), 1);
                break;
            case R.id.reminder_button:
                startActivityForResult(new Intent(WelcomePage.this, Reminder.class), 1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    int ReturnData = data.getIntExtra("datareturn", 1);
                    switch (ReturnData) {
                        case 1:
                            startActivityForResult(new Intent(WelcomePage.this, CallLog.class), 1);
                            break;
                        case 2:
                            startActivityForResult(new Intent(WelcomePage.this, ContactActivity.class), 1);
                            break;
                        case 3:
                            startActivityForResult(new Intent(WelcomePage.this, Reminder.class), 1);
                            break;
                        default:
                            break;
                    }
                }
        }
    }
}
