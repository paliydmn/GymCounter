package com.paliy.gymcounter_test_04;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;

import android.app.DatePickerDialog;
import android.database.Cursor;
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

    ImageButton dateBeforeBtn;
    ImageButton dateAfterBtn;
    TextView dateTitleTV;
    FloatingActionButton addNewExBtn;

    Date dateOnTitleTV;

    Adapter adapter;

    private  AdapterView.OnClickListener listener;
    private DBManager dbManager;

    private static final SimpleDateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
    private static final Date TODAY = Calendar.getInstance().getTime();
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        recyclerView = findViewById(R.id.recyclerView);
        dateAfterBtn = findViewById(R.id.btnRightDate);
        dateBeforeBtn = findViewById(R.id.btnLeftDate);
        addNewExBtn = findViewById(R.id.addNewItemFABtn);

        dateTitleTV = findViewById(R.id.tvDate);

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
        initTodayData();
        updateDateTv(new Date());
        adapter.setCurrentViewDate(dateOnTitleTV);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.setBackgroundColor(Color.CYAN);
        recyclerView.setAdapter(adapter);


        dateTitleTV.setOnClickListener(view -> {
            new DatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        });

        dateTitleTV.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                onBeforeDate(dateTitleTV);
            }
            public void onSwipeLeft() {
                onAfterDate(dateTitleTV);
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                new DatePickerDialog(MainActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

            public void myOnLongPress(){
                DatePickerDialog datePickerDialog = new DatePickerDialog(dateTitleTV.getContext());
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

    public Date getDateOnTitleTV(){
        return dateOnTitleTV;
    }
    public void updateDateTv(Date date){
        dateOnTitleTV = date;
        dateTitleTV.setText(TITLE_DATE_FORMAT.format(dateOnTitleTV));
    }

    public void onBeforeDate(View view){
        Calendar cal = addDayToTitleDate(dateOnTitleTV, -1);
        Cursor cursor =  dbManager.selectByDate(cal.getTime());
        if (cursor.getCount() >= 1) {
            clearAllListData();
            updateDateTv(cal.getTime());
            if(!new Date().before(dateOnTitleTV)){
                addNewExBtn.setVisibility(View.INVISIBLE);
            }
            while (cursor.moveToNext()) {
                titleList.add(cursor.getString(cursor.getColumnIndex("title")));
                countList.add(cursor.getString(cursor.getColumnIndex("counter")));
            }
            adapter.setCurrentViewDate(dateOnTitleTV);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No DATA for requested Date", Toast.LENGTH_SHORT);
            toast.show();
        }

       adapter.notifyDataSetChanged();
    }

    private void clearAllListData() {
        titleList.clear();
        countList.clear();
    }

    private static Calendar addDayToTitleDate(Date dateOnTitleTV, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOnTitleTV);
        cal.add(Calendar.DATE, i);
        return cal;
    }

    public void onAfterDate(View view) {
        Calendar cal = addDayToTitleDate(dateOnTitleTV, 1);
        Cursor cursor =  dbManager.selectByDate(cal.getTime());
        if (cursor.getCount() >= 1) {
            clearAllListData();
            updateDateTv(cal.getTime());
            while (cursor.moveToNext()) {
                titleList.add(cursor.getString(cursor.getColumnIndex("title")));
                countList.add(cursor.getString(cursor.getColumnIndex("counter")));
            }
            adapter.setCurrentViewDate(dateOnTitleTV);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            if(sdf.format(new Date()).equals(sdf.format(dateOnTitleTV))){
                addNewExBtn.setVisibility(View.VISIBLE);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No DATA for requested Date", Toast.LENGTH_SHORT);
            toast.show();
        }

        adapter.notifyDataSetChanged();
    }

    public boolean initTodayData(){
        setTodayData();
        adapter.notifyDataSetChanged();
        return true;
    }

    public boolean getDataForDate(Date date){
        Cursor cursor =  dbManager.selectByDate(date);
        if (cursor.getCount() <= 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No DATA for requested Date", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            clearAllListData();
            updateDateTv(date);
            adapter.setCurrentViewDate(dateOnTitleTV);
            while (cursor.moveToNext()) {
                titleList.add(cursor.getString(cursor.getColumnIndex("title")));
                countList.add(cursor.getString(cursor.getColumnIndex("counter")));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            if(!sdf.format(TODAY).equals(sdf.format(dateOnTitleTV))){
                addNewExBtn.setVisibility(View.INVISIBLE);
            } else {
                addNewExBtn.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
           return true;
        }
    }

    public void setTodayData(){
        Cursor cursor =  dbManager.selectByDate(new Date());
        if (cursor.getCount() <= 0){
            Calendar cal = addDayToTitleDate(new Date(), -1);
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

    DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH,month);
        myCalendar.set(Calendar.DAY_OF_MONTH,day);
        updateLabel(myCalendar.getTime());
    };
    private void updateLabel(Date foDate){
        if (getDataForDate(foDate)){
            dateTitleTV.setText(TITLE_DATE_FORMAT.format(foDate));
        }
    }

    public void onDateTitleclick(View view) {
        Log.println(Log.DEBUG, "TEST ", "Click on Title");
    }
}