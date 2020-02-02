package com.example.beproject;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{
    Button b1, b2,b3;


    //    String[] appPermissions = {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_FINE_LOCATION
//    };
//
//    private static final int PERMISSION_REQUEST_CODE=1240;
//
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int Permission_All = 1;

        String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(this, Permissions))
        {
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
            GpsUtils gu = new GpsUtils(this);
            Log.d("GPS", "Turning GPS ON");
            gu.turnGPSOn(new GpsUtils.onGpsListener()
            {
                @Override
                public void gpsStatus(boolean isGPSEnable)
                {
                    Log.d("GPS", "GPS Enabled:" + isGPSEnable);
                }


            });
        } else
        {
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
            GpsUtils gu = new GpsUtils(this);
            Log.d("GPS", "Turning GPS ON");
            gu.turnGPSOn(new GpsUtils.onGpsListener()
            {
                @Override
                public void gpsStatus(boolean isGPSEnable)
                {
                    Log.d("GPS", "GPS Enabled:" + isGPSEnable);
                }


            });
        }

//        Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
//        startService(serviceIntent);

//        if(checkAndRequestPermission())
//        {
//            initApp();
//
//        }

        b1 = findViewById(R.id.start);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                SharedPreferences pref=getSharedPreferences("Unique_User_id",MODE_PRIVATE);
                if(pref.getFloat("basedb",-1)==-1)
                {
                    Toast.makeText(getApplicationContext(),"Please calibrate the device", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
                    startService(serviceIntent);
                    b2.setEnabled(true);
                    b1.setEnabled(false);

                }

                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);


                // Intent m=new Intent(getApplicationContext(),MapsActivity.class);
                // startActivity(m);
            }
        });
        b2 = findViewById(R.id.stop);
        b2.setEnabled(false);
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
                stopService(serviceIntent);

                b1.setEnabled(true);
                b2.setEnabled(false);
            }
        });
        b3=findViewById(R.id.callib);
        b3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                ((NoiseMinionApplication)getApplication()).isCalibrating=true;
//                Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
//                startService(serviceIntent);
                  Intent i =new Intent(getApplicationContext(),Callibration.class);
                  startActivity(i);
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
//
//    private boolean checkAndRequestPermission()
//    {
//        List<String>listPermmisionNeeded=new ArrayList<>();
//        for(String perm:appPermissions)
//        {
//            if(ContextCompat.checkSelfPermission((this,perm)!= PackageManager.PERMISSION_GRANTED)
//            {
//                listPermmisionNeeded.add(perm);
//            }
//
//
//        }
//
//        if(!listPermmisionNeeded.isEmpty())
//        {
//            ActivityCompat.requestPermissions(this,
//                    listPermmisionNeeded.toArray(new String[listPermmisionNeeded.size()]),
//                    PERMISSION_REQUEST_CODE
//            );
//            return false;
//        }
//
//
//        return true;
//    }


