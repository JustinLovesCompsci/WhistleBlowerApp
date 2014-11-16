package com.example.main.whistleblower;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ListView;

import com.example.main.whistleblower.models.ListAdapter;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private static MainActivity myActivity;


    private List<Data> dataList;
    private ListAdapter mAdapter;

    LocationManager mLocationManager;
    GoogleMap googleMap;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting location manager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Starting map services
        setContentView(R.layout.activity_main);
        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        // Starting array adapter for message display
        dataList = new ArrayList<Data>();
        mAdapter = new ListAdapter(this, dataList);
        mListView = (ListView) findViewById(R.id.msg_list);
        mListView.setAdapter(mAdapter);

        // For static access to this activity by thread handler
        myActivity = this;
    }

    private void placePointsOnMap(List<Location> listOfLocations){
        // A reference of a GoogleMap object exists as instance variable
        for(Location l : listOfLocations){
            String myTitle = "";                // Empty string for title
            googleMap.addMarker(new MarkerOptions()
                     .position(new LatLng(l.getLatitude(), l.getLongitude()))
                     .title(myTitle));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.post_message:
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static Activity getMyActivity(){
        return myActivity;
    }

    /**
     * Handler used for updating the ListView
     * @return
     */
    public Handler getHandler(){
        return new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message m) {
                Data d = (Data) m.obj;

                // Eliminating the oldest element in the list if full
                if (dataList.size() >= 30) {
                    dataList.remove(29);
                }
                dataList.add(d);
                Collections.sort(dataList);
                mAdapter.notifyDataSetChanged();
            }
        };
    }

}
