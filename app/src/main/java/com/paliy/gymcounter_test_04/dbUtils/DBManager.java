package com.paliy.gymcounter_test_04.dbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

public class DBManager {
    private DBHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String title, int count, String date, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.TITLE, title);
        contentValue.put(DBHelper.COUNT, count);
        contentValue.put(DBHelper.DATE, date);
        contentValue.put(DBHelper.DESC, desc);
        database.insert(DBHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DBHelper._ID, DBHelper.TITLE,DBHelper.COUNT,DBHelper.DATE, DBHelper.DESC };
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String title, int count, String date, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TITLE, title);
        contentValues.put(DBHelper.COUNT, count);
        contentValues.put(DBHelper.DATE, date);
        contentValues.put(DBHelper.DESC, desc);
        int i = database.update(DBHelper.TABLE_NAME, contentValues, DBHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DBHelper.TABLE_NAME, DBHelper._ID + "=" + _id, null);
    }
}
