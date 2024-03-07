package com.example.myapplication;

public class MonthItem {
    private String monthName;
    private int img;

    // Default constructor with static image
    public MonthItem(String monthName) {
        this.monthName = monthName;
        this.img = R.drawable.mon; // Assuming a static image for all months
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
