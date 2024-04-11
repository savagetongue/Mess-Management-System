package com.example.myapplication;

public class PaidStudent {
    private String studentName;
    private String paidDate;
    private int img;  // You can set a static image for all paid students

    public PaidStudent(String studentName) {
        this.studentName = studentName;
        this.img = R.drawable.person; // Set a default image for paid students
    }




        public PaidStudent(String studentName, String paidDate) {
            this.studentName = studentName;
            this.paidDate = paidDate;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getPaidDate() {
            return paidDate;
        }


    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }






}
