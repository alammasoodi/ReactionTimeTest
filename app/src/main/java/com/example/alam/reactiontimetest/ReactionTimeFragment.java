package com.example.alam.reactiontimetest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;


public class ReactionTimeFragment extends Fragment implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float lastX;
    private float deltaX = 0;
    private float vibrateThreshold = 0;
    private ImageView blueDot;
    private Vibrator vibrator;
    private int shakeOnTimeNum = 0;


    public ReactionTimeFragment() {

    }

    public static ReactionTimeFragment newInstance() {
        ReactionTimeFragment fragment = new ReactionTimeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reaction_time, container, false);
        blueDot = view.findViewById(R.id.textView);
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        startCountDown();
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        return view;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void getAccelerometer(SensorEvent event) {
        deltaX = Math.abs(lastX - event.values[0]);
        if (deltaX < 10) {
            deltaX = 0;
        }
        vibrate();
    }

    public void vibrate() {
        if ((deltaX > vibrateThreshold)) {
            shakeOnTimeNum++;
            Toast.makeText(getActivity(), "shake detected", Toast.LENGTH_SHORT).show();
            vibrator.vibrate(100);
            unregisterListener();
        }
    }

    public void startCountDown() {
        new CountDownTimer(24000, 500) {
            int counter = 0;

            @Override
            public void onTick(long l) {
                counter++;
                if (counter == 6) {
                    registerListener();
                    lastX = 0;
                    blueDot.setVisibility(View.VISIBLE);
                    counter = 0;
                }
                if (counter == 1) {
                    unregisterListener();
                    blueDot.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFinish() {
                Toast.makeText(getActivity(), "correct number of shakes " + shakeOnTimeNum, Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    public void registerListener() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void unregisterListener() {
        mSensorManager.unregisterListener(this);

    }

}
