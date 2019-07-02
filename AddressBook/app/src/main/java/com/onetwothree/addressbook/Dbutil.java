package com.onetwothree.addressbook;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liyang21 on 2019/6/26.
 */

public class Dbutil {

    private final String TAG = "DbUtil";
    DbOpenHandler helper;
    CallRecordUtil callRecordUtil;
    static Dbutil instance;

    private Dbutil(DbOpenHandler helper, CallRecordUtil callRecordUtil) {
        this.helper = helper;
        this.callRecordUtil = callRecordUtil;
    }

    static public void init(DbOpenHandler helper, CallRecordUtil callRecordUtil) {
        instance = new Dbutil(helper, callRecordUtil);
    }

    static Dbutil getInstance() {
        return instance;
    }

    public void DropTable() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("drop table Contact");
        db.execSQL("CREATE TABLE Contact(_id integer primary key autoincrement, name varchar(20), email varchar(20), birthday DATE)");
        db.close();
    }

    public void DeleteCallRecord(Long date) {
        callRecordUtil.deleteCallLog(date);
    }

    public void AddCallRecord(CallRecord callRecord) { callRecordUtil.insertCallLog(callRecord); }

    public void ClearDb() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from Phone_Contact");
        db.execSQL("delete from PhoneNumber");
        db.execSQL("delete from Contact");
        db.execSQL("delete from Remind");
        callRecordUtil.clear();
        db.close();
    }

    // 添加联系人
    public void addContact(Contacts contact) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        // 1. 添加联系人到 Contact
        db.execSQL("insert into Contact(name, email, birthday) values(?,?,?)",
                new String[]{contact.getName(), contact.getEmail(), String.valueOf(contact.getBirthday().getTime())});
        cursor = db.rawQuery("select last_insert_rowid() from Contact", null);
        int contact_id = -1;
        if (cursor.moveToFirst()) {
            contact_id = cursor.getInt(0);
            Log.v(TAG, "contact id = " + String.valueOf(contact_id));
        }

        // 2. 添加电话号码到 PhoneNumber
        ArrayList<PhoneNumber> numbers = contact.getNumbers();
        for (int i = 0; i < numbers.size(); ++ i) {
            String number = numbers.get(i).getNumber();
            String type = numbers.get(i).getType().getName();
            db.execSQL("insert into PhoneNumber(number, type) values(?,?)", new String[]{number, type});

            // 3. 获取对应的id，并添加进入 Phone_Contact
            cursor = db.rawQuery("select last_insert_rowid() from PhoneNumber", null);
            int phone_id = -1;
            if (cursor.moveToFirst()) {
                phone_id = cursor.getInt(0);
            }
            db.execSQL("insert into Phone_Contact(phone_id, contact_id) values(?,?)",
                    new String[]{String.valueOf(phone_id), String.valueOf(contact_id)});
        }
        db.close();
    }


    // 为指定 id 的联系人添加号码
    public void addPhoneNumber(int contact_id, PhoneNumber phoneNumber) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        String number = phoneNumber.getNumber();
        String type = phoneNumber.getType().getName();
        db.execSQL("insert into PhoneNumber(number, type) values(?,?)", new String[]{number, type});

        // 3. 获取对应的id，并添加进入 Phone_Contact
        cursor = db.rawQuery("select last_insert_rowid() from PhoneNumber", null);
        int phone_id = -1;
        if (cursor.moveToFirst()) {
            phone_id = cursor.getInt(0);
        }
        db.execSQL("insert into Phone_Contact(phone_id, contact_id) values(?,?)",
                new String[]{String.valueOf(phone_id), String.valueOf(contact_id)});
    }

    // 删除指定 id 的联系人 以及所有它关联的电话
    public void DeleteContact(Integer id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        ArrayList<Integer> phone_id = new ArrayList<Integer>();
        cursor = db.rawQuery("select phone_id from Phone_Contact where contact_id=?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(cursor.getColumnIndex("phone_id"));
            phone_id.add(id);
        }

        db.delete("Phone_Contact", "contact_id=?", new String[]{String.valueOf(id)});
        for (int i = 0; i < phone_id.size(); ++ i) {
            db.delete("PhoneNumber", "_id=?", new String[]{String.valueOf(phone_id.get(i))});
        }
        db.delete("Contact", "_id=?",new String[]{String.valueOf(id)});
        db.close();
    }

    // 给出所有的联系人
    public ArrayList<Contacts> getAllContacts() {

        ArrayList<Contacts> Contacts = new ArrayList<Contacts>();
        SQLiteDatabase db = helper.getWritableDatabase();

        // 1. get all _id, name from Contact
        Cursor cursor = db.rawQuery("select * from Contact", null);
        HashMap<Integer, Contacts> contact_map = new HashMap<Integer, Contacts>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            Long birthday = cursor.getLong(cursor.getColumnIndex("birthday"));
            Contacts contact = new Contacts();
            contact.setName(name);
            contact.set_id(id);
            contact.setEmail(email);
            contact.setBirthday(birthday);
            contact_map.put(id, contact);
        }

        // 2. get all _id, number, type from  PhoneNumber
        cursor = db.rawQuery("select * from PhoneNumber", null);
        HashMap<Integer, PhoneNumber> phone_map = new HashMap<Integer, PhoneNumber>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.set_id(id);
            phoneNumber.setNumber(number);
            phoneNumber.setType(PhoneNumber.PhoneType.valueOf(type));
            phone_map.put(id, phoneNumber);
        }

        // 3. get all contact_id phone_id from Phone_Contact
        cursor = db.rawQuery("select * from Phone_Contact", null);
        while (cursor.moveToNext()) {
            int contact_id = cursor.getInt(cursor.getColumnIndex("contact_id"));
            int phone_id = cursor.getInt(cursor.getColumnIndex("phone_id"));
            PhoneNumber phoneNumber = phone_map.get(phone_id);
            Contacts contact = contact_map.get(contact_id);
            contact.addNumber(phoneNumber);
            contact_map.put(contact_id, contact);
        }

        for (Map.Entry<Integer, Contacts> entry : contact_map.entrySet()) {
            Contacts.add(entry.getValue());
        }
        db.close();
        return Contacts;
    }

    // 根据id 返回对应的联系人，若未找到则返回一个姓名为 Unknow的联系人
    public Contacts GetContactById(int contact_id) {

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        Contacts contact = new Contacts();
        contact.setName("Unknow");

        contact.set_id(contact_id);
        cursor = db.rawQuery("select * from Contact where _id=?", new String[]{String.valueOf(contact_id)});
        if (cursor.moveToFirst()) {
            contact.setName(cursor.getString(cursor.getColumnIndex("name")));
            cursor = db.rawQuery("select * from Phone_Contact where contact_id=?", new String[]{String.valueOf(contact_id)});
            while (cursor.moveToNext()) {
                int phone_id1 = cursor.getInt(cursor.getColumnIndex("phone_id"));
                Cursor cursor1 = db.rawQuery("select * from PhoneNumber where _id=?", new String[]{String.valueOf(phone_id1)});
                cursor1.moveToFirst();
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.set_id(phone_id1);
                phoneNumber.setNumber(cursor1.getString(cursor1.getColumnIndex("number")));
                phoneNumber.setType(PhoneNumber.PhoneType.valueOf(cursor1.getString(cursor1.getColumnIndex("type"))));
                contact.addNumber(phoneNumber);
            }
        }
        db.close();
        return contact;
    }

    // 获取所有通话记录
    public ArrayList<CallRecord> getAllCallRecord() {
        return callRecordUtil.GetCallsInPhone();
    }

    // 根据电话号码给出联系人, 若没找到，则返回姓名为"Unknow"的联系人
    public Contacts getContactByNumber(String Number) {
        String realNumber = utils.PhoneNumberDeformat(Number);
        SQLiteDatabase db = helper.getWritableDatabase();

        Log.v(TAG, "get real number = " + realNumber);
        int contact_id = -1;
        Cursor cursor = db.rawQuery("select * from PhoneNumber where number=?", new String[]{realNumber});
        if (cursor.moveToFirst()) {
            int phone_id = cursor.getInt(cursor.getColumnIndex("_id"));
            Log.v(TAG, "get phone_id = " + String.valueOf(phone_id));
            cursor = db.rawQuery("select * from Phone_Contact where phone_id=?", new String[]{String.valueOf(phone_id)});
            cursor.moveToFirst();
            contact_id = cursor.getInt(cursor.getColumnIndex("contact_id"));
        }
        db.close();
        return GetContactById(contact_id);
    }

    // 添加提醒
    public void AddRemind(Remind remind) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into Remind(contact_id, time, note) values(?, ?, ?)",
                new String[]{String.valueOf(remind.getContact_id()), String.valueOf(remind.getDate().getTime()), String.valueOf(remind.getNote())});
        db.close();
    }


    // 获取所有提醒数据
    public ArrayList<Remind> GetAllRemind() {
        ArrayList<Remind> reminds = new ArrayList<Remind>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("select * from Remind", null);
        while (cursor.moveToNext()) {
            Remind remind = new Remind();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            Long date = cursor.getLong(cursor.getColumnIndex("time"));
            String note = cursor.getString(cursor.getColumnIndex("note"));
            int contact_id = cursor.getInt(cursor.getColumnIndex("contact_id"));

            remind.setId(id);
            remind.setDate(new Date(date));
            remind.setNote(note);
            remind.setContact_id(contact_id);

            reminds.add(remind);
        }
        db.close();
        return reminds;
    }

}
