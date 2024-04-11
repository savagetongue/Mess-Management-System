package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DBManager extends SQLiteOpenHelper {
    public static String DB_NAME="Customer_Data";
    public static String ID="id";
    public static String TABLENAME1 ="customer_table";
    public static String TABLENAME2 ="owner_note";
    public static String NAME="name";
    public static String C_NAME="c_name";
    public static String MOB="mob";
    public static String NOTES="notes";
    public static String DATE="date";
    public static String BILL="bill";
    public static int DB_VER=1;

    public static String MONTH = "month";
    public static String query = "create table " + TABLENAME1 + " (" + ID + " integer primary key autoincrement, " + NAME + " text, " + C_NAME + " text, " + MOB + " text, " + BILL + " double(20), " + MONTH + " text)";

    public static String query2="create table "+TABLENAME2+" ("+ID+" integer primary key autoincrement,"+DATE+" text, "+ NOTES+ " text)";
    public DBManager(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLENAME1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLENAME2);
        onCreate(sqLiteDatabase);
    }


    public void insert_note(String date,String text)
    {
        SQLiteDatabase db=getWritableDatabase() ;
        ContentValues values=new ContentValues(); //  ContentValues Alloqs col-value pairs
        values.put(DATE,date); // Insert date in Col DATE
        values.put(NOTES,text); // Insert text In NOTES COl

        db.insert(TABLENAME2,null,values);
        db.close();
    }

    // Used To Get Dates
    // Cursors Are Used TO Navigate Through The Results Of Database Query
    public ArrayList<String> get_date()
    {
        ArrayList<String> list=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase(); //db IS An Instance Of DB In read Only Form
        String col[]={"date"};
        //Want Only Date Column From TABLENAME2
        Cursor cursor=db.query(TABLENAME2,col,null,null,null,null,null);

        // Stores Index Of Date Column
        int iName= cursor.getColumnIndex("date");

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            list.add(cursor.getString(iName)); //Fetching Date By Index in iName & Adding It TO list
        }
        db.close();
        return list;
    }

    public Cursor get_notes(String date)
    {
        SQLiteDatabase db=getReadableDatabase();

        String s="select * from owner_note where date = "+"'"+date+"'";
        Cursor cursor=db.rawQuery(s,null); //rawQuery returns Cursor Object

        return cursor;


    }


    public void delete_notes(String id)
    {
        SQLiteDatabase db=getWritableDatabase();
        String s="delete from owner_note where id= "+id;
        db.execSQL(s); // execSQL Used For DML & Create Delete Operations
    }


    public void update_notes(String date,String notes)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTES, notes);
        db.update(TABLENAME2, values, "date=?", new String[]{date});
        db.close();
    }


}

