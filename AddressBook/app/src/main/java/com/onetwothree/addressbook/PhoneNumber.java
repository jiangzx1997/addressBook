package com.onetwothree.addressbook;

/**
 * Created by liyang21 on 2019/6/24.
 */

public class PhoneNumber {

    public enum PhoneType {
        Home("Home"), Mobile("Mobile"), Company("Company"), Unknow("Unknow");

        private String name;

        private PhoneType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private int _id;
    private PhoneType type;
    private String Number;

    PhoneNumber () { }
    PhoneNumber (String Number, PhoneType type) {
        this.Number = Number;
        this.type = type;
    }

    public int get_id() { return this._id; }
    public String getNumber() {
        return this.Number;
    }
    public PhoneType getType() {
        return this.type;
    }
    public void set_id(int _id) { this._id = _id; }
    public void setNumber(String Number) {
        this.Number = Number;
    }
    public void setType(PhoneType type) {
        this.type = type;
    }

}
