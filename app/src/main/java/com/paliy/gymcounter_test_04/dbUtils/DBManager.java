package com.paliy.gymcounter_test_04.dbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
        String[] columns = new String[]{DBHelper._ID, DBHelper.TITLE, DBHelper.COUNT, DBHelper.DATE, DBHelper.DESC};
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //select * from main where _date between '2022-12-27' and  '2023-01-13' ;
    public Cursor selectByDate(Date date) {
        //SELECT * from main where _date = date('now', '-1 days');
        String[] columns = new String[]{DBHelper._ID, DBHelper.TITLE, DBHelper.COUNT, DBHelper.DATE, DBHelper.DESC};
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, String.format("_date = '%s'", dateFormat.format(date)), null, null, null, null);
        if (cursor != null) {
            // cursor.moveToFirst();
        }
        return cursor;
    }

    //#Todo check if that select is needed
    public Cursor selectByTitleAndDate(Date date, String title) {
        String[] columns = new String[]{DBHelper.DESC};
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, String.format("_date = '%s' AND title='%s'", dateFormat.format(date), title), null, null, null, null);
        if (cursor != null) {
            // cursor.moveToFirst();
        }
        return cursor;
    }

    //SELECT title, SUM(counter) from main where _date between '2023-01-01' and '2023-01-25' GROUP BY title;
    //public Cursor selectCountSumForDateRange(Date startDate, Date endDate) {
    public Cursor selectCountSumForDateRange(String startDate, String endDate) {
        String[] columns = new String[]{DBHelper.TITLE, String.format("SUM(%s) as counter", DBHelper.COUNT)};
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, String.format("_date BETWEEN '%s' AND '%s'", startDate, endDate), null, "title", null, null);
        //Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, String.format("_date = '%s' BETWEEN _date = '%s'", dateFormat.format(startDate),dateFormat.format(endDate)), null, "title", null, null);
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
        int i = database.update(DBHelper.TABLE_NAME, contentValues, DBHelper.TITLE + " = '" + title + "' AND " + DBHelper.DATE + " = '" + dateFormat.format(date) + "'", null);

        return i;
    }

    public boolean updateAddCounterRaw(String title, int count, Date date, String desc) {
        String strSQL = "UPDATE main SET counter = (counter + " + count + ") WHERE _date = '" + dateFormat.format(date) + "' AND title = '" + title + "'";
        database.execSQL(strSQL);
        return true;
    }

    public boolean updateSetCounterRaw(String title, int count, Date date, String desc) {
        String strSQL = "UPDATE main SET counter = " + count + " WHERE _date = '" + dateFormat.format(date) + "' AND title = '" + title + "'";
        database.execSQL(strSQL);
        return true;
    }

    public boolean updateTitleRaw(String oldTitle, String newTitle, Date date) {
        String strSQL = "UPDATE main SET title = '" + newTitle + "' WHERE _date = '" + dateFormat.format(date) + "' AND title = '" + oldTitle + "'";
        database.execSQL(strSQL);
        return true;
    }

    public boolean updateTitleAndDescrRaw(String oldTitle, String newTitle, String newDescr, Date date) {
        String strSQL = "UPDATE main SET title = '" + newTitle + "', description = '" + newDescr + "'  WHERE _date = '" + dateFormat.format(date) + "' AND title = '" + oldTitle + "'";
        database.execSQL(strSQL);
        return true;
    }

    public int delete(String title, Date date) {
        return database.delete(DBHelper.TABLE_NAME, DBHelper.TITLE + " = '" + title + "' AND _date = '" + dateFormat.format(date) + "'", null);

    }
}
