package com.example.ondrejvane.zivnostnicek.model;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Event {

    private String title;
    private Calendar calendar;


    /**
     * Konstruktor třídy událost.
     *
     * @param title   nadpis udásloti
     * @param strDate den události
     */
    public Event(String title, String strDate) {
        this.title = title;
        this.calendar = formatDate(strDate);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * Metoda, která vytvoří kalendář s příslučným datem
     * podle vstupního stringu.
     *
     * @param strDate řetězec, který představuje datum v evropském formátu
     * @return kalendář s přesným datem
     */
    private Calendar formatDate(String strDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
