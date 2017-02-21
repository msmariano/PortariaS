package com.projetos.marcelo.porteiros;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    ImageButton IbOnOff;
	private boolean connected = false;
	public String msg;
    public String IMEI;
    int milliseconds;
    TelephonyManager telephony;
	Context context;
    boolean bOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bOk = false;
        context = getApplicationContext();
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        else
          bOk = true;

        if (bOk){
          telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
          IMEI =  telephony.getDeviceId();
        }

        IbOnOff = (ImageButton) findViewById(R.id.imageButton );
		IbOnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                boolean inv = false;
                CharSequence text = "Bemvindo!";
                int duration = Toast.LENGTH_SHORT;
                IbOnOff.setBackgroundColor(Color.parseColor("#00FF00"));
                IbOnOff.setEnabled(false);
                if (!connected) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
                delay(3);
            }
        });		
    }
	public void delay(int seconds) {
        milliseconds = seconds * 1000;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IbOnOff.setBackgroundColor(Color.parseColor("#FF0000"));
                        msg = "";
                        IbOnOff.setEnabled(true);
                    }
                }, milliseconds);
            }
        });
    }
	public class ClientThread implements Runnable
    {

        public void run()
        {
            int duration = Toast.LENGTH_SHORT;
            try
            {
                InetAddress serverAddr = InetAddress.getByName("192.168.0.14");
                Socket socket = new Socket(serverAddr,81);
                connected = true;
                boolean bEnviado = false;

                if (connected)
                {
                    try {
                        PrintWriter out = new PrintWriter(
                                new BufferedWriter(new OutputStreamWriter(
                                        socket.getOutputStream())), true);
                        out.println("act");

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));

                        try {
                            StringBuilder total = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) {
                                total.append(line);
                            }
                            msg = total.toString().trim();
                        } catch (IOException e) {
                            Toast toast = Toast.makeText(context, e.getMessage(), duration);
                            toast.show();
                        }

                    }
                    catch (Exception e)
                    {
                        Toast toast = Toast.makeText(context, e.getMessage(), duration);
                        toast.show();
                    }
                }
                socket.close();
                connected = false;
            }
            catch (Exception e)
            {
                Toast toast = Toast.makeText(context, e.getMessage(), duration);
                toast.show();
                connected = false;
            }
        }
    }
}
