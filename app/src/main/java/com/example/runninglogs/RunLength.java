package com.example.runninglogs;

public class RunLength implements Comparable<RunLength> {
    private int mHour;
    private int mMinute;
    private double mSecond;

    public RunLength(int hour, int minute, double second) {
        mHour = hour;
        mMinute = minute;
        mSecond = second;
    }

    public RunLength(int minute, double second) {
        mHour = 0;
        mMinute = minute;
        mSecond = second;
    }

    public RunLength(double second) {
        mHour = 0;
        mMinute = 0;
        mSecond = second;
    }

    public int getmHour() {
        return mHour;
    }

    public int getmMinute() {
        return mMinute;
    }

    public void setmMinute(int mMinute) {
        this.mMinute = mMinute;
    }

    public double getmSecond() {
        return mSecond;
    }

    public void setmSecond(double mSecond) {
        this.mSecond = mSecond;
    }

    public void setmHour(int mHour) {
        this.mHour = mHour;
    }

    @Override
    public int compareTo(RunLength rl) {
        if (mHour == rl.getmHour()) {
            if (mMinute == rl.getmMinute()) {
                if (mSecond == rl.getmSecond()) {
                    return 0;
                } else if (mSecond < rl.getmSecond()) {
                    return -1;
                } else if (mSecond > rl.getmSecond()) {
                    return 1;
                }
            } else if (mMinute < rl.getmMinute()) {
                return -1;
            } else if (mMinute > rl.getmMinute()) {
                return 1;
            }
        } else if (mHour < rl.getmHour()) {
            return -1;
        } else if (mHour > rl.getmHour()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString(){
        String toBeReturned = "";
        if(mHour==0){
            toBeReturned+="";
        }
        else{
            toBeReturned+=""+mHour+":";
        }
        if(mMinute==0){
            toBeReturned+="00:";
        }
        else {
            toBeReturned+=""+mMinute+":";
        }
        if(mSecond==0){
            toBeReturned+="00.00";
        }
        else toBeReturned+=""+mSecond;
        return toBeReturned;
    }
}
