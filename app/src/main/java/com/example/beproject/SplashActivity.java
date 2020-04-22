package com.example.beproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler h=new Handler();
        h.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent splash=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(splash);
                finish();
            }
        },2000);
    }
}
