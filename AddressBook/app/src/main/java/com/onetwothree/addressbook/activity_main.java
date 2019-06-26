package com.onetwothree.addressbook;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.security.cert.PolicyNode;
import java.util.ArrayList;

/**
 * Created by liyang on 2019/6/24.
 */

public class activity_main extends AppCompatActivity {

    private final String TAG = "Activity_Main";
    private CallRecordUtil callRecord;
    private DbOpenHandler helper;
    private Dbutil dbutil;


    private void addDebugData() {

        dbutil.ClearDb();

        PhoneNumber phoneNumber;
        Contacts contacts;

        contacts = new Contacts();
        contacts.setName("Liyang");
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
        contacts.setName("Liyang");
        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("12345789");
        phoneNumber.setType(PhoneNumber.PhoneType.Mobile);
        contacts.addNumber(phoneNumber);

        phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("223456789");
        phoneNumber.setType(PhoneNumber.PhoneType.Unknow);
        contacts.addNumber(phoneNumber);
        dbutil.addContact(contacts);
    }

    void showAllContacts() {
        ArrayList<Contacts> contacts = dbutil.getAllContacts();
        for (int i = 0; i < contacts.size(); ++ i) {
            Contacts contact = contacts.get(i);
            Log.v(TAG, "Get contact:" + contact.getName() + ":");
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
        addDebugData();
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
        helper = new DbOpenHandler(this, "AddressBook.db", null, 1);
        callRecord = new CallRecordUtil(this, this);
        dbutil = new Dbutil(helper, callRecord);

        // for debug dbutil.
         debugTest();
    }

}
