package com.example.myapplication;

public class Student {
    private String id;
    private String name;
    private String cName;
    private String mob;

    public Student() {
        // Default constructor required for Firestore
    }

    public Student(String id, String name, String cName, String mob) {
        this.id = id;
        this.name = name;
        this.cName = cName;
        this.mob = mob;
    }

    public String getId() {
        return id;
    }
    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public void setId(String id) {
        this.id = id;
    }

    // Add this getter method for studentId
    public int getStudentId() {
        return Integer.parseInt(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCName() {
        return cName;
    }

    public void setCName(String cName) {
        this.cName = cName;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }
}

