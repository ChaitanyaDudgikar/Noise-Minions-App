
package com.example.beproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MyService extends Service
{

   // public static final int notify = 300000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling

    AudioRecord audioRecord;
    Boolean isRecorderStart = null;
    ShortBuffer shortBuffer;
    public double Lat, Long;
    int callibval=0;
    float BASE;
    Handler handler = new Handler();
    private MyLocationListener ml;

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


    class StartRecordingThread extends Thread
    {
//        Button btn2, btn3;
        // Make handler and apply to start and stop

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
    }

    class StopRecordingThread extends Thread
    {
        @Override
        public void run()
        {
            Log.d("Log", "Stopping recording");
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


            Log.d("Log", "Recording stopped:" + audioRecording.length + " bytes read AVG=" + max + " dB=" + db);
            final double db2 = db;
            final short finalMax = max;

//                Toast.makeText(getApplicationContext(), "stop recording", Toast.LENGTH_SHORT).show();
//            System.out.println("Uploading data");
            final String UID = "Unique_User_id";
            SharedPreferences prefs = getSharedPreferences(UID, MODE_PRIVATE);
            String deviceid = prefs.getString("UID", null);
            float basedb = prefs.getFloat("basedb", 0);
            SharedPreferences.Editor editor = prefs.edit();
            Log.d("Log", "basedb" + basedb);
            if (deviceid == null)
            {
                String uuid = UUID.randomUUID().toString().replace("-", "");
                editor = prefs.edit();
                editor.putString("UID", uuid);
                editor.apply();
                deviceid = uuid;
            }
            Log.d("Log", "Uploading data " + deviceid + ", " + Long + ", " + Lat + ", " + db2 + ":" + (db2 -basedb));
            BASE=basedb;
            handler.post(new Runnable()
            {

                @Override
                public void run()
                {
                    Toast.makeText(getApplicationContext(), "stop recording" + " AVG=" + finalMax + " dB=" + (db2-BASE), Toast.LENGTH_LONG).show();
                }
            });
            if (((NoiseMinionApplication) getApplication()).isCalibratinglow)
            {

                callibval=35;
                editor.putFloat("basedb", (float) db2-callibval);
                editor.apply();
                Log.d("Log", "basedb" + (db2-callibval));
                //Log.d("Log", "Uploading data " + deviceid + ", " + Long + ", " + Lat + ", " + db2 + ":" + (db2 -basedb));
                ((NoiseMinionApplication) getApplication()).isCalibratinglow = false;
            }
            if (((NoiseMinionApplication) getApplication()).isCalibratingmedium)
            {

                callibval=55;
                editor.putFloat("basedb", (float) db2-callibval);
                editor.apply();
                Log.d("Log", "basedb" + (db2-callibval));
                //Log.d("Log", "Uploading data " + deviceid + ", " + Long + ", " + Lat + ", " + db2 + ":" + (db2 - basedb));
                ((NoiseMinionApplication) getApplication()).isCalibratingmedium = false;
            }
            if (((NoiseMinionApplication) getApplication()).isCalibratinghigh)
            {

                callibval=70;
                editor.putFloat("basedb", (float) db2-callibval);
                editor.apply();
                Log.d("Log", "basedb" + (db2-callibval));
                //Log.d("Log", "Uploading data " + deviceid + ", " + Long + ", " + Lat + ", " + db2 + ":" + (db2 - basedb));
                ((NoiseMinionApplication) getApplication()).isCalibratinghigh = false;
            }
            else if (Lat != 0 && Long != 0)
            {
                ServiceClient.uploadData(deviceid, Long, Lat, db2 - basedb);
                System.out.println("Data uploaded");
            }
        }


    }


    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        boolean isCalibratinglow = ((NoiseMinionApplication) getApplication()).isCalibratinglow;
        boolean isCalibratingmedium = ((NoiseMinionApplication) getApplication()).isCalibratingmedium;
        boolean isCalibratinghigh = ((NoiseMinionApplication) getApplication()).isCalibratinghigh;

        Log.d("Log", "MyService.onCreate()");

        if (!isCalibratinglow || !isCalibratingmedium || !isCalibratinghigh )
        {
            if (mTimer != null) // Cancel if already existed
                mTimer.cancel();
            else
                mTimer = new Timer();   //recreate new

            mTimer.scheduleAtFixedRate(new TimeDisplay(), 0_000, 15_000);   //Schedule task

            ml = new MyLocationListener();
            final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

            try
            {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, ml);
            } catch (SecurityException se)
            {
                System.err.println("Location access not permitted");
                se.printStackTrace();
            }
        } else
        {
            new Thread(new TimeDisplay()).start();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "noiseminionchannel";
            String description = "noiseminionchannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Log.d("Log", "Starting as Foreground");
        Notification notification = new NotificationCompat.Builder(getApplicationContext()).setContentTitle("Noise Minions").setContentText("Service is running").setOngoing(true).setChannelId("1").setPriority(NotificationCompat.PRIORITY_MAX).setVisibility(NotificationCompat.VISIBILITY_PUBLIC).build();
        Log.d("Log", notification.toString());
        startForeground(0x0001, notification);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.d("Log", "Stopping service");

        if (mTimer != null)
        {
            mTimer.cancel();    //For Cancel Timer
        }
        if (ml != null)
        {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.removeUpdates(ml);
        }

        Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show();
        stopForeground(true);
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask
    {
        @Override
        public void run()
        {
            // run on another thread
            try
            {
                Log.d("Log", "new StartRecordingThread().start()");
                new StartRecordingThread().start();
                Thread.sleep(5_000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } finally
            {
                Log.d("Log", "new StopRecordingThread().start();");
                new StopRecordingThread().start();
            }


            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    // display toast
                    Toast.makeText(MyService.this, "Service is running", Toast.LENGTH_SHORT).show();
                    if (((NoiseMinionApplication) getApplication()).isCalibratinglow)
                    {
                        stopSelf();
                    }
                    if (((NoiseMinionApplication) getApplication()).isCalibratingmedium)
                    {
                        stopSelf();
                    }
                    if (((NoiseMinionApplication) getApplication()).isCalibratinghigh)
                    {
                        stopSelf();
                    }
                }
            });
        }
    }
}