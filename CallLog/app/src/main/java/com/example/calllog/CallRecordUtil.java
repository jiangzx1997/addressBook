package com.example.calllog;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by liyang21 on 2019/6/24.
 *
 *      (TODO) get location information
 */

public class CallRecordUtil {

    private final String TAG = "ContactMsgUtils";
    private Context context;
    private Activity activity;

    public CallRecordUtil(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    // 查询所有记录
    ArrayList<CallRecord> GetCallsInPhone() {
        String debug = new String();
        ArrayList<CallRecord> records = new ArrayList<CallRecord>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.READ_CALL_LOG}, 100);
            return records;
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.CACHED_FORMATTED_NUMBER, CallLog.Calls.CACHED_MATCHED_NUMBER,
                        CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION,
                        CallLog.Calls.NUMBER}, null, null, "date DESC");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    CallRecord record = new CallRecord();
                    if (cursor.getString(1) != null) {
                        record.setNumber(cursor.getString(1));
                    } else if (cursor.getString(0) != null) {
                        record.setNumber(cursor.getString(0));
                    } else {
                        record.setNumber(cursor.getString(6));
                    }
                    record.setName(cursor.getString(2));
                    record.setType(cursor.getInt(3));
                    record.setDate(cursor.getLong(4));
                    record.setTime(cursor.getLong(5));
                    records.add(record);

                    Log.v(TAG, record.getName() + record.getNumber());

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return records;
    }

    // 根据 date 删除指定的通话记录
    public void deleteCallLog(Long date) {
        ContentResolver resolver = context.getContentResolver();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.WRITE_CALL_LOG}, 100);
            Log.v(TAG, "not have permission.");
            return;
        }

        // (TODO) remove
        /*if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.READ_CALL_LOG}, 100);
            return;
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.DATE}, null, null, null);
        while (cursor.moveToNext()) {
            Log.v(TAG, String.valueOf(cursor.getLong(0)));
        }*/

        int result = resolver.delete(CallLog.Calls.CONTENT_URI, "date=?", new String[] {String.valueOf(date)});
        if (result > 0) {
            Log.d(TAG, "deleted success:" + utils.formatDate(date));
        } else {
            Log.d(TAG, "deleted fail:" + String.valueOf(date));
        }
    }

}
