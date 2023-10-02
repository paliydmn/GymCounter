package com.paliy.gymcounter_test_04.dbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DBManager {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Context context;
    private DBHelper dbHelper;
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
        database.insert(DBHelper.MAIN_TABLE_NAME, null, contentValue);
    }

    public Cursor select() {
        String[] columns = new String[]{DBHelper._ID, DBHelper.TITLE, DBHelper.COUNT, DBHelper.DATE, DBHelper.DESC};
        Cursor cursor = database.query(DBHelper.MAIN_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //select * from main where _date between '2022-12-27' and  '2023-01-13' ;
    public Cursor selectByDate(Date date) {
        //SELECT * from main where _date = date('now', '-1 days');
        String[] columns = new String[]{DBHelper._ID, DBHelper.TITLE, DBHelper.COUNT, DBHelper.DATE, DBHelper.DESC};
        Cursor cursor = database.query(DBHelper.MAIN_TABLE_NAME, columns, String.format("_date = '%s'", dateFormat.format(date)), null, null, null, null);
        if (cursor != null) {
            // cursor.moveToFirst();
        }
        return cursor;
    }

    //#Todo check if that select is needed
    public Cursor selectByTitleAndDate(Date date, String title) {
        String[] columns = new String[]{DBHelper.DESC};
        Cursor cursor = database.query(DBHelper.MAIN_TABLE_NAME, columns, String.format("_date = '%s' AND title='%s'", dateFormat.format(date), title), null, null, null, null);
        if (cursor != null) {
            // cursor.moveToFirst();
        }
        return cursor;
    }

    //SELECT title, SUM(counter) from main where _date between '2023-01-01' and '2023-01-25' GROUP BY title;
    //public Cursor selectCountSumForDateRange(Date startDate, Date endDate) {
    public Cursor selectCountSumForDateRange(String startDate, String endDate) {
        String[] columns = new String[]{DBHelper.TITLE, String.format("SUM(%s) as counter", DBHelper.COUNT)};
        Cursor cursor = database.query(DBHelper.MAIN_TABLE_NAME, columns, String.format("_date BETWEEN '%s' AND '%s'", startDate, endDate), null, "title", null, null);
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
        int i = database.update(DBHelper.MAIN_TABLE_NAME, contentValues, DBHelper.TITLE + " = '" + title + "' AND " + DBHelper.DATE + " = '" + dateFormat.format(date) + "'", null);

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

    public int deleteExFromMainByTitleDate(String title, Date date) {
        return database.delete(DBHelper.MAIN_TABLE_NAME, DBHelper.TITLE + " = '" + title + "' AND _date = '" + dateFormat.format(date) + "'", null);
    }

    //Todo check for UNIQUE constraint set_name
    public boolean insertNewSet(String set_name, int status) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.SET_NAME, set_name);
        contentValue.put(DBHelper.STATUS, status);
        long res = database.insert(DBHelper.SETS_TABLE_NAME, null, contentValue);
        return (res >= 0) ? true : false;
    }

    public void insertNewExToSetRaw(String set_name, String ex_name, String ex_descr, int status) {
        //INSERT INTO main (set_name, title, counter, description, _date, set_id)
        // SELECT sets.set_name, exercise.ex_name, 0, exercise.description, datetime(), sets.id FROM exercise
        // INNER JOIN sets on exercise.set_id = sets.id WHERE sets.set_name = "Back3"

        //INSERT INTO exercise (set_id, ex_name, counter, description, status) VALUES ((SELECT id FROM sets WHERE set_name = 'back'), "Stand Up", 0, "We do it hard +16kg", 0);
        String strSQL = "INSERT INTO exercise (set_id, ex_name, counter, description, status) " +
                "VALUES " +
                "((SELECT id FROM sets WHERE set_name = '" + set_name + "'), '" +
                ex_name + "', 0, '" +
                ex_descr + "', " + status + ");";
        database.execSQL(strSQL);
    }

    public Cursor selectTest() {
        String[] columns = new String[]{"set_name", "status", "id"};
        Cursor cursor = database.query("sets", columns, null, null, null, null, null);
        if (cursor != null) {
            // cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor selectExs(String set_name) {
        String[] columns = new String[]{"ex_name", "description"};
        //SELECT sets.set_name, exercise.ex_name, 0, exercise.description, datetime(), sets.id FROM exercise INNER JOIN sets on exercise.set_id = sets.id WHERE sets.set_name = "Back3"
        Cursor cursor = database.rawQuery("SELECT exercise.ex_name, exercise.description FROM exercise INNER JOIN sets on exercise.set_id = sets.id WHERE sets.set_name = '" + set_name + "'", null);
        if (cursor != null) {
            // cursor.moveToFirst();
        }
        return cursor;
    }


    public void deleteSetByName(String set_name) {
/*
SELECT id from exercise where set_id = (SELECT id FROM sets where set_name='Biceps');
DELETE FROM exercise WHERE id In (1, 2, 3, 4, 5);
DELETE from sets WHERE set_name = 'Biceps';
 */

        String select_ids = String.format("SELECT id from exercise where set_id = (SELECT id FROM sets where set_name='%s');", set_name);
        Cursor c = database.rawQuery(select_ids, null);

        StringBuffer ids = new StringBuffer();
        if (c.getCount() >= 1) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex("id"));
                ids.append(id).append(", ");
                System.out.println(id);
            }
        }

        String del_ex = null;
        if (!ids.toString().equals("")) {
            del_ex = String.format("DELETE FROM exercise WHERE id IN (%s);", ids.deleteCharAt(ids.length() - 2).toString());
            System.out.println(del_ex);
            database.execSQL(del_ex);

        }
        String del_set = String.format("DELETE FROM sets where set_name='%s';", set_name);

        database.execSQL(del_set);
    }

    public void deleteExByExNameSetName(String ex_name, String set_name) {
        String deleteExFromSet = String.format("DELETE FROM exercise WHERE ex_name = '%s' AND set_id = (select id from sets where set_name = '%s');", ex_name, set_name);
        database.execSQL(deleteExFromSet);
    }
}
