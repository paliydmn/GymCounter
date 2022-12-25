package com.paliy.gymcounter_test_04;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> abc;
    List<Integer> abcImage;

    Adapter adapter;

    private  AdapterView.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        abc = new ArrayList<>();
        abcImage = new ArrayList<>();


        abc.add("Test_1");
        abc.add("Test_2");
        abc.add("Test_3");
        abc.add("Test_4");
        abc.add("Test_5");

        adapter = new Adapter(this, abc, abcImage, listener);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);


    }

}