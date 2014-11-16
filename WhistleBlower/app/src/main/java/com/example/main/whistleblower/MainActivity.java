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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;


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
        SupportMapFragment myFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        try {
            googleMap = myFrag.getMap();
        } catch(Exception ex){
            Toast.makeText(this,"error rendering map",Toast.LENGTH_SHORT).show();
        }

        // Starting array adapter for message display
        dataList = new ArrayList<Data>();
        mAdapter = new ListAdapter(this, dataList);
        mListView = (ListView) findViewById(R.id.msg_list);
        mListView.setAdapter(mAdapter);

        // For static access to this activity by thread handler
        myActivity = this;

//        getFragmentManager().beginTransaction()
//                .add(R.id.container, new MapFragment()).commit();
        initFetchDataTask();

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
        new FetchDataTask().execute(Constants.DATABASE_URL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.post_message:
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                return true;
            case R.id.item_data_analysis:
                startActivity(new Intent(MainActivity.this, AnalysisActivity.class));
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

    private void placePointsOnMap(List<Location> listOfLocations){
        // A reference of a GoogleMap object exists as instance variable
        for(Location l : listOfLocations){
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

    public class FetchDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL obj = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    Log.w("MainActivity", "Unable to fetch data");
                    return null;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.w("FetchDataTask", response.toString());

                if (!response.toString().equals("null")) {
                    JsonElement jsonElement = new JsonParser().parse(response.toString());
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        String type = entry.getValue().getAsJsonObject().get(Constants.TYPE).toString().replace("\"", "");
                        Log.w("FetchDataTask type:", type);
                        String sub_type = entry.getValue().getAsJsonObject().get(Constants.SUB_TYPE).toString().replace("\"", "");
                        Log.w("FetchDataTask sub_type:", sub_type);
                        String message = entry.getValue().getAsJsonObject().get(Constants.MESSAGE).toString().replace("\"", "");
                        Log.w("FetchDataTask message:", message);
                        String category = entry.getValue().getAsJsonObject().get(Constants.CATEGORY).toString().replace("\"", "");
                        Log.w("FetchDataTask category:", category);
                        String timestamp = entry.getValue().getAsJsonObject().get(Constants.TIME_STAMP).toString().replace("\"", "");
                        Log.w("FetchDataTask timestamp:", timestamp);
                        String location = entry.getValue().getAsJsonObject().get(Constants.LOCATION).toString().replace("\"", "");
                        Log.w("FetchDataTask location:", location);
                        SQLiteHelper.getInstance().insertEntry(message, timestamp, category, type, sub_type, location);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Success";
        }
    }

}
