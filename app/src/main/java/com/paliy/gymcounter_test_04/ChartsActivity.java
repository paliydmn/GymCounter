package com.paliy.gymcounter_test_04;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ChartsActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        mTextView = (TextView) findViewById(R.id.text);

    }
}