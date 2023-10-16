package com.paliy.gymcounter_test_04;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Locale;


public class PopupScreen extends DialogFragment implements SensorEventListener, PopUpOnClickHandler {
    public static String TAG = PopupScreen.class.getSimpleName();

    // private static final String TAG = "ORIENTATION";
    public double UP_THRESHOLD;
    public double DOWN_THRESHOLD;

    private long startTime;
    private int c = 0;
    private int currentTime = 0;
    private boolean isUp = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private File file;
    private TextView countTV;
    private Button startStop;
    private boolean isRunning = false;
    private TextToSpeech toSpeech;
    private Orientations currentOrientation;
    private MovementSpeed mSpeed;
    private int timeStep = 500;


    PopUpOnClickHandler handler;

    private AdapterView.OnItemSelectedListener dropDownListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            mSpeed = MovementSpeed.valueOf(parentView.getItemAtPosition(position).toString());
            switch (mSpeed) {
                case FAST:
                    UP_THRESHOLD = StaticData.FAST_H;
                    DOWN_THRESHOLD = StaticData.FAST_L;
                    break;
                case AVERAGE:
                    UP_THRESHOLD = StaticData.AVERAGE_H;
                    DOWN_THRESHOLD = StaticData.AVERAGE_L;
                    break;
                case SLOW:
                    UP_THRESHOLD = StaticData.SLOW_H;
                    DOWN_THRESHOLD = StaticData.SLOW_L;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }
    };

    private void mOnClick(OnClickActions action, int value) {
        System.out.println(action);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {


        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.counting_pop_up, null);
        countTV = v.findViewById(R.id.counterTV);
        startStop = v.findViewById(R.id.startCountingBtn);

        countTV.setOnClickListener(this::mOnClick);

        ImageButton close = v.findViewById(R.id.closePopUpImBtn);


        close.setOnClickListener(v1 -> dismiss());

        Spinner dropdown = v.findViewById(R.id.spinner);
        String[] items = new String[3];
        items[0] = MovementSpeed.values()[0].toString();
        items[1] = MovementSpeed.values()[1].toString();
        items[2] = MovementSpeed.values()[2].toString();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(dropDownListener);

        toSpeech = new TextToSpeech(this.getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                toSpeech.setLanguage(Locale.UK);
            }
        });

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        startTime = System.currentTimeMillis();


        startStop.setOnClickListener(view -> {
            if (startStop.getText().equals(getResources().getString(R.string.start_counting))) {
                startStop.setText(getResources().getString(R.string.stop_counting));
                isRunning = true;
                c = 0;
                countTV.setText("0");
            } else {
                startStop.setText(getResources().getString(R.string.start_counting));
                isRunning = false;
                handler.onPopUpClick(OnClickActions.EDIT_SET, Integer.parseInt(countTV.getText().toString()));

            }
            System.out.println("Start");
        });


        return v; //inflater.inflate(R.layout.counting_pop_up, container, false);
    }

    private void mOnClick(View view) {
    }

    public void setOnClickHandler(PopUpOnClickHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
                super.show(manager, tag);
    }

    private void startCounting(float accY) {

        if (!isUp && accY > UP_THRESHOLD && currentTime + timeStep <= getTimeDelta()) {
            c++;
            isUp = true;
            countTV.setText(String.valueOf(c));
            toSpeech.speak(countTV.getText(), TextToSpeech.QUEUE_FLUSH, null, null);
            currentTime = (int) getTimeDelta();
            System.out.println("Time: " + (getTimeDelta()) / 1000);
        }
        if (accY < DOWN_THRESHOLD && currentTime + timeStep <= getTimeDelta()) {
            isUp = false;
        }

    }

    @Nullable
    @Override
    public Dialog getDialog() {

        return super.getDialog();
    }
    //    public void onStartBtnClick(View view) {

//    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float accX = event.values[0];
        float accY = event.values[1];
        float accZ = event.values[2];

        String x = "";
        String y = "";
        String z = "";

        if (event.sensor == accelerometer) {
            if (Math.abs(accY) > Math.abs(accX)) {
                //Mainly portrait
                if (accY > 1) {
                    currentOrientation = Orientations.PORTRAIT;
                    Log.d(TAG, "Portrait");
                } else if (accY < -1) {
                    currentOrientation = Orientations.PORTRAIT_INVERSE;
                    Log.d(TAG, "Inverse portrait");
                }
                if (isRunning)
                    startCounting(accY);
            } else {
                //Mainly landscape
                if (isRunning)
                    startCounting(accX);
                if (accX > 1) {
                    currentOrientation = Orientations.LAND_RIGHT_UP;
                    Log.d(TAG, "Landscape - right side up");
                } else if (accX < -1) {
                    currentOrientation = Orientations.LAND_LEFT_UP;
                    Log.d(TAG, "Landscape - left side up");
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        System.out.println(this.getResources().getConfiguration().orientation);
        super.onConfigurationChanged(newConfig);
    }

    private long getTimeDelta() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {

        super.onDismiss(dialog);
    }

    @Override
    public void onPopUpClick(OnClickActions action, int countValue) {
        System.out.println("Pop UP");
    }
}