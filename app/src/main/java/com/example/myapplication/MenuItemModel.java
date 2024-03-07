package com.example.myapplication;

public class MenuItemModel {
    private String name;
    private String description;
    private int icon;

    public MenuItemModel(String name, String description, int icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getIcon() {
        return icon;
    }
}
