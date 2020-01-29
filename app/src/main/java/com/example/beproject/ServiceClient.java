package com.example.beproject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServiceClient
{
    private static final String BASE_URL="http://13.58.40.245:8084";
    public static String uploadData(String deviceid, double longitude, double latitude, double noiselevel)
    {
        try
        {
            URL u=new URL(BASE_URL+"/upload.htm");
            HttpURLConnection con= (HttpURLConnection) u.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            String data="deviceid="+deviceid+"&longitude="+longitude+"&latitude="+latitude+"&noiselevel="+noiselevel;

            con.setDoOutput(true);
            try(PrintWriter pw=new PrintWriter(con.getOutputStream()))
            {
                pw.println(data);
                pw.flush();
            }

            int sc=con.getResponseCode();
            if(sc/100==2)
            {
                InputStream is=con.getInputStream();
                byte []buffer=new byte[10240];
                int n;
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                while((n=is.read(buffer))!=-1)
                {
                    baos.write(buffer,0,n);
                }
                is.close();
                buffer=baos.toByteArray();

                return new String(buffer,"UTF-8");

            }
        } catch (java.io.IOException e)
        {
            e.printStackTrace();
        }
        return "{}";
    }
}
