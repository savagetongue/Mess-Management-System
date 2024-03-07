package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DBManager extends SQLiteOpenHelper {
    public static String DB_NAME="customer_data";
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

    public void insert_into(String name,String c_name,String mob)
    {
        SQLiteDatabase db=getWritableDatabase() ;
        ContentValues values=new ContentValues();
        values.put("name",name);
        values.put("c_name",c_name);
        values.put("mob",mob);
        values.put("bill","2000");

        db.insert(TABLENAME1,null,values);
        db.close();
    }
    public void insert_note(String date,String text)
    {
        SQLiteDatabase db=getWritableDatabase() ;
        ContentValues values=new ContentValues();
        values.put(DATE,date);
        values.put(NOTES,text);

        db.insert(TABLENAME2,null,values);
        db.close();
    }

    public ArrayList<String> get_users()
    {
        ArrayList<String> list=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        String col[]={"name"};
        Cursor cursor=db.query(TABLENAME1,col,null,null,null,null,null);

        int iName= cursor.getColumnIndex("name");

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            list.add(cursor.getString(iName));
        }
        db.close();
        return list;
    }
    public ArrayList<String> get_date()
    {
        ArrayList<String> list=new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        String col[]={"date"};
        Cursor cursor=db.query(TABLENAME2,col,null,null,null,null,null);

        int iName= cursor.getColumnIndex("date");

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            list.add(cursor.getString(iName));
        }
        db.close();
        return list;
    }

    public Cursor get_notes(String date)
    {
        SQLiteDatabase db=getReadableDatabase();

        String s="select * from owner_note where date = "+"'"+date+"'";
        Cursor cursor=db.rawQuery(s,null);

        return cursor;


    }

    public Cursor fetch_data(String name)
    {
        SQLiteDatabase db=getReadableDatabase();

        String s="select * from customer_table where name = "+"'"+name+"'";
        Cursor cursor=db.rawQuery(s,null);

        return cursor;


    }

    public void delete_user(String id)
    {
        SQLiteDatabase db=getWritableDatabase();
        String s="Delete from customer_table where id="+id;
        db.execSQL(s);
    }

    public void delete_notes(String id)
    {
        SQLiteDatabase db=getWritableDatabase();
        String s="delete from owner_note where id= "+id;
        db.execSQL(s);
    }

    public ArrayList<String> getPaid()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<String> list=new ArrayList<>();
        String col[]={"name"};
        Cursor cursor=db.query(TABLENAME1,col,"bill = ?",new String[]{"0"},null,null,null);
        int iName= cursor.getColumnIndex("name");
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            list.add(cursor.getString(iName));
        }
        db.close();
        return list;
    }
    public ArrayList<String> getUnpaid()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<String> list=new ArrayList<>();
        String col[]={"name"};
        Cursor cursor= db.query(TABLENAME1,col,"bill > ?",new String[]{"0"},null,null,null);

        int iName=cursor.getColumnIndex("name");
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                list.add(cursor.getString(iName));
            }
            db.close();
            return list;
        }
    }
    public void update_data(String id,String name,String mob,String c_name)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(MOB, mob);
        values.put(C_NAME, c_name);
        db.update(TABLENAME1, values, "id=?", new String[]{id});
        db.close();
    }

    public void update_bill(String id,String bill)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BILL, bill);
        db.update(TABLENAME1, values, "id=?", new String[]{id});
        db.close();
    }
    // Add this method in your DBManager class


    public void update_notes(String date,String notes)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTES, notes);
        db.update(TABLENAME2, values, "date=?", new String[]{date});
        db.close();
    }
    public ArrayList<String> getPaidForMonth(String month) {
        return getCustomersWithStatusForMonth("0", month);
    }

    public ArrayList<String> getUnpaidForMonth(String month) {
        return getCustomersWithStatusForMonth("1", month);
    }

    private ArrayList<String> getCustomersWithStatusForMonth(String status, String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        String col[] = {NAME};

        // Fetch customers with the specified billing status for the specified month
        Cursor cursor = db.query(TABLENAME1, col, BILL + " = ? AND " + MONTH + " = ?", new String[]{status, month}, null, null, null);

        int iName = cursor.getColumnIndex(NAME);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(cursor.getString(iName));
        }

        cursor.close();
        db.close();
        return list;
    }

}

