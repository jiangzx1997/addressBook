package com.onetwothree.addressbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.provider.CallLog;

import java.security.cert.PolicyNode;
import java.util.ArrayList;

/**
 * Created by liyang on 2019/6/24.
 */

public class activity_main extends AppCompatActivity {

    private final String TAG = "Activity_Main";
    private CallRecordUtil callRecord;
    private Remind remindRecord;
    private DbOpenHandler helper;
    private Dbutil dbutil;
    private ActionBar actionBar;

    static private int permissions_code = 100;
    static private String[] permissions = {Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.CALL_PHONE};


    private void PrepareTestData() {

        dbutil.ClearDb();
        PhoneNumber phoneNumber;
        Contacts contacts;
        CallRecord callRecord;

        contacts = new Contacts();
        contacts.setName("Alice");
        contacts.setBirthday(utils.GenerateDate(1996, 7, 3, 0, 0, 0).getTime());
        contacts.setEmail("alice@test.com");
        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("18858559271");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("88654517");
        phoneNumber.setType(PhoneNumber.PhoneType.Home);
        contacts.addNumber(phoneNumber);
        dbutil.addContact(contacts);


        contacts = new Contacts();
        contacts.setName("Bob");
        contacts.setBirthday(utils.GenerateDate(1998, 4,3, 0, 0, 0).getTime());
        contacts.setEmail("1602546812@qq.com");
        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("18605855921");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("95412549");
        phoneNumber.setType(PhoneNumber.PhoneType.Company);
        contacts.addNumber(phoneNumber);

        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("84123647");
        phoneNumber.setType(PhoneNumber.PhoneType.Home);
        contacts.addNumber(phoneNumber);

        dbutil.addContact(contacts);


        contacts = new Contacts();
        contacts.setName("Cared");
        contacts.setBirthday(utils.GenerateDate(1994, 7,3, 0, 0, 0).getTime());
        contacts.setEmail("cared123456@163.com");
        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("188543135154");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("85442649");
        phoneNumber.setType(PhoneNumber.PhoneType.Company);
        contacts.addNumber(phoneNumber);

        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("74542478");
        phoneNumber.setType(PhoneNumber.PhoneType.Home);
        contacts.addNumber(phoneNumber);

        dbutil.addContact(contacts);

        contacts = new Contacts();
        contacts.setName("Riker");
        contacts.setBirthday(utils.GenerateDate(1996, 3,12, 0, 0, 0).getTime());
        contacts.setEmail("Riker1452@123.com");
        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("15936214258");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        dbutil.addContact(contacts);

        contacts = new Contacts();
        contacts.setName("Mikoo");
        contacts.setBirthday(utils.GenerateDate(1999, 1,5, 0, 0, 0).getTime());
        contacts.setEmail("mikomiko@gmail.com");
        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("16452143528");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        dbutil.addContact(contacts);

        contacts = new Contacts();
        contacts.setName("Zerj");
        contacts.setBirthday(utils.GenerateDate(1996, 5,2, 0, 0, 0).getTime());
        contacts.setEmail("Zzzzzzzzzz@gmail.com");
        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("18963211425");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("18920124158");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        dbutil.addContact(contacts);

        contacts = new Contacts();
        contacts.setName("Parere");
        contacts.setBirthday(utils.GenerateDate(1998, 11,4, 0, 0, 0).getTime());
        contacts.setEmail("Prerererere@gmail.com");
        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("16895214255");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("86241254");
        phoneNumber.setType(PhoneNumber.PhoneType.Home);
        contacts.addNumber(phoneNumber);

        dbutil.addContact(contacts);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2018, 2, 23, 15, 4, 10).getTime());
        callRecord.setNumber("18858559271");
        callRecord.setName("Alice");
        callRecord.setTime(30);
        callRecord.setType(CallLog.Calls.INCOMING_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2018, 4, 1, 12, 45, 22).getTime());
        callRecord.setNumber("18605855921");
        callRecord.setName("Bob");
        callRecord.setTime(256);
        callRecord.setType(CallLog.Calls.INCOMING_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 5, 6, 10, 31, 14).getTime());
        callRecord.setNumber("88654517");
        callRecord.setName("Bob");
        callRecord.setTime(54);
        callRecord.setType(CallLog.Calls.OUTGOING_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 6, 25, 23, 45, 11).getTime());
        callRecord.setNumber("88654517");
        callRecord.setName("Bob");
        callRecord.setTime(95);
        callRecord.setType(CallLog.Calls.MISSED_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 7, 3, 17, 44, 3).getTime());
        callRecord.setNumber("16452143528");
        callRecord.setName("Mikoo");
        callRecord.setTime(600);
        callRecord.setType(CallLog.Calls.OUTGOING_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 7, 1, 15, 22, 12).getTime());
        callRecord.setNumber("18920124158");
        callRecord.setName("Zerj");
        callRecord.setTime(55);
        callRecord.setType(CallLog.Calls.INCOMING_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 7, 2, 23, 35, 8).getTime());
        callRecord.setNumber("85442649");
        callRecord.setName("Cared");
        callRecord.setTime(20);
        callRecord.setType(CallLog.Calls.MISSED_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 7, 2, 23, 38, 12).getTime());
        callRecord.setNumber("85442649");
        callRecord.setName("Cared");
        callRecord.setTime(15);
        callRecord.setType(CallLog.Calls.MISSED_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 7, 2, 23, 40, 4).getTime());
        callRecord.setNumber("85442649");
        callRecord.setName("Cared");
        callRecord.setTime(16);
        callRecord.setType(CallLog.Calls.MISSED_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 7, 2, 23, 42, 35).getTime());
        callRecord.setNumber("85442649");
        callRecord.setName("Cared");
        callRecord.setTime(18);
        callRecord.setType(CallLog.Calls.MISSED_TYPE);
        dbutil.AddCallRecord(callRecord);

        callRecord = new CallRecord();
        callRecord.setDate(utils.GenerateDate(2019, 7, 3, 12, 12, 10).getTime());
        callRecord.setNumber("18963211425");
        callRecord.setName("Zerj");
        callRecord.setTime(55);
        callRecord.setType(CallLog.Calls.OUTGOING_TYPE);
        dbutil.AddCallRecord(callRecord);

    }

    void showAllContacts() {
        ArrayList<Contacts> contacts = dbutil.getAllContacts();
        for (int i = 0; i < contacts.size(); ++ i) {
            Contacts contact = contacts.get(i);
            Log.v(TAG, "Get contact:" + contact.getName() + ":");
            Log.v(TAG, "    email:" + contact.getEmail());
            if (utils.CheckEmailFormat(contact.getEmail()))
                Log.v(TAG, "    email format check pass");
            Log.v(TAG, "    birthday:" + utils.formatDate(contact.getBirthday().getTime()));
            ArrayList<PhoneNumber> numbers = contact.getNumbers();
            for (int j = 0; j < numbers.size(); ++ j) {
                PhoneNumber number = numbers.get(j);
                Log.v(TAG, "    " + number.getNumber() + " type:" + number.getType());
            }
        }
    }

    void showAllCallRecord() {
        ArrayList<CallRecord> records = dbutil.getAllCallRecord();
        for (int i = 0; i < records.size(); ++ i) {
            Log.v(TAG, "got " + records.get(i).getDebugString());
        }
    }

    void debugTest() {
        showAllCallRecord();
//        Log.v(TAG, "now delete the first Call Record");
//        ArrayList<CallRecord> records = dbutil.getAllCallRecord();
//        CallRecord callRecord = records.get(0);
//        dbutil.DeleteCallRecord(callRecord.getDate().getTime());
//        showAllCallRecord();
        showAllContacts();
        Log.v(TAG, "now add a PhoneNumber for the first contact");
        ArrayList<Contacts> contacts = dbutil.getAllContacts();
        Contacts contact = contacts.get(0);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setType(PhoneNumber.PhoneType.Unknow);
        phoneNumber.setNumber("233333333");
        dbutil.addPhoneNumber(contact.get_id(), phoneNumber);

        showAllContacts();

        Contacts contacts1 = dbutil.getContactByNumber("18858559271");
        Log.v(TAG, "find contact name = " + contacts1.getName() + " id= " + contacts1.get_id());
        ArrayList<PhoneNumber> numbers = contacts1.getNumbers();
        for (int j = 0; j < numbers.size(); ++ j) {
            PhoneNumber number = numbers.get(j);
            Log.v(TAG, "    " + number.getNumber() + " type:" + number.getType());
        }


        Remind remind = new Remind();
        remind.setContact_id(contact.get_id());
        remind.setNote("for test");
        remind.setDate(utils.GenerateDate(2019, 6, 28, 6, 0, 0));

        dbutil.AddRemind(remind);

        ArrayList<Remind> reminds = dbutil.GetAllRemind();
        for (int i = 0; i < reminds.size(); ++ i) {
            remind = reminds.get(i);
            Log.v(TAG, "get record contact_id = " + String.valueOf(remind.getContact_id()) + " data: " + remind.getDate());
        }


        Log.v(TAG, "now delete fisrt contact");
        dbutil.DeleteContact(contact.get_id());
        showAllContacts();

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("权限获取");

        helper = new DbOpenHandler(this, "AddressBook.db", null, 1);
        callRecord = new CallRecordUtil(this, this);

        Dbutil.init(helper, callRecord);
        dbutil = Dbutil.getInstance();

        // for debug dbutil.
        dbutil.DropTable();
        Log.v(TAG, "request Permissions");
        ActivityCompat.requestPermissions(this, permissions, permissions_code);
//        showAllCallRecord();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PrepareTestData();
        //showAllCallRecord();
        showAllContacts();

        Intent intent = new Intent(activity_main.this, WelcomePage.class);
        startActivity(intent);
    }
}
