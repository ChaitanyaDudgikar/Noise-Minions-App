package com.example.beproject;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.UUID;


public class Main2Activity extends AppCompatActivity
{


    Button b1, b2, b3;
    AudioRecord audioRecord;
    Boolean isRecorderStart = null;
    //ByteArrayOutputStream baos;
    Handler handler = new Handler();
    ShortBuffer shortBuffer;
    
    public double Lat, Long;
    Location location;
    class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            // Do something with the location
            Toast.makeText(getBaseContext(), "Location changed!", Toast.LENGTH_SHORT).show();
            Log.i("Provider: ", location.getProvider());
            Log.i("Latitude: ", String.valueOf(location.getLatitude()));
            Log.i("Longitude: ", String.valueOf(location.getLongitude()));
            Lat = location.getLatitude();
            Long = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.i("onStatusChanged: ", "Do something with the status: " + status);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.i("onProviderEnabled: ", "Do something with the provider-> " + provider);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.i("onProviderDisabled:", "Do something with the provider-> " + provider);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
//        startService(serviceIntent);s

        MyLocationListener ml = new MyLocationListener();
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        try
        {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, ml);
        } catch (SecurityException se)
        {
            System.err.println("Location access not permitted");
            se.printStackTrace();
        }


        b1 = (Button) findViewById(R.id.Open_map);
        b2 = (Button) findViewById(R.id.start_record);
        b3 = (Button) findViewById(R.id.stop_record);
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);

            }
        });

        b2.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
// Make handler and apply to start and stop
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        Log.d("Log", "Started recording");

                        int bufferLength = 0;
                        int bufferSize;
                        short[] audioData;
                        int bufferReadResult;
                        final String TAG = "Main2Activity";
//                Boolean isAudioRecording;
//            MediaRecorder recorder;


                        try

                        {
                            byte sampleAudioBitRate = 8;
                            bufferSize = AudioRecord.getMinBufferSize(22050, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                            Log.d("Main2Activity", "Buffersize=" + bufferSize);
                            if (bufferSize <= 2048)
                            {
                                bufferLength = 2048;
                            } else if (bufferSize <= 4096)
                            {
                                bufferLength = 4096;
                            }

                            /* set audio recorder parameters, and start recording */
                            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050,
                                    AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferLength);

                            audioData = new short[bufferLength * 2];
                            audioRecord.startRecording();
                            Log.d(TAG, "audioRecord.startRecording()");

                            isRecorderStart = true;

                            //baos = new ByteArrayOutputStream();
                            short[] backbuffer = new short[1045760];
                            shortBuffer = ShortBuffer.wrap(backbuffer);

                            /* ffmpeg_audio encoding loop */
                            while (isRecorderStart)
                            {
                                bufferReadResult = audioRecord.read(audioData, 0, audioData.length);

                                //baos.write(audioData, 0, bufferReadResult);
                                shortBuffer.put(audioData, 0, bufferReadResult);

//                    if (bufferReadResult == 1024 && isRecorderStart)
//                    {
//                        Buffer realAudioData1024 = ShortBuffer.wrap(audioData, 0, 1024);
//                        audioRecord.read(realAudioData1024, 1024);
//
//
//                    } else if (bufferReadResult == 2048 && isRecorderStart)
//                    {
//                        Buffer realAudioData2048_1 = ShortBuffer.wrap(audioData, 0, 1024);
//                        Buffer realAudioData2048_2 = ShortBuffer.wrap(audioData, 1024, 1024);
//                        for (int i = 0; i < 2; i++)
//                        {
//                            if (i == 0)
//                            {
//
//                                recorder.record(realAudioData2048_1);
//
//
//                            } else if (i == 1)
//                            {
//
//                                recorder.record(realAudioData2048_2);
//
//
//                            }
//                        }
//                    }
                            }


                        } catch (Exception e)
                        {
                            Log.e(TAG, "get audio data failed:" + e.getMessage() + e.getCause() + e.toString(), e);
                        }
                    }
                }).start();
            }
        });  //clicklistener

        b3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        isRecorderStart = false;

                        if (audioRecord != null)
                        {
                            try
                            {
                                audioRecord.stop();
                                audioRecord.release();

                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            audioRecord = null;
                        }
//                        final byte[] audioRecording = baos.toByteArray();
                        final short[] audioRecording = shortBuffer.array();
                        long sum = 0;
                        double db = 0;
//                        for (short b : audioRecording)
                        int l = shortBuffer.position();
                        short[] backbuffer = shortBuffer.array();
                        short max = Short.MIN_VALUE;
                        for (int i = 0; i < l; i++)
                        {
                            short b = backbuffer[i];
                            if (b < 0) b = (short) -b;
                            if (max < b) max = b;
                            b += 32768;

                            //db+=20*Math.log10(0.00002+((0.6325-0.00002)*b/128)/0.00002);

//                            sum += (b * b);
                        }
                        System.out.println(Arrays.toString(audioRecording));
                        System.out.println("MAX:" + max + " LogMax: " + Math.log10(max));

//                        final double rms =  Math.sqrt(sum) / audioRecording.length;
//                        db=20*Math.log10(0.00002+((((0.6325-0.00002)*max)/32768)/0.00002));

                        db += 20 * Math.log10(max);

//                        AudioAttributes attr=new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED).setLegacyStreamType(AudioAttributes.USAGE_MEDIA).setUsage(AudioAttributes.USAGE_MEDIA).build();
//                        AudioFormat format=new AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(1).setSampleRate(11025).build();
//                        AudioTrack track=new AudioTrack(attr,format,102400,AudioTrack.MODE_STREAM,1);
//
//                        track.write(backbuffer,0,l);
//                        track.play();


                        Log.d("Main2Activity", "Recording stopped:" + audioRecording.length + " bytes read AVG=" + max + " dB=" + db);
                        final double db2 = db;
                        final short finalMax = max;
                        handler.post(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(), "stop recording" + " AVG=" + finalMax + " dB=" + db2, Toast.LENGTH_LONG).show();
                            }
                        });
//                Toast.makeText(getApplicationContext(), "stop recording", Toast.LENGTH_SHORT).show();
                        System.out.println("Uploading data");
                        final String UID = "Unique_User_id";
                        SharedPreferences prefs = getSharedPreferences(UID, MODE_PRIVATE);
                        String deviceid = prefs.getString("UID", null);
                        if(deviceid==null)
                        {
                            String uuid = UUID.randomUUID().toString().replace("-", "");
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("UID", uuid);
                            editor.apply();
                            deviceid=uuid;
                        }
                        if(Lat!=0&&Long!=0)
                        {
                            ServiceClient.uploadData(deviceid, Long, Lat, db2);
                            System.out.println("Data uploaded");
                        }
                    }
                }).start();

            }
        });

    }

}