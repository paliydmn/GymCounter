package com.paliy.gymcounter_test_04;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.paliy.gymcounter_test_04.adapters.Adapter;
import com.paliy.gymcounter_test_04.adapters.ExpListAdapter;
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
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
    private static final Date TODAY = Calendar.getInstance().getTime();
    final Calendar myCalendar = Calendar.getInstance();
    private final ExpandableListView.OnChildClickListener myOnChildClickListener = (parent, v, groupPosition, childPosition, id) -> true;
    private final ExpandableListView.OnGroupCollapseListener myOnGroupCollapseListener = groupPosition -> {
        // group collapse at groupPosition
        System.out.println("OnGroupCollapseListener");
    };
    private final ExpandableListView.OnGroupClickListener myOnGroupClickListener = (parent, v, groupPosition, id) -> {
        System.out.println("OnGroupClickListener");
        return false;
    };
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private int lastExpandedPosition = -1;
    private List<String> titleList;
    private List<String> countList;
    private TextView dateTitleTV;
    final DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);
        updateLabel(myCalendar.getTime());
    };
    private TextView addNewSetTVBtn;
    private TextView titleOnMainTV;
    private FloatingActionButton addNewExBtn;
    private Date dateOnTitleTV;
    private Adapter mViewHolderAdapter;
    private ExpandableListView expandableListView;
    private final ExpandableListView.OnGroupExpandListener myOnGroupExpandListener = groupPosition -> {
        // group expand at groupPosition
        if ((lastExpandedPosition != -1) && (groupPosition != lastExpandedPosition)) {
            expandableListView.collapseGroup(lastExpandedPosition);
        }
        lastExpandedPosition = groupPosition;
        System.out.println("OnGroupExpandListener");
    };
    private ExpListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private AdapterView.OnClickListener listener;
    private DBManager dbManager;
    private final View.OnClickListener onAddNewSetListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.new_set_form, null);
            builder.setView(dialogView)
                    .setPositiveButton("Add", (dialog, id) -> {
                        EditText set_name = dialogView.findViewById(R.id.setNameEdText);
                        String setNameStr = set_name.getText().toString();
                        //#ToDo check is empty, check if exists - do not create
                        if (!setNameStr.isEmpty() && !hasSpecialSym(setNameStr)) {
                            if (dbManager.insertNewSet(setNameStr, 0)) {
                                refreshExListView();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Set Name should be Unique! \n Not Created!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Set Name can't be Empty or Contain Special Symbols! \n Not Created!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {
                    });
            builder.create();
            builder.show();
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
        dateTitleTV = findViewById(R.id.tvDate);
        titleOnMainTV = findViewById(R.id.titleOnMainTV);

        titleList = new ArrayList<>();
        countList = new ArrayList<>();

        //add db manager instance
        dbManager = new DBManager(this);
        try {
            dbManager.open();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        //Expandable List View block
        expandableListView = findViewById(R.id.exListView);
        //Init exList with data from db and update
        expandableListDetail = initExpList();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new ExpListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(myOnChildClickListener);
        expandableListView.setOnGroupClickListener(myOnGroupClickListener);
        expandableListView.setOnGroupCollapseListener(myOnGroupCollapseListener);
        expandableListView.setOnGroupExpandListener(myOnGroupExpandListener);

        expandableListAdapter.setOnClickHandler(this::mOnClickExpListListener);

        mViewHolderAdapter = new Adapter(this, titleList, countList, listener, getSupportFragmentManager());

        mViewHolderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
            }
        });
        //#ToDo seems this setAdapter is redundant
//        recyclerView.setAdapter(mViewHolderAdapter);

        initTodayData();
        updateDateTv(new Date());
        mViewHolderAdapter.setCurrentViewDate(dateOnTitleTV);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mViewHolderAdapter);


        dateTitleTV.setOnClickListener(view -> new DatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        OnSwipeTouchListener onRecyclerViewSwipeTouchListener = new OnSwipeTouchListener(this) {
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
                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();

            }
        };
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
                datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateData(myCalendar.getTime());
                });
                datePickerDialog.show();
            }
        };

        recyclerView.setOnTouchListener(onRecyclerViewSwipeTouchListener);
        dateTitleTV.setOnTouchListener(onSwipeTouchListener);
        addNewSetTVBtn.setOnClickListener(onAddNewSetListener);
    }

    private void refreshExListView() {
        expandableListDetail = initExpList();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter.setNewItems(expandableListTitle, expandableListDetail);
        expandableListAdapter.notifyDataSetChanged();
    }

    private HashMap<String, List<String>> initExpList() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<>();
        Cursor cursor = dbManager.selectTest();
        List<String> exChildItem;
        if (cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {
                String set_name = cursor.getString(cursor.getColumnIndex("set_name"));
                System.out.println("SETS = " + set_name);
                Cursor cursor2 = dbManager.selectExs(set_name);
                exChildItem = new ArrayList<>();
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
            mViewHolderAdapter.setCurrentViewDate(dateOnTitleTV);
        } else {
            clearAllListData();
            updateDateTv(cal.getTime());
            mViewHolderAdapter.setCurrentViewDate(dateOnTitleTV);
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
        mViewHolderAdapter.notifyDataSetChanged();
    }

    private void clearAllListData() {
        titleList.clear();
        countList.clear();
    }

    public void initLists(Cursor cursor) {
        String _title = cursor.getString(cursor.getColumnIndex("title"));
        String _counter = cursor.getString(cursor.getColumnIndex("counter"));
        if (!titleList.contains(_title)) {
            titleList.add(_title);
            countList.add(_counter);
        }
    }

    public boolean initTodayData() {
        setTodayData();
        mViewHolderAdapter.notifyDataSetChanged();
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
        mViewHolderAdapter.notifyDataSetChanged();
        updateDateTv(new Date());
    }

    public void initDefaultTitles() {
        titleList.addAll(Arrays.asList("PushUP", "PullUP", "Squats", "ABS"));
        countList.addAll(Arrays.asList("0", "0", "0", "0"));
        for (String title : titleList) {
            dbManager.insert(title, 0, new Date(), "default");
        }
    }

    public void applySetToMain(String setName) {
        dbManager.insertSetMain(setName);
        setTodayData();
    }

    //Click on Floating button. Add new exercise
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
                    if (!newTitle.isEmpty() && !titleList.contains(newTitle) && !hasSpecialSym(newTitle) && !hasSpecialSym(newDesc)) {
                        dbManager.insert(newTitle, 0, new Date(), newDesc);
                        titleList.add(newTitle);
                        countList.add("0");
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Title can't be Empty, contain Special Symbols Or it is already exists! \n Not Created!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        builder.create();
        builder.show();
    }

    private void updateLabel(Date foDate) {
        dateTitleTV.setText(TITLE_DATE_FORMAT.format(foDate));
    }

    public void onOpenChartsView(View view) {
        Intent ChartsActivityIntent = new Intent(MainActivity.this, ChartsActivity.class);
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal.setTime(dateOnTitleTV);// all done
        ChartsActivityIntent.putExtra("dateRange", cal.toString()); //Optional parameters
        MainActivity.this.startActivity(ChartsActivityIntent);
    }

    private void mOnClickExpListListener(OnClickActions action, String setName, String newName, String old_name) {
        System.out.println(action);
        System.out.println("Here! ->" + setName + " -> " + newName);
        switch (action) {
            case DELETE_EXERCISE:
                dbManager.deleteExByExNameSetName(newName, setName);
                refreshExListView();
                break;
            case EDIT_EXERCISE:
                dbManager.editExByExNameSetName(newName, setName, old_name);
                refreshExListView();
                break;
            case CREATE_EXERCISE:
                //Add New Ex to current SET
                AlertDialog.Builder builder = new AlertDialog.Builder(navigationView.getContext());
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.new_exersice_form, null);
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(dialogView)
                        .setPositiveButton("Add", (dialog, id1) -> {
                            EditText ex_name = dialogView.findViewById(R.id.exNameEditT);
                            EditText ex_descr = dialogView.findViewById(R.id.exDescrEditT);
                            //#ToDo check is empty, check if exists - do not create
                            String exNameStr = ex_name.getText().toString();
                            String exDescStr = ex_descr.getText().toString();
                            if (!exNameStr.isEmpty() && !hasSpecialSym(exNameStr) && !hasSpecialSym(exDescStr)) {
                                System.out.println("ADD NEW Ex!");
                                dbManager.insertNewExToSetRaw(setName, exNameStr, exDescStr, 0);
                                //#Todo If insert success refresh expandableListView
                                refreshExListView();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Exercise Name can't be Empty! \n Not Created!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, id1) -> {
                        });
                builder.create();
                builder.show();
                break;
            case DELETE_SET:
                dbManager.deleteSetByName(setName);
                refreshExListView();
                break;
            case EDIT_SET:
                dbManager.editSetNameByName(newName, old_name);
                refreshExListView();
                break;
            case SUBMIT_SET_TO_MAIN_VIEW:
                clearAllListData();
                applySetToMain(setName);
                titleOnMainTV.setText(setName);
                //  refreshExListView();
                break;
        }
    }

    public boolean hasSpecialSym(String checkStr) {
        return Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE).matcher(checkStr).find();
    }
}