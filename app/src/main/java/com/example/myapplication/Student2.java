package com.example.myapplication;

public class Student2 {
    private String studentName;
    private int img;

    public Student2(String studentName) {
        this.studentName = studentName;
        this.img = R.drawable.person; // Assuming a static image for all students
    }

    public String getStudentName() {
        return studentName;
    }

    public int getImg() {
        return img;
    }
}
