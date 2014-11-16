package com.example.main.whistleblower;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private static MainActivity myActivity;

    LocationManager mLocationManager;
    GoogleMap googleMap;
    private List<Data> dataList;

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SQLiteHelper.getInstance().getRecentMessages();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting location manager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Starting map services
        setContentView(R.layout.activity_main);
        SupportMapFragment myFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        try {
            googleMap = myFrag.getMap();
        } catch (Exception ex) {
            Toast.makeText(this, "Unable to load map", Toast.LENGTH_SHORT).show();
        }

        // For static access to this activity by thread handler
        myActivity = this;
        initFetchDataTask();
        dataList = new ArrayList<Data>();
        Button feedsButton = (Button) findViewById(R.id.button_see_feeds);
        feedsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FeedsActivity.class));
            }
        });

        Button analysisButton = (Button) findViewById(R.id.button_see_analysis);
        analysisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AnalysisActivity.class));
            }
        });

        Button sendButton = (Button) findViewById(R.id.button_send_post);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });
        // Getting cached messages
        SQLiteHelper.getInstance().getRecentMessages();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
//        initFetchDataTask();
        super.onResume();
    }

    private void initFetchDataTask() {
        Intent fetchIntent = new Intent(getMyActivity(), BackendService.class);
        getMyActivity().startService(fetchIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Activity getMyActivity() {
        return myActivity;
    }

    /**
     * Handler used for updating the ListView
     *
     * @return
     */
    public Handler getHandler() {
        return new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message m) {
                Data d = (Data) m.obj;

                // Eliminating the oldest element in the list if full
                if (dataList.size() >= 30) {
                    dataList.remove(29);
                }
                dataList.add(d);
                Collections.sort(dataList);
            }
        };
    }

    private void placePointsOnMap(List<Location> listOfLocations) {
        // A reference of a GoogleMap object exists as instance variable
        for (Location l : listOfLocations) {
            String myTitle = "";                // Empty string for title
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(l.getLatitude(), l.getLongitude()))
                    .title(myTitle));
        }
    }

    private void populateMap() {
        if (dataList.isEmpty() || googleMap == null)
            return;
        String location = null;
        for (Data d : dataList) {
            location = d.getLocation();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Util.convertToLatitude(location),
                            Util.convertToLongtitude(location))));
        }
    }
}
