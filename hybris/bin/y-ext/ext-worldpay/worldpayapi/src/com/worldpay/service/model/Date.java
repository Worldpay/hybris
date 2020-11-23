package com.worldpay.service.model;

import com.worldpay.service.request.transform.InternalModelTransformer;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * POJO representation of a date
 */
public class Date implements InternalModelTransformer, Serializable {

    private String dayOfMonth;
    private String month;
    private String year;
    private String hour;
    private String minute;
    private String second;

    /**
     * Constructor that takes a standard java util LocalDateTime object and turns it into a date pojo
     *
     * @param localDateTime
     */
    public Date(final LocalDateTime localDateTime) {
        this.dayOfMonth = String.valueOf(localDateTime.getDayOfMonth());
        this.month = String.valueOf(localDateTime.getMonth().getValue());
        this.year = String.valueOf(localDateTime.getYear());
        this.hour = String.valueOf(localDateTime.getHour());
        this.minute = String.valueOf(localDateTime.getMinute());
        this.second = String.valueOf(localDateTime.getSecond());
    }

    /**
     * Constructor with full list of fields
     *
     * @param dayOfMonth
     * @param month
     * @param year
     * @param hour
     * @param minute
     * @param second
     */
    public Date(final String dayOfMonth, final String month, final String year, final String hour, final String minute, final String second) {
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    /**
     * Constructor with just month and year for expiry date type fields
     *
     * @param month
     * @param year
     */
    public Date(final String month, final String year) {
        this.dayOfMonth = null;
        this.month = month;
        this.year = year;
        this.hour = null;
        this.minute = null;
        this.second = null;
    }

    @Override
    public com.worldpay.internal.model.Date transformToInternalModel() {
        com.worldpay.internal.model.Date intDate = new com.worldpay.internal.model.Date();
        intDate.setDayOfMonth(dayOfMonth);
        intDate.setHour(hour);
        intDate.setMinute(minute);
        intDate.setMonth(month);
        intDate.setSecond(second);
        intDate.setYear(year);
        return intDate;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Date [dayOfMonth=" + dayOfMonth + ", month=" + month + ", year=" + year + ", hour=" + hour + ", minute=" + minute + ", second=" + second + "]";
    }
}
