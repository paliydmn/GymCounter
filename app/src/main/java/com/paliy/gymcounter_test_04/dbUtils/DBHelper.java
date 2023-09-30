package com.paliy.gymcounter_test_04.dbUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "main";

    // Table columns
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String COUNT = "counter";
    public static final String DATE = "_date";
    public static final String DESC = "description";

    // Database Information
    static final String DB_NAME = "GymCounter.DB";

    // database version
    static final int DB_VERSION = 8;

    // Creating table query
    private static final String CREATE_MAIN_TABLE = "create table "
            + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TITLE + " TEXT NOT NULL, "
            + COUNT + " INTEGER, "
         //   + DATE + " INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + DATE + " INTEGER NOT NULL, "
            + DESC + " TEXT);";

    private static final String CREATE_EXERCISE_TABLE = "CREATE TABLE 'exercise' " +
            "('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
            "'set_id' INTEGER NOT NULL, " +
            "'ex_name' TEXT NOT NULL, " +
            "'counter' INTEGER, " +
            "'description' TEXT, " +
            "'status' INTEGER, " +
            "FOREIGN KEY('set_id') REFERENCES 'sets'('id'));";

    private static final String CREATE_SETS_TABLE = "CREATE TABLE 'sets' " +
            "('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
            "'set_name' TEXT," +
            "'status' INTEGER)";

    public DBHelper (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MAIN_TABLE);
        sqLiteDatabase.execSQL(CREATE_SETS_TABLE);
        sqLiteDatabase.execSQL(CREATE_EXERCISE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
