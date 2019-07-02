package com.onetwothree.addressbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by liyang21 on 2019/5/26.
 */

public class DbOpenHandler extends SQLiteOpenHelper {

    private final String TAG = "MySQLiteOpenHelper";

    public DbOpenHandler(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int dbVersion) {
        super(context, dbName, factory, dbVersion);
        Log.d(TAG, "MySQLiteOpenHelper");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL("CREATE TABLE PhoneNumber(_id integer primary key autoincrement, number varchar(20), type varchar(20))");
        Log.d(TAG, "Create table PhoneNumber");
        db.execSQL("CREATE TABLE Contact(_id integer primary key autoincrement, name varchar(20), email varchar(20), birthday DATE)");
        Log.d(TAG, "Create table Contact");
        db.execSQL("CREATE TABLE Phone_Contact(phone_id integer, contact_id integer, PRIMARY KEY (phone_id, contact_id))");
        Log.d(TAG, "Create table Phone_Contact");
        db.execSQL("CREATE TABLE Remind(_id integer primary key autoincrement, contact_id integer, time DATE, note TEXT)");
        Log.d(TAG, "Create table Remind");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
    }
}
