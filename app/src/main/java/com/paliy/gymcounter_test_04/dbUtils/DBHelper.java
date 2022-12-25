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
    static final int DB_VERSION = 3;

    // Creating table query
    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TITLE + " TEXT NOT NULL, "
            + COUNT + " INTEGER, "
            + DATE + " INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, "
            + DESC + " TEXT);";

    public DBHelper (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
