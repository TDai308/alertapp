package com.example.alertapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btstart,btstop;
    ImageView lock;
    EditText pass1,pass2;
    TextView tv1,tv2;
    int password;
    int i =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lock = (ImageView)findViewById(R.id.lock);
        tv1 = (TextView)findViewById(R.id.notification);
        tv2 = (TextView)findViewById(R.id.thongbao);
        btstart = (Button)findViewById(R.id.bstart);
        btstop = (Button)findViewById(R.id.bstop);
        pass1 = (EditText)findViewById(R.id.passstart);
        pass2 = (EditText)findViewById(R.id.passstop);
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(this, Uri.parse("android.resource://"+ this.getPackageName()+ "/raw/hehehe"));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},0);
        SmsManager m = SmsManager.getDefault();
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final SensorManager ss = (SensorManager)getSystemService(SENSOR_SERVICE);
        final Sensor sensoralert = ss.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (sensoralert == null) {
            Toast.makeText(MainActivity.this,"This device doesnt have sensor!!!",Toast.LENGTH_LONG).show();
        }else {
            final SensorEventListener sslistener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float x = event.values[0];
                    if (x <5) {
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                        mp.start();
                        mp.setLooping(true);
//                            m.sendTextMessage("so dien thoai",null,"tin nhan",null,null);
//                            Toast.makeText(MainActivity.this, "ĐÃ GỬI TIN NHẮN", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            btstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pass1.getText().toString().trim().length() == 0) {
                        Toast.makeText(MainActivity.this, "Type a password to start please!!!",Toast.LENGTH_LONG).show();
                    } else {
                        password = Integer.parseInt(pass1.getText().toString());
                        ss.registerListener(sslistener, sensoralert, 1 * 1000 * 1000);
                        lock.setImageResource(R.drawable.lock);//thay doi hinh anh cho imageView
                        tv1.setText("The protection system is ON");
                        tv2.setText("Type the correct password to stop the protection");
                        pass1.setText("");
                        pass1.setHint("password to start");
                        pass1.setEnabled(false);
                        pass2.setEnabled(true);
                        btstop.setEnabled(true);
                        btstart.setEnabled(false);
                    }
                }
            });
            btstop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pass2.getText().toString().trim().length() == 0) {
                        Toast.makeText(MainActivity.this, "Type the password to stop", Toast.LENGTH_LONG).show();
                    } else {
                        if (Integer.parseInt(pass2.getText().toString()) == password) {
                            i=0;
                            ss.unregisterListener(sslistener,sensoralert);
                            mp.pause();
                            lock.setImageResource(R.drawable.unlock);
                            tv1.setText("The protection system is OFF");
                            tv2.setText("Set up a password to start the protection");
                            btstart.setEnabled(true);
                            pass1.setEnabled(true);
                            pass2.setText("");
                            pass2.setHint("password to stop");
                            pass2.setEnabled(false);
                            btstop.setEnabled(false);
                        }
                        else {
                            i++;
                            Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_LONG).show();
                        }
                        if(i>=3){
                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},1);
                            }
                            else
                            {
                                Intent call = new Intent();
                                call.setAction(Intent.ACTION_CALL);
                                call.setData(Uri.parse("tel:0969254303"));
                                startActivity(call);
                            }
                        }
                    }
                }
            });
        }

    }

}