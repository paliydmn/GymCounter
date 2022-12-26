package com.paliy.gymcounter_test_04;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.paliy.gymcounter_test_04.dbUtils.DBManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> titleList;
    List<String> countList;
 //   Map<String, Integer> titleCountMap;

    ImageButton dateBeforeBtn;
    ImageButton dateAfterBtn;
    TextView dateTv;
    public Date dateOnView;

    Adapter adapter;

    private  AdapterView.OnClickListener listener;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        dateAfterBtn = findViewById(R.id.btnRightDate);
        dateBeforeBtn = findViewById(R.id.btnLeftDate);
        dateTv = findViewById(R.id.tvDate);


        titleList = new ArrayList<>();
        countList = new ArrayList<>();

        // debug data
//        titleCountMap = new ArrayMap<>();
//        titleCountMap.put("PushUp", 200);
//        titleCountMap.put("ABS", 140);
//        titleCountMap.put("Squatting", 240);

//        titleList.add("PushUp");
//        titleList.add("PullUp");
//        titleList.add("Squatting");
//        titleList.add("ABS");
//        titleList.add("Test_5");
//        countList.add("90");
//        countList.add("65");
//        countList.add("70");
//        countList.add("80");
//        countList.add("0");

        dbManager = new DBManager(this);
        try {
            dbManager.open();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
       // dbManager.insert("Title for ex", 100, new Date(), "some desc");

        adapter = new Adapter(this, titleList, countList, listener);
        intitTodaysData();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setBackgroundColor(Color.CYAN);
        recyclerView.setAdapter(adapter);

    }

    public Date getDateOnView(){
        return dateOnView;
    }
    public void updateDateTv(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        dateOnView = date;
        dateTv.setText(dateFormat.format(dateOnView));
    }

    public void onBeforeDate(View view){
        Cursor cursor =  dbManager.selectByDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOnView);
        cal.add(Calendar.DATE, -1);
        cursor =  dbManager.selectByDate(cal.getTime());
        if (cursor.getCount() >= 1) {
            titleList.clear();
            countList.clear();
            updateDateTv(cal.getTime());
            while (cursor.moveToNext()) {
                titleList.add(cursor.getString(cursor.getColumnIndex("title")));
                countList.add(cursor.getString(cursor.getColumnIndex("counter")));
            }
            adapter.setCutterViewDate(dateOnView);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No DATA for requested Date", Toast.LENGTH_SHORT);
            toast.show();
        }

       adapter.notifyDataSetChanged();
//       while (cursor.moveToNext()) {
//           Log.println(Log.DEBUG,"SELECT: ", cursor.getString(cursor.getColumnIndex("title")));
//           Log.println(Log.DEBUG,"SELECT: ", cursor.getString(cursor.getColumnIndex("counter")));
//           Log.println(Log.DEBUG,"SELECT: ", cursor.getString(cursor.getColumnIndex("title")));
//           Log.println(Log.DEBUG,"SELECT: ", cursor.getString(cursor.getColumnIndex("_date")));
//        }
    }

    public void onAfterDate(View view) {
        Cursor cursor =  dbManager.selectByDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOnView);
        cal.add(Calendar.DATE, 1);
        // dbManager.insert("Test", 100, cal.getTime(), "some desc");
        cursor =  dbManager.selectByDate(cal.getTime());
        if (cursor.getCount() >= 1) {
            titleList.clear();
            countList.clear();
            updateDateTv(cal.getTime());
            while (cursor.moveToNext()) {
                titleList.add(cursor.getString(cursor.getColumnIndex("title")));
                countList.add(cursor.getString(cursor.getColumnIndex("counter")));
            }
            adapter.setCutterViewDate(dateOnView);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No DATA for requested Date", Toast.LENGTH_SHORT);
            toast.show();
        }

        adapter.notifyDataSetChanged();
    }

    public boolean intitTodaysData(){
        getTodayData();
    //    adapter.addItems(titleList, countList);
        adapter.notifyDataSetChanged();
        return true;
    }

    public void getTodayData(){

        //Cursor cursor =  dbManager.selectByDate(new Date());
        //minus number would decrement the days

        Cursor cursor =  dbManager.selectByDate(new Date());
        if (cursor.getCount() <= 0){
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -1);
            cursor =  dbManager.selectByDate(cal.getTime());
            if (cursor.getCount() <= 0) {
                initDefaultTitles();
            } else {
                while (cursor.moveToNext()) {
                    titleList.add(cursor.getString(cursor.getColumnIndex("title")));
                    countList.add("0");
                }
            }
        } else {
            while (cursor.moveToNext()) {
                titleList.add(cursor.getString(cursor.getColumnIndex("title")));
                countList.add(cursor.getString(cursor.getColumnIndex("counter")));

                Log.println(Log.DEBUG, "SELECT: ", cursor.getString(cursor.getColumnIndex("title")));
                Log.println(Log.DEBUG, "SELECT: ", cursor.getString(cursor.getColumnIndex("counter")));
                Log.println(Log.DEBUG, "SELECT: ", cursor.getString(cursor.getColumnIndex("title")));
                Log.println(Log.DEBUG, "SELECT: ", cursor.getString(cursor.getColumnIndex("_date")));
            }
        }

        updateDateTv(new Date());
    }

    public void initDefaultTitles(){
        titleList.addAll(Arrays.asList("PushUP", "PullUP", "ABS"));
        countList.addAll(Arrays.asList("0", "0", "0"));
        for (String title : titleList) {
            dbManager.insert(title, 0, new Date(), "default");
        }
    }

}