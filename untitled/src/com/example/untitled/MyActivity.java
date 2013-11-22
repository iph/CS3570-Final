package com.example.untitled;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MyActivity extends Activity  implements SensorEventListener{

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    //private TextView output;
    private Handler handler;
    private boolean initialized;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final float NOISE = (float) 2.0;
    private float mLastX, mLastY, mLastZ;


    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "10.0.0.11";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //output = (TextView)findViewById(R.id.textView);
        handler = new Handler();

        initialized = false;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        new Thread(new ClientThread()).start();
    }

    public void onClick(View view) {
        try {
            EditText et = (EditText) findViewById(R.id.editText);
            String str = et.getText().toString();
            out.println(str);
            out.println("Hello world");

        } catch (Exception e) {
            Log.e("pew", "Shit." + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        boolean hasChangedLittle = Math.abs(mLastX - x) + Math.abs(mLastY - y) < .2;
        if (!initialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            initialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            if (deltaX < NOISE) deltaX = (float)0.0;
            if (deltaY < NOISE) deltaY = (float)0.0;
            if (deltaZ < NOISE) deltaZ = (float)0.0;
            mLastX = x;
            mLastY = y;
            mLastZ = z;

        }

        if(!hasChangedLittle && out != null){
            out.println("OUT:" + x + "," + y + "," + z);
        }

        }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    class ServerThread implements Runnable {

        @Override
        public void run() {
            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while((!Thread.currentThread().isInterrupted())){
                    Log.d("pew", in.toString());
                        String currentText = in.readLine();
                        Log.d("pew", handler.toString());
                    handler.post(new updateUIThread(currentText));

                }
            }catch(Exception e){

                Log.e("pew", e.toString());
            }
        }
    }


    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                socket = new Socket(serverAddr, SERVERPORT);
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                new Thread(new ServerThread()).start();


            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                Log.d("pew", "WHERE AM I");
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e("pew", "I AM DOWN " + e1.toString());
            }

        }

    }

    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            //output.setText(output.getText().toString()+"Client Says: "+ msg + "\n");
        }

    }

}