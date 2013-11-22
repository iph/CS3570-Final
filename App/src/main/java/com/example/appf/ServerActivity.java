package com.example.appf;


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
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ServerActivity extends Activity implements View.OnClickListener {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    //private TextView output;
    private Handler handler;

    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "10.0.0.11";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
        //output = (TextView)findViewById(R.id.textView);
        handler = new Handler();
        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);

        //new Thread(new SocketThread()).start();
    }

    public void onClick(View view) {
        try {
            EditText et = (EditText) findViewById(R.id.editText);
            String server_ip = et.getText().toString();
            EditText et2 = (EditText) findViewById(R.id.editText2);
            String server_port = et2.getText().toString();
            //out.println(str);
            Intent intent = new Intent(this, CS3570.class);
            if(server_ip != null && server_ip.length() > 0)
                intent.putExtra("server_name", server_ip);
            if(server_port != null && server_port.length() > 0)
                intent.putExtra("server_port", server_port);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("pew", "Shit." + e.toString());
            e.printStackTrace();
        }
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


    class SocketThread implements Runnable {

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