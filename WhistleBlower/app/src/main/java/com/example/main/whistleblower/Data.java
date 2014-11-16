package com.example.main.whistleblower;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by daniel on 11/15/14.
 */
public class Data implements Comparable<Data> {

    private String ID;
    private String TimeStamp;
    private String Message;
    private String Location;
    private String Category;
    private String Type;
    private String Sub_Type;

    public Data(String id, String timestamp, String msg,
                String mLocation, String mCategory, String mType, String mSub_Type){
        ID = id;
        TimeStamp = convertTime(Long.parseLong(timestamp));
        Message = msg;
        Location = mLocation;
        Category = mCategory;
        Type = mType;
        Sub_Type = mSub_Type;

    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    public String getID() {
        return ID;
    }

    public void setID(int i){
        ID = Integer.toString(i);
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public String getMessage() {
        return Message;
    }


    // Order by timestamp
    @Override
    public int compareTo(Data another) {
        return -(this.TimeStamp.compareTo(another.TimeStamp));
    }
}
