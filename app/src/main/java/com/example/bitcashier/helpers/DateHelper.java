package com.example.bitcashier.helpers;

import java.util.Calendar;

public class DateHelper {

    private int currYear, currMonth, currDay;
    private int givenYear, givenMonth, givenDay;
    private String currDateInDisplayFormat, currDateInStoreFormat;
    private String givenDateInDisplayFormat, givenDateInStoreFormat;

    public DateHelper() {
        setCurrentDate();
        setCurrentDateStringFormats();
    }

    public DateHelper(int year, int month, int day) {
        setCurrentDate();
        setCurrentDateStringFormats();

        givenYear = year;
        givenMonth = month;
        givenDay = day;

        setGivenDateStringFormats();
    }

    public DateHelper(String date, String separator) {
        setCurrentDate();
        setCurrentDateStringFormats();
        setGivenDate(date, separator);
        setGivenDateStringFormats();
    }

    private void setCurrentDate() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        currYear = c.get(Calendar.YEAR);
        currMonth = c.get(Calendar.MONTH);
        currDay = c.get(Calendar.DAY_OF_MONTH);
    }

    private void setCurrentDateStringFormats() {
        currDateInDisplayFormat = convertDateToFormat(
                this.getCurrYear(),
                this.getCurrMonth(),
                this.getCurrDay(),
                "/",
                "display"
        );
        currDateInStoreFormat = convertDateToFormat(
                this.getCurrYear(),
                this.getCurrMonth(),
                this.getCurrDay(),
                "-",
                "store"
        );
    }

    private void setGivenDate(String date, String separator) {
        String [] dateParts = date.split(separator);
        givenDay = Integer.parseInt(dateParts[0]);
        givenMonth = Integer.parseInt(dateParts[1]) - 1;
        givenYear = Integer.parseInt(dateParts[2]);
    }

    private void setGivenDateStringFormats() {
        givenDateInDisplayFormat = convertDateToFormat(
                this.getGivenYear(),
                this.getGivenMonth(),
                this.getGivenDay(),
                "/",
                "display"
        );
        givenDateInStoreFormat = convertDateToFormat(
                this.getGivenYear(),
                this.getGivenMonth(),
                this.getGivenDay(),
                "-",
                "store"
        );
    }

    public int getCurrYear() {
        return currYear;
    }

    public void setCurrYear(int currYear) {
        this.currYear = currYear;
    }

    public int getCurrMonth() {
        return currMonth;
    }

    public void setCurrMonth(int currMonth) {
        this.currMonth = currMonth;
    }

    public int getCurrDay() {
        return currDay;
    }

    public void setCurrDay(int currDay) {
        this.currDay = currDay;
    }

    public int getGivenYear() {
        return givenYear;
    }

    public void setGivenYear(int givenYear) {
        this.givenYear = givenYear;
    }

    public int getGivenMonth() {
        return givenMonth;
    }

    public void setGivenMonth(int givenMonth) {
        this.givenMonth = givenMonth;
    }

    public int getGivenDay() {
        return givenDay;
    }

    public void setGivenDay(int givenDay) {
        this.givenDay = givenDay;
    }

    public String getCurrDateInDisplayFormat() {
        return currDateInDisplayFormat;
    }

    public void setCurrDateInDisplayFormat(String currDateInDisplayFormat) {
        this.currDateInDisplayFormat = currDateInDisplayFormat;
    }

    public String getCurrDateInStoreFormat() {
        return currDateInStoreFormat;
    }

    public void setCurrDateInStoreFormat(String currDateInStoreFormat) {
        this.currDateInStoreFormat = currDateInStoreFormat;
    }

    public String getGivenDateInDisplayFormat() {
        return givenDateInDisplayFormat;
    }

    public String getGivenDateInStoreFormat() {
        return givenDateInStoreFormat;
    }

    public void setGivenDateInDisplayFormat(String givenDateInDisplayFormat) {
        this.givenDateInDisplayFormat = givenDateInDisplayFormat;
    }

    public void setGivenDateInStoreFormat(String givenDateInStoreFormat) {
        this.givenDateInStoreFormat = givenDateInStoreFormat;
    }

    private String appendZero(int value) {
        return (value < 10) ? "0"+value : ""+value;
    }

    public String convertDateToFormat(int year, int month, int day, String separator, String format) {
        int monthValue = month + 1;
        String monthStr = appendZero(monthValue);
        String dayStr = appendZero(day);

        String displayFormat = "" + dayStr + separator + monthStr + separator + year + "";
        String storeFormat = "" + year + separator + monthStr + separator + dayStr + "";

        return (format.equals("display")) ? displayFormat : storeFormat;
    }

    public int[] extractDatePartsFromDateString(String date, String separator) {
        String [] dateParts = date.split(separator);
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);
        return new int[]{ year, month, day };
    }

}
