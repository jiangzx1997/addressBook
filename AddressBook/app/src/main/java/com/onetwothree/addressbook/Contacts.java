package com.onetwothree.addressbook;

import java.util.ArrayList;

/**
 * Created by liyang21 on 2019/6/24.
 */

public class Contacts {

    private int _id;
    private String Name;
    ArrayList<PhoneNumber> Numbers;

    Contacts () {
        this.Numbers = new ArrayList<PhoneNumber>();
    }
    Contacts (String Name) {
        this.Name = Name;
        this.Numbers = new ArrayList<PhoneNumber>();
    }

    public ArrayList<PhoneNumber> getNumbers() {
        return this.Numbers;
    }
    public String getName() {
        return this.Name;
    }
    public int get_id() { return _id; }
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
    public void setName(String Name) {
        this.Name = Name;
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
}