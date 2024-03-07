package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Complaint implements Parcelable {

    private String complaintText;
    private String date;
    private byte[] imageBytes; // Store the image as a byte array

    public Complaint(String complaintText, String date, byte[] imageBytes) {
        this.complaintText = complaintText;
        this.date = date;
        this.imageBytes = imageBytes;
    }

    protected Complaint(Parcel in) {
        complaintText = in.readString();
        date = in.readString();
        imageBytes = in.createByteArray();
    }

    public static final Creator<Complaint> CREATOR = new Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel in) {
            return new Complaint(in);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };

    public String getComplaintText() {
        return complaintText;
    }

    public String getDate() {
        return date;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(complaintText);
        dest.writeString(date);
        dest.writeByteArray(imageBytes);
    }
}
