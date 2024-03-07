// Utils.java
package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utils {

    public static String serializeComplaints(ArrayList<Complaint> complaintsList) {
        Gson gson = new Gson();
        return gson.toJson(complaintsList);
    }

    public static ArrayList<Complaint> deserializeComplaints(String serializedComplaints) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Complaint>>() {}.getType();
        return gson.fromJson(serializedComplaints, type);
    }

    public static String encodeBitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64ToBitmap(String encodedImage) {
        if (encodedImage.isEmpty()) return null;

        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
