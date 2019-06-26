package com.onetwothree.addressbook;

import java.util.Date;

/**
 * Created by liyang on 2019/6/24.
 */

public class Remind {
    private int id;
    private Date date;
    private String Note;
    private int contact_id;

    public Remind() {  }


    public int getId() { return id; }
    public Date getDate() { return date; }
    public String getNote() { return Note; }
    public int getContact_id() { return contact_id; }

    public void setId(int id) {
        this.id = id;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setNote(String note) {
        this.Note = note;
    }
    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

}
