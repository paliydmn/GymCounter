package com.paliy.gymcounter_test_04;

import android.content.Context;
import android.database.Cursor;
import android.util.ArrayMap;
import android.util.Log;

import com.paliy.gymcounter_test_04.dbUtils.DBManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Statistic {
    private final DBManager dbManager;
    private Date startDate;
    private Date endDate;


    private String dateRange;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Statistic(Context context, Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        dbManager = new DBManager(context);
        try {
            dbManager.open();
        } catch (
                SQLException throwable) {
            throwable.printStackTrace();

        }
    }

    public String getDateRange() {
        dateRange = String.format("%s %d - %d", new SimpleDateFormat("MMMM").format(startDate.getTime()), startDate.getDate(), endDate.getDate());
        return dateRange;
    }

    //#ToDo develop get statistic method
    public Map<String, Integer> getStatisticMapForDateRange() {
        Cursor cursor2 = dbManager.selectCountSumForDateRange(dateFormat.format(startDate), dateFormat.format(endDate));
        ArrayMap<String, Integer> map = new ArrayMap<>();

        while (cursor2.moveToNext()) {
            String titleStr = cursor2.getString(cursor2.getColumnIndex("title"));
            String countStr = cursor2.getString(cursor2.getColumnIndex("counter"));
            map.put(titleStr, Integer.valueOf(countStr));
            Log.println(Log.DEBUG, "TITLE  : ", titleStr);
            Log.println(Log.DEBUG, "COUNT  : ", countStr);
        }
        dbManager.close();
        return map;
    }
}
