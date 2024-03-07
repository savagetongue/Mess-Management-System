package com.example.myapplication;

public class UnpaidStudent {
    private String studentName;
    private int img;  // You can set a static image for all unpaid students

    public UnpaidStudent(String studentName) {
        this.studentName = studentName;
        this.img = R.drawable.person; // Set a default image for unpaid students
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
