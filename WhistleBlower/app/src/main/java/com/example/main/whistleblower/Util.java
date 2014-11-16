package com.example.main.whistleblower;

/**
 * Created by Main on 11/15/14.
 */
public class Util {

    public static final String SEPARATOR = ":";
    public static final String TIME_FORMAT = "yyyy MM dd HH:mm:ss";

    public static double convertToLatitude(String location) {
        String[] coordinates = location.split(SEPARATOR);
        double latitude = Double.valueOf(coordinates[1]);
        return latitude;
    }

    public static double convertToLongtitude(String location) {
        String[] coordinates = location.split(SEPARATOR);
        double longitude = Double.valueOf(coordinates[0]);
        return longitude;
    }

    public static String appendCoordinates(double longitude, double latitude) {
        return longitude + SEPARATOR + latitude;
    }
}
