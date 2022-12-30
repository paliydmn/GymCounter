package com.paliy.gymcounter_test_04.dbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;


public class DBManager {
    private DBHelper dbHelper;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    private final Context context;

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

    public void insert(String title, int count, Date date, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.TITLE, title);
        contentValue.put(DBHelper.COUNT, count);
        contentValue.put(DBHelper.DATE, dateFormat.format(date));
        contentValue.put(DBHelper.DESC, desc);
        database.insert(DBHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor select() {
        String[] columns = new String[] { DBHelper._ID, DBHelper.TITLE,DBHelper.COUNT,DBHelper.DATE, DBHelper.DESC };
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor selectByDate(Date date) {
        //SELECT * from main where _date = date('now', '-1 days');
        String[] columns = new String[] { DBHelper._ID, DBHelper.TITLE,DBHelper.COUNT,DBHelper.DATE, DBHelper.DESC };
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, String.format("_date = '%s'", dateFormat.format(date)), null, null, null, null);
        if (cursor != null) {
           // cursor.moveToFirst();
        }
        return cursor;
    }

    public int updateCounter(String title, int count, Date date, String desc) {
        //UPDATE main SET counter = (counter + 5) WHERE _date = '2022-12-25' AND title = 'PushUp' ;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COUNT, ("(" + DBHelper.COUNT + " + " + count + ")"));
        contentValues.put(DBHelper.DESC, desc);
        int i = database.update(DBHelper.TABLE_NAME, contentValues, DBHelper.TITLE + " = '" + title + "' AND " + DBHelper.DATE + " = '" + dateFormat.format(date) + "'" , null);

        return i;
    }

    public boolean updateCounterRaw(String title, int count, Date date, String desc){
        String strSQL = "UPDATE main SET counter = (counter + 5) WHERE _date = '" + dateFormat.format(date) + "' AND title = '"+ title + "'";
        database.execSQL(strSQL);
        return true;
    }
    public boolean updateTitleRaw(String oldTitle, String newTitle, Date date){
        String strSQL = "UPDATE main SET title = '" + newTitle + "' WHERE _date = '" + dateFormat.format(date) + "' AND title = '"+ oldTitle + "'";
        database.execSQL(strSQL);
        return true;
    }

    public int delete(String title, Date date) {
       return database.delete(DBHelper.TABLE_NAME, DBHelper.TITLE + " = '" + title + "' AND _date = '" + dateFormat.format(date) + "'" , null);

    }
}
