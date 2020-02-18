package com.example.beproject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private List<WeightedLatLng> heatmapPoints = new ArrayList<>();
    private TileOverlay mOverlay;
    private Handler handler;
    private int reqcount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        handler = new Handler();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        mMap = googleMap;
        float zoomLevel = (float) 12.5;
        // Add a marker in Sydney and move the camera
        LatLng solapur = new LatLng(17.677353, 75.908675);
        mMap.addMarker(new MarkerOptions().position(solapur).title("Marker in Solapur"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(solapur, zoomLevel));

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener()
        {
            @Override
            public void onCameraMove()
            {
                fetchData(googleMap);
            }
        });

        fetchData(googleMap);
    }

    private void fetchData(final GoogleMap googleMap)
    {
        Log.d("json", "onCameraMove()");
        final LatLng sw = googleMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        final LatLng ne = googleMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        final int reqno=++reqcount;

        new Thread()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if(reqno!=reqcount) return;

                final List<WeightedLatLng> noiseList = new ArrayList<>();
                final String BASE_URL = "http://13.58.40.245:8084";
                try
                {
                    URL u = new URL(BASE_URL + "/query.htm?long_from=" + sw.longitude + "&long_to=" + ne.longitude + "&lat_from=" + sw.latitude + "&lat_to=" + ne.latitude);
                    HttpURLConnection con = (HttpURLConnection) u.openConnection();

                    con.setRequestMethod("GET");

                    InputStream inputStream = con.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] b = new byte[10240];
                    int n;
                    while ((n = inputStream.read(b)) != -1)
                    {
                        baos.write(b, 0, n);
                    }
                    b = baos.toByteArray();
                    baos.close();
                    inputStream.close();

                    String json = new String(b, "UTF-8");
                    Log.d("json", json);
                    JSONArray array = new JSONArray(json);
                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject object = array.getJSONObject(i);
                        double lat = object.getDouble("latitudeNoise");
                        double lng = object.getDouble("longitudeNoise");
                        double weight = object.getDouble("noiselevel");
                        //weight = Math.pow(10, weight / 20.0);
                        noiseList.add(new WeightedLatLng(new LatLng(lat, lng), weight));
                    }
                } catch (Exception e)
                {
//                    System.out.println(e);
                    e.printStackTrace();
                }

                handler.post(new Runnable()
                {
                    public void run()
                    {
                        if (mProvider == null)
                        {
                            if (noiseList != null && !noiseList.isEmpty())
                            {
                                mProvider = new HeatmapTileProvider.Builder().weightedData(noiseList).build();
                                mProvider.setRadius((int)Math.pow(1.25,googleMap.getCameraPosition().zoom));
                                mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider).transparency(0.3f));
                            }
                            // Render links
//            attribution.setMovementMethod(LinkMovementMethod.getInstance());
                        } else
                        {
                            if (noiseList != null && !noiseList.isEmpty())
                            {
                                mProvider.setRadius((int)Math.pow(1.25,googleMap.getCameraPosition().zoom));
                                mProvider.setWeightedData(noiseList);
                                mOverlay.clearTileCache();
                            }
                        }
                    }
                });
            }
        }.start();

    }
}