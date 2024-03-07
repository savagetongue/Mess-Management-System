package com.example.myapplication;

public class User {
    private String menu;
    private String menu_item;
    private int img;

    public User(String menu, String menu_item, int img) {
        this.menu = menu;
        this.menu_item = menu_item;
        this.img = img;
    }



        public User(String studentName) {
            this.menu = studentName;
            this.menu_item = ""; // Optional: Set a default value for menu_item
            this.img = R.drawable.person; // Assuming a static image for all students
        }



    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMenu_item() {
        return menu_item;
    }

    public void setMenu_item(String menu_item) {
        this.menu_item = menu_item;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
