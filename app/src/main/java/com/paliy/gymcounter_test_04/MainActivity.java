package com.paliy.gymcounter_test_04;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.paliy.gymcounter_test_04.dbUtils.DBManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.sql.DriverManager.println;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> title;
    List<String> count;
 //   Map<String, Integer> titleCountMap;

    ImageButton dateBefore;
    ImageButton dateAfter;

    Adapter adapter;

    private  AdapterView.OnClickListener listener;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        dateAfter = findViewById(R.id.btnRightDate);
        dateBefore = findViewById(R.id.btnLeftDate);

        title = new ArrayList<>();
        count = new ArrayList<>();
       // abcImage = new ArrayList<>();
//        titleCountMap = new ArrayMap<>();
//        titleCountMap.put("PushUp", 200);
//        titleCountMap.put("ABS", 140);
//        titleCountMap.put("Squatting", 240);

        title.add("PushUp");
        title.add("PullUp");
        title.add("Squatting");
        title.add("ABS");
        title.add("Test_5");
        count.add("90");
        count.add("65");
        count.add("70");
        count.add("80");
        count.add("0");

        adapter = new Adapter(this, title, count, listener);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setBackgroundColor(Color.CYAN);
        recyclerView.setAdapter(adapter);

        dbManager = new DBManager(this);
        try {
            dbManager.open();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        dbManager.insert("Title for ex", 100, new Date(), "some desc");

    }


    public void onBeforeDate(View view){
       Cursor cursor =  dbManager.fetch();

       while (cursor.moveToNext()) {
           Log.println(Log.DEBUG,"SELECT: ", cursor.getString(cursor.getColumnIndex("title")));

           Log.println(Log.DEBUG,"SELECT: ", cursor.getString(cursor.getColumnIndex("counter")));
           Log.println(Log.DEBUG,"SELECT: ", cursor.getString(cursor.getColumnIndex("title")));
           Log.println(Log.DEBUG,"SELECT: ", cursor.getString(cursor.getColumnIndex("_date")));
        }

    }

    public void onAfterDate(View view) {
        adapter.addItem("Test10", "0");
        adapter.notifyDataSetChanged();


    }
}