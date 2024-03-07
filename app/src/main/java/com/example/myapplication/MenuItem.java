package com.example.myapplication;

public class MenuItem {

    private String name;
    private int iconResId;

    public MenuItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
