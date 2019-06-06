package com.example.runninglogs;

import java.util.Comparator;
import java.util.Date;

public class Run implements java.io.Serializable {
    private String title;
    private Date date;
    private String condition;
    private String humidity;
    private String temp;
    private String wind;
    private double distance;
    private RunLength runLength;
    private String notes;
    private String pace;

    public static final Comparator<Run> BY_TIME = new byTime();
    public static final Comparator<Run> BY_DISTANCE = new byDistance();

    public Run(String title, Date date, String condition, String humidity, String temp, String wind, double distance, RunLength runLength, String pace, String notes) {
        this.title = title;
        this.date = date;
        this.condition = condition;
        this.humidity = humidity;
        this.temp = temp;
        this.wind = wind;
        this.distance = distance;
        this.runLength = runLength;
        this.notes = notes;
        this.pace = pace;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemp() {
        return temp;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public RunLength getRunLength() {
        return runLength;
    }

    public void setRunLength(RunLength runLength) {
        this.runLength = runLength;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private static class byTime implements Comparator<Run> {

        @Override
        public int compare(Run o1, Run o2) {
            if (o1.getRunLength().compareTo(o2.getRunLength()) < 0) {
                return 1;
            } else if (o1.getRunLength().compareTo(o2.getRunLength()) > 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private static class byDistance implements Comparator<Run> {

        @Override
        public int compare(Run o1, Run o2) {
            if (o1.getDistance()<o2.getDistance()) {
                return 1;
            } else if (o1.getDistance()>o2.getDistance()) {
                return -1;
            } else {
                return 0;
            }
        }
    }


}