package com.example.mlmotion;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final String TAG = "test" ;
    private TextView mTextView;
    private SensorManager sensorManager;
    private Sensor sensor;
    private TriggerEventListener triggerEventListener;
    String pattern = "MM/dd/yyyy HH:mm:ss";
    final DateFormat df = new SimpleDateFormat(pattern);
    private Sensor accelerometer;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private FirebaseAuth mAuth;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        mTextView = (TextView) findViewById(R.id.text);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        // Enables Always-on
        setAmbientEnabled();
        //sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

       /* triggerEventListener = new TriggerEventListener() {
            @Override
            public void onTrigger(TriggerEvent event) {
                // Do work
                mTextView.setText("worked");
                
            }
        };


        sensorManager.requestTriggerSensor(triggerEventListener, sensor);

*/

        if (sensor != null) {
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor1 : sensors) {
                Log.i(TAG, sensor1.getName() + ": " + sensor1.getType());
            }
        }



    }
    @Override
    public void onStart() {


         super.onStart();

         sensorManager.registerListener(this, accelerometer,
             SensorManager.SENSOR_DELAY_UI);

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;

            mAccelCurrent = (float)Math.sqrt(x*x + y*y + z*z);
            float delta = mAccelCurrent - mAccelLast;

            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if(mAccel > 1){

                Log.e("Movement","working");

               Date currentTime = Calendar.getInstance().getTime();
                String TimeString = df.format(currentTime);
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("Movement Detected", "True");
                hashMap.put("Time", TimeString);

                reference.child("Movement").child("Loc1").setValue(hashMap);

                // do something
            }
        }

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

