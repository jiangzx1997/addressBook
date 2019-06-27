package com.example.calllog;

import android.provider.CallLog;

import java.util.Date;

/**
 * Created by liyang21 on 2019/6/24.
 */

public class CallRecord {

    private String Name;
    private String Number; // (TODO) change this to PhoneNumber
    private java.util.Date Date;
    private long Time;
    private int type;
    private String Location; //(TODO)

    CallRecord() { }

    public String getName() {
        return this.Name;
    }
    public String getNumber() {
        return this.Number;
    }
    public long getTime() {
        return this.Time;
    }
    public java.util.Date getDate() {
        return this.Date;
    }
    public String getLocation() {
        return this.Location;
    }
    public String getType(){return this.changeType(type);}

    public void setName(String Name) {
        if (Name != null) {
            this.Name = Name;
        } else {
            this.Name = "UnKnow";
        }
    }
    public void setNumber(String Number) { this.Number = Number; }
    public void setTime(long Time) {
        this.Time = Time;
    }
    public void setType(int type) { this.type = type; }
    public void setDate(long Time) { this.Date = new Date(Time); }
    public void setLocation(String Location) {
        this.Location = Location;
    }

    private String changeType(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "呼入";
            case CallLog.Calls.OUTGOING_TYPE:
                return "呼出";
            case CallLog.Calls.MISSED_TYPE:
                return "未接";
            case CallLog.Calls.REJECTED_TYPE:
                return "拒绝";
            default:
                return "UnKnow";
        }
    }

    public String getDebugString() {
        String Info = new String();
        Info = Info + "号码：" + Number + "\n"
                + "姓名：" + Name + "\n"
                + "类型：" + changeType(type) + "\n"
                + "日期：" + utils.formatDate(Date.getTime()) + "\n"
                + "时间：" + utils.formatDuration(Time) + "\n"
                + "地址：" + Location + "\n\n";
        return Info;
    }

}
