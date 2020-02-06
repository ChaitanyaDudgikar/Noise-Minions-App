package com.example.beproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Callibration extends AppCompatActivity
{
    TextView t1,t2,t3,t4;
    Button b1,b2,b3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callibration);
        t1=findViewById(R.id.textViewb1);
        t2=findViewById(R.id.textViewb2);
        t3=findViewById(R.id.textViewb3);
        b1=findViewById(R.id.button1);
        b2=findViewById(R.id.button2);
        b3=findViewById(R.id.button3);
        t4=findViewById(R.id.textarea);


        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Toast.makeText(getApplicationContext(), "Your Device is Calibrating", Toast.LENGTH_LONG).show();
                ((NoiseMinionApplication)getApplication()).isCalibratinglow=true;
                Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
                startService(serviceIntent);
            }
        });
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "Your Device is Calibrating", Toast.LENGTH_LONG).show();
                ((NoiseMinionApplication)getApplication()).isCalibratingmedium=true;
                Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
                startService(serviceIntent);
            }
        });
        b3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "Your Device is Calibrating", Toast.LENGTH_LONG).show();
                ((NoiseMinionApplication)getApplication()).isCalibratinghigh=true;
                Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
                startService(serviceIntent);
            }
        });

    }
}
