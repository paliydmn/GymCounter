package com.paliy.gymcounter_test_04;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> title;
    List<String> count;
    //List<Integer> abcImage;

    Adapter adapter;

    private  AdapterView.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        title = new ArrayList<>();
        count = new ArrayList<>();
       // abcImage = new ArrayList<>();


        title.add("PushUp");
        title.add("PullUp");
        title.add("Squatting");
        title.add("ABS");
        title.add("Test_5");
        count.add("90");
        count.add("65");
        count.add("70");
        count.add("80");
        count.add("80");

        adapter = new Adapter(this, title, count, listener);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);


    }

}