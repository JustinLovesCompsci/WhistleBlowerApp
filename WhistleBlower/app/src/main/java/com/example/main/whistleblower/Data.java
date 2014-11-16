package com.example.main.whistleblower;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by daniel on 11/15/14.
 */
public class Data implements Comparable<Data> {

    private String TimeStamp;
    private String Message;
    private String Location;
    private String Category;
    private String Type;
    private String Sub_Type;

    /**
     * Allowing empty constructor
     */
    public Data() {

    }

    public Data(String msg,   String timestamp, String mCategory,
                String mType, String mSub_Type, String mLocation) {
        Message = msg;
        TimeStamp = convertTime(Long.parseLong(timestamp));
        Category = mCategory;
        Type = mType;
        Sub_Type = mSub_Type;
        Location = mLocation;
    }

    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    // Order by timestamp
    @Override
    public int compareTo(Data d) {
        return -(this.TimeStamp.compareTo(d.TimeStamp));
    }

    public String getMessage() {
        return Message;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getSub_Type() {
        return Sub_Type;
    }

    public void setSub_Type(String sub_Type) {
        Sub_Type = sub_Type;
    }

}