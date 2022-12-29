package com.paliy.gymcounter_test_04;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    FloatingActionButton addNewExBtn;

    public Date dateOnView;

    Adapter adapter;

    private  AdapterView.OnClickListener listener;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide(); //<< this

        recyclerView = findViewById(R.id.recyclerView);
        dateAfterBtn = findViewById(R.id.btnRightDate);
        dateBeforeBtn = findViewById(R.id.btnLeftDate);
        addNewExBtn = findViewById(R.id.addNewItemFABtn);

        dateTv = findViewById(R.id.tvDate);

        titleList = new ArrayList<>();
        countList = new ArrayList<>();

        dbManager = new DBManager(this);
        try {
            dbManager.open();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
       // dbManager.insert("Title for ex", 100, new Date(), "some desc");

        adapter = new Adapter(this, titleList, countList, listener);
        initTodaysData();
        updateDateTv(new Date());
        adapter.setCutterViewDate(dateOnView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setBackgroundColor(Color.BLACK);
        recyclerView.setAdapter(adapter);

/*
//for debugging
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -3);
        dbManager.insert("Test-3", 100,cal.getTime(), "some desc");*/
        dateTv.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                onBeforeDate(dateTv);
            }
            public void onSwipeLeft() {
                onAfterDate(dateTv);
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();

            }
            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

            public void myOnLongPress(){
                DatePickerDialog datePickerDialog = new DatePickerDialog(dateTv.getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                         Toast.makeText(MainActivity.this, "Long Pess " + dayOfMonth , Toast.LENGTH_SHORT).show();

                    }
                });
                datePickerDialog.show();
            }

        });

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
            if(!new Date().before(dateOnView)){
                addNewExBtn.setVisibility(View.INVISIBLE);
            }
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            if(sdf.format(new Date()).equals(sdf.format(dateOnView))){
                addNewExBtn.setVisibility(View.VISIBLE);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No DATA for requested Date", Toast.LENGTH_SHORT);
            toast.show();
        }

        adapter.notifyDataSetChanged();
    }

    public boolean initTodaysData(){
        getTodayData();
        adapter.notifyDataSetChanged();
        return true;
    }

    public void getTodayData(){

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
                    String titleStr = cursor.getString(cursor.getColumnIndex("title"));
                    titleList.add(titleStr);
                    countList.add("0");
                    dbManager.insert(titleStr, 0, new Date(), "default");
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
        adapter.notifyDataSetChanged();
        updateDateTv(new Date());
    }

    public void initDefaultTitles(){
        titleList.addAll(Arrays.asList("PushUP", "PullUP", "ABS"));
        countList.addAll(Arrays.asList("0", "0", "0"));
        for (String title : titleList) {
            dbManager.insert(title, 0, new Date(), "default");
        }
    }

    public void onAddNewItem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        final View dialogView = inflater.inflate(R.layout.add_new_item_dialog,null);
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("Create", (dialog, id) -> {
                    EditText newTitleEditT =dialogView.findViewById(R.id.newExeciseEditText);
                    String newTitle = newTitleEditT.getText().toString();
                    dbManager.insert(newTitle, 0, new Date(), "Created");
                    titleList.add(newTitle);
                    countList.add("0");
                })
                .setNegativeButton("Cancel", (dialog, id) -> {

                });
        builder.create();
        builder.show();

    }




}