package com.paliy.gymcounter_test_04;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.paliy.gymcounter_test_04.dbUtils.DBManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
    private static final Date TODAY = Calendar.getInstance().getTime();
    final Calendar myCalendar = Calendar.getInstance();
    NavigationView navigationView;
    RecyclerView recyclerView;
    List<String> titleList;
    List<String> countList;
    ImageButton dateBeforeBtn;
    ImageButton dateAfterBtn;
    TextView dateTitleTV;
    final DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);
        updateLabel(myCalendar.getTime());
    };
    TextView addNewSetTVBtn;
    FloatingActionButton addNewExBtn;
    Date dateOnTitleTV;
    Adapter adapter;
    DrawerLayout drawerLayout;
    MainActivity activity_main;
    Toolbar toolbar;

    ExpListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    ImageView trashImB;

    ExpandableListView.OnChildClickListener myOnChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {
            System.out.println("onChildClick");

            return true;
        }
    };
    ExpandableListView.OnGroupCollapseListener myOnGroupCollapseListener = new ExpandableListView.OnGroupCollapseListener() {

        @Override
        public void onGroupCollapse(int groupPosition) {
            // group collapse at groupPosition

            System.out.println("OnGroupCollapseListener");
        }
    };
    ExpandableListView.OnGroupExpandListener myOnGroupExpandListener = new ExpandableListView.OnGroupExpandListener() {

        @Override
        public void onGroupExpand(int groupPosition) {
            // group expand at groupPosition
            System.out.println("OnGroupExpandListener");

        }
    };
    private AdapterView.OnClickListener listener;
    private DBManager dbManager;
    ExpandableListView.OnGroupClickListener myOnGroupClickListener = new ExpandableListView.OnGroupClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v,
                                    int groupPosition, long id) {
            System.out.println("OnGroupClickListener");
            TextView textGroup = v.findViewById(R.id.textGroup);
            TextView addNewSet = v.findViewById(R.id.addNewExTVBtn);
            textGroup.setSingleLine(true);

            trashImB = v.findViewById(R.id.trashImB);

            String set_name = (String) textGroup.getText();
            addNewSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("ADD NEW Ex!");
                    dbManager.insertNewExToSetRaw(set_name, "1 Pupa Sq", "do like a boss! ", 0);
                    refreshExListView();
                }
            });

            trashImB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("DELETE SET");
                    dbManager.deleteSetByName(set_name);
                    refreshExListView();
                }
            });
            return false;
        }
    };

    private static Calendar addDayToTitle(Date dateOnTitleTV, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOnTitleTV);
        cal.add(Calendar.DATE, i);
        return cal;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        recyclerView = findViewById(R.id.recyclerView);
        dateAfterBtn = findViewById(R.id.btnRightDate);
        dateBeforeBtn = findViewById(R.id.btnLeftDate);
        //ToDo addNewExBtn = findViewById(R.id.addNewItemFABtn); should add new Ex to currently selected SET.
        addNewExBtn = findViewById(R.id.addNewItemFABtn);
        // Add new Ex to SET at Expandable List
        addNewSetTVBtn = findViewById(R.id.addNewSetTVBtn);
        navigationView = findViewById(R.id.navigationView);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        navigationView.setLayoutParams(new ConstraintLayout.LayoutParams((int) (width * 0.75), ViewGroup.LayoutParams.MATCH_PARENT));
        activity_main = this;

        dateTitleTV = findViewById(R.id.tvDate);

        titleList = new ArrayList<>();
        countList = new ArrayList<>();

        dbManager = new DBManager(this);
        try {
            dbManager.open();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }


        //
        //
        ExpandableListView expandableListView = findViewById(R.id.exListView);
        expandableListDetail = initExpList();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new ExpListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(myOnChildClickListener);
        expandableListView.setOnGroupClickListener(myOnGroupClickListener);
        expandableListView.setOnGroupCollapseListener(myOnGroupCollapseListener);
        expandableListView.setOnGroupExpandListener(myOnGroupExpandListener);

//
        adapter = new Adapter(this, titleList, countList, listener);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
            }
        });
        recyclerView.setAdapter(adapter);

        initTodayData();
        updateDateTv(new Date());
        adapter.setCurrentViewDate(dateOnTitleTV);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);


        dateTitleTV.setOnClickListener(view -> {
            new DatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        recyclerView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                //   Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                navigationView.setVisibility(View.INVISIBLE);
                navigationView.animate()
                        .translationX(-navigationView.getWidth())
                        .alpha(0.0f)
                        .setDuration(300);
                return super.onTouch(v, event);
            }

            public void onSwipeRight() {
                //   Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
//                onBeforeDate(dateTitleTV);
                navigationView.animate()
                        .translationX(0)
                        .alpha(1.0f)
                        .setDuration(300);
                navigationView.setVisibility(View.VISIBLE);

            }

            public void onSwipeLeft(MotionEvent e) {
                //onAfterDate(dateTitleTV);
                navigationView.setVisibility(View.INVISIBLE);
                navigationView.animate()
                        .translationX(-navigationView.getWidth())
                        .alpha(0.0f)
                        .setDuration(300);
            }

            public void onSwipeBottom() {
                // ToDo
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateOnTitleTV);
                updateDataForDate(cal);
                //getDataForDate(dateOnTitleTV);
                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();

            }
        });

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                // Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                //   Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                onBeforeDate(dateTitleTV);

            }

            public void onSwipeLeft(MotionEvent e) {
                onAfterDate(dateTitleTV);
            }

            public void onSwipeBottom() {
                //#ToDo Refresh data
            }

            public void myOnLongPress() {
                DatePickerDialog datePickerDialog = new DatePickerDialog(dateTitleTV.getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateData(myCalendar.getTime());
                    }
                });
                datePickerDialog.show();
            }
        };
        dateTitleTV.setOnTouchListener(onSwipeTouchListener);

        addNewSetTVBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbManager.insertNewSet("2_squad", 0);
                //#Todo If insert success refresh expandableListView
                //expandableListAdapter.setNewItems();
                refreshExListView();
            }
        });
    }

    private void refreshExListView() {
        expandableListDetail = initExpList();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter.setNewItems(expandableListTitle,expandableListDetail);
        expandableListAdapter.notifyDataSetChanged();
    }

    private HashMap<String, List<String>> initExpList() {

        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        Cursor cursor = dbManager.selectTest();
        List<String> exChildItem;
        if (cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {
                String set_name = cursor.getString(cursor.getColumnIndex("set_name"));
                System.out.println("SETS = " + set_name);
                Cursor cursor2 = dbManager.selectExs(set_name);
                exChildItem = new ArrayList<String>();

                if (cursor2.getCount() >= 1) {
                    while (cursor2.moveToNext()) {
                        String ex_name = cursor2.getString(cursor2.getColumnIndex("ex_name"));
                        System.out.println("EXERCISE = " + ex_name);
                        exChildItem.add(ex_name);
                    }
                }
                expandableListDetail.put(set_name, exChildItem);
                cursor2.close();
            }
        }
        cursor.close();
        return expandableListDetail;
    }

    public Date getDateOnTitleTV() {
        return dateOnTitleTV;
    }

    public void updateDateTv(Date date) {
        dateOnTitleTV = date;
        dateTitleTV.setText(TITLE_DATE_FORMAT.format(dateOnTitleTV));
    }

    public void onBeforeDate(View view) {
        updateData(-1);
    }

    public void onAfterDate(View view) {
        updateData(1);
    }

    public void updateData(int forDay) {
        Calendar cal = addDayToTitle(dateOnTitleTV, forDay);
        updateDataForDate(cal);
    }

//    public boolean getDataForDate(Date date) {
//        Cursor cursor = dbManager.selectByDate(date);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//
//        if (cursor.getCount() <= 0) {
//            Toast toast = Toast.makeText(getApplicationContext(),
//                    "No DATA for " + TITLE_DATE_FORMAT.format(date), Toast.LENGTH_SHORT);
//            toast.show();
//            return false;
//        } else {
//            clearAllListData();
//            updateDateTv(date);
//            adapter.setCurrentViewDate(dateOnTitleTV);
//            while (cursor.moveToNext()) {
//                initLists(cursor);
//            }
//            if (!sdf.format(TODAY).equals(sdf.format(dateOnTitleTV))) {
//                addNewExBtn.setVisibility(View.INVISIBLE);
//            } else {
//                addNewExBtn.setVisibility(View.VISIBLE);
//            }
//            adapter.notifyDataSetChanged();
//            return true;
//        }
//    }

    public void updateData(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        updateDataForDate(cal);
    }

    private void updateDataForDate(Calendar cal) {
        Cursor cursor = dbManager.selectByDate(cal.getTime());
        if (cursor.getCount() >= 1) {
            clearAllListData();
            updateDateTv(cal.getTime());
            while (cursor.moveToNext()) {
                initLists(cursor);
            }
            adapter.setCurrentViewDate(dateOnTitleTV);
        } else {
            clearAllListData();
            updateDateTv(cal.getTime());
            adapter.setCurrentViewDate(dateOnTitleTV);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No DATA for " + dateTitleTV.getText(), Toast.LENGTH_SHORT);
            toast.show();
        }

        Calendar cal_new = Calendar.getInstance();
        cal_new.setTime(TODAY);
        if (cal_new.get(Calendar.DAY_OF_YEAR) != cal.get(Calendar.DAY_OF_YEAR)) {
            addNewExBtn.setVisibility(View.INVISIBLE);
        } else {
            addNewExBtn.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void clearAllListData() {
        titleList.clear();
        countList.clear();
    }

    public void initLists(Cursor cursor) {
        titleList.add(cursor.getString(cursor.getColumnIndex("title")));
        countList.add(cursor.getString(cursor.getColumnIndex("counter")));
    }

    public boolean initTodayData() {
        setTodayData();
        adapter.notifyDataSetChanged();
        return true;
    }

    public void setTodayData() {
        Cursor cursor = dbManager.selectByDate(new Date());
        if (cursor.getCount() <= 0) {
            Calendar cal = addDayToTitle(new Date(), -1);
            cursor = dbManager.selectByDate(cal.getTime());
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
                initLists(cursor);

                Log.println(Log.DEBUG, "SELECT: ", cursor.getString(cursor.getColumnIndex("title")));
                Log.println(Log.DEBUG, "SELECT: ", cursor.getString(cursor.getColumnIndex("counter")));
                Log.println(Log.DEBUG, "SELECT: ", cursor.getString(cursor.getColumnIndex("title")));
                Log.println(Log.DEBUG, "SELECT: ", cursor.getString(cursor.getColumnIndex("_date")));
            }
        }
        adapter.notifyDataSetChanged();
        updateDateTv(new Date());
    }

    public void initDefaultTitles() {
        titleList.addAll(Arrays.asList("PushUP", "PullUP", "ABS"));
        countList.addAll(Arrays.asList("0", "0", "0"));
        for (String title : titleList) {
            dbManager.insert(title, 0, new Date(), "default");
        }
    }

    public void onAddNewItem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        final View dialogView = inflater.inflate(R.layout.add_new_item_dialog, null);
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setPositiveButton("Create", (dialog, id) -> {
                    TextInputEditText newExTitle = dialogView.findViewById(R.id.newExInpText);
                    TextInputEditText newExDesc = dialogView.findViewById(R.id.newExeDescInpTxt);
                    String newTitle = newExTitle.getText().toString();
                    String newDesc = newExDesc.getText().toString();
                    //#ToDo check is empty, check if exists - do not create
                    if (!newTitle.isEmpty()) {
                        dbManager.insert(newTitle, 0, new Date(), newDesc);
                        titleList.add(newTitle);
                        countList.add("0");
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Title can't be Empty! \n Not Created!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        builder.create();
        builder.show();
    }

    private void updateLabel(Date foDate) {
        //if (getDataForDate(foDate)) {
        dateTitleTV.setText(TITLE_DATE_FORMAT.format(foDate));
        //}
    }

    public void onOpenChartsView(View view) {
        Intent ChartsActivityIntent = new Intent(MainActivity.this, ChartsActivity.class);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        cal.setTime(dateOnTitleTV);// all done

        ChartsActivityIntent.putExtra("dateRange", cal.toString()); //Optional parameters
        MainActivity.this.startActivity(ChartsActivityIntent);

    }

}