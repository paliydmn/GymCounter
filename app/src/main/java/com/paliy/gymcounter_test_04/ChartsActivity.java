package com.paliy.gymcounter_test_04;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.ArrayMap;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class ChartsActivity extends AppCompatActivity {

    private Statistic stat;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String cal = intent.getStringExtra("dateRange");

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(cal));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_charts);

        int lastMonthDay = getLastDayOfMonthUsingCalendar(calendar.get(Calendar.MONTH));
        // ToDo remove hardcode and rewrite the Deprecated API
        Date end = new Date(calendar.get(Calendar.YEAR) -1900, calendar.get(Calendar.MONTH), lastMonthDay);
        Date start = new Date(calendar.get(Calendar.YEAR) -1900, calendar.get(Calendar.MONTH), 1);
        stat = new Statistic(this, start, end);
        String chartTitle = stat.getDateRange();
        BarChart chart = (BarChart) findViewById(R.id.chart);

        Description description = new Description();
        description.setText(chartTitle);

        BarData data = new BarData(getDataSet());
        chart.setData(data);
        chart.setDescription(description);
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    static int getLastDayOfMonthUsingCalendar(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private ArrayList getDataSet() {
        ArrayList dataSets = new ArrayList();

        ArrayMap<String, Integer> map = (ArrayMap<String, Integer>) stat.getStatisticMapForDateRange();
        ArrayList valueSet1;
        float c = 0;
        BarDataSet barDataSet1;
        Random rnd = new Random();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            valueSet1 = new ArrayList();
            valueSet1.add(new BarEntry(c++, entry.getValue()));
            barDataSet1 = new BarDataSet(valueSet1, entry.getKey());
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            barDataSet1.setColors(color);
            dataSets.add(barDataSet1);
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        return dataSets;
    }
}