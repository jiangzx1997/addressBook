package com.onetwothree.addressbook;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by liyang21 on 2019/6/24.
 */

public class Contacts {
    private String headChar;
    private String name_en;
    private int type;


    private int _id;
    private String Name;
    private String email;
    private Date birthday;
    public String getHeadChar() {
        return headChar;
    }

    ArrayList<PhoneNumber> Numbers;

    Contacts () {
        this.Numbers = new ArrayList<PhoneNumber>();
    }
    Contacts (String Name) {
        setName(Name);
        this.Numbers = new ArrayList<PhoneNumber>();
    }

    public String getEmail() { return this.email; }
    public Date getBirthday() { return this.birthday; }
    public ArrayList<PhoneNumber> getNumbers() {
        return this.Numbers;
    }
    public String getName() {
        return this.Name;
    }
    public int get_id() { return _id; }


    public int getType() {
        return type;
    }
    public String getName_en() {
        return name_en;
    }

    public String getNumbersString() {
        StringBuilder tmp = new StringBuilder("");
        for (PhoneNumber s: Numbers) {
            tmp.append(s.getType().getName() + ":" + s.getNumber()+"\n");
        }
        tmp.deleteCharAt(tmp.length()-1);
        return tmp.toString();
    }

    public void setType(int type) {
        this.type = type;
    }


    public boolean hasNumber(String Number) {
        for (int i = 0; i < Numbers.size(); ++ i) {
            if (Numbers.get(i).equals(Number)) {
                return true;
            }
        }
        return false;
    }
    public PhoneNumber.PhoneType getNumberType(String Number) {
        // find a phnoe number type by number.
        for (int i = 0; i < Numbers.size(); ++ i) {
            if (Numbers.get(i).getNumber().equals(Number)) {
                return Numbers.get(i).getType();
            }
        }
        return PhoneNumber.PhoneType.Unknow;
    }

    public void set_id(int _id) { this._id = _id; }

    public void setName(String name) {
        this.Name = name;
        name_en = getPinYin(Name);//获取字母名称
        name_en = name_en.toUpperCase();//把小写字母换成大写字母
        if (!TextUtils.isEmpty(name_en)) {
            char head = name_en.charAt(0);
            if (head < 'A' || head > 'Z') {
                head = '#';
            }
            headChar = head + "";
        }
    }
    public void setEmail(String email) { this.email = email; }
    public void setBirthday(Long birthday) { this.birthday= new Date(birthday); }
    public void setNumbers(ArrayList<PhoneNumber> x) {
        for (PhoneNumber s: x)
            addNumber(s);
    }
    public void addNumber(PhoneNumber Number) {
        this.Numbers.add(Number);
    }
    public boolean delNumber(String Number) {
        // Delete a phone number by number. Return true if successful, otherwise false.
        for (int i = 0; i < Numbers.size(); ++ i) {
            if (Numbers.get(i).getNumber().equals(Number)) {
                Numbers.remove(i);
                return true;
            }
        }
        return false;
    }
    /**
     * 汉字转换拼音，字母原样返回，都转换为小写
     */
    public String getPinYin(String input) {
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(input);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (token.type == HanziToPinyin.Token.PINYIN) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        return sb.toString().toLowerCase();
    }
}