package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private String title;
    private String description;
    private boolean isChecked;

    public Task(String title, String description, boolean isChecked) {
        this.title = title;
        this.description = description;
        this.isChecked = isChecked;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\nDescription: " + description + "\nChecked: " + isChecked;
    }

    // Parcelable implementation
    protected Task(Parcel in) {
        title = in.readString();
        description = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}
