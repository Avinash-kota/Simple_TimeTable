package com.kota.simpletimetable;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Comparator;

public class Slot {

    public String title;
    public String code;
    public String room;

    public int STime_H;
    public int STime_M;
    public int ETime_H;
    public int ETime_M;

    public boolean edit_mode;

    public int getSorterTime() {
        return sorterTime;
    }

    public int sorterTime;

    public Slot() {
    }

    public Slot(String title, String code, String room, int STime_H, int STime_M, int ETime_H, int ETime_M) {
        this.title=title;
        this.code=code;
        this.room=room;
        this.STime_H = STime_H;
        this.STime_M = STime_M;
        this.ETime_H = ETime_H;
        this.ETime_M = ETime_M;
        sorterTime = STime_H*60+STime_M;
    }

    public boolean isEdit_mode() {
        return edit_mode;
    }

    public void setEdit_mode(boolean edit_mode) {
        this.edit_mode = edit_mode;
    }

    public String getTime(){
        String time;
        Calendar calendar = Calendar.getInstance();
        calendar.set(0,0,0,STime_H,STime_M);
        time = DateFormat.format("hh:mm aa",calendar).toString()+"--";
        calendar.set(0,0,0,ETime_H,ETime_M);
        time = time + DateFormat.format("hh:mm aa",calendar).toString();

        return time;
    }

    public static Comparator<Slot> sortList = new Comparator<Slot>() {

        public int compare(Slot s1, Slot s2) {

            int rollno1 = s1.getSorterTime();
            int rollno2 = s2.getSorterTime();

            /*For ascending order*/
            return rollno1-rollno2;

            /*For descending order*/
            //rollno2-rollno1;
        }};

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

}
