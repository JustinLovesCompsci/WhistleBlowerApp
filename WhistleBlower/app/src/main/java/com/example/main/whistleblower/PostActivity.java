package com.example.main.whistleblower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class PostActivity extends Activity {

    private Data myData;
    private double myLongitude;
    private double myLatitude;
    private Location mCurrentLocation;
    private LocationManager locationManager;


    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(ACTIVITY_SERVICE, "Received broadcast from LocationUpdater");
            // Decoding the broadcast
            Bundle extras = intent.getExtras();
            Location NEW_LOCATION = (Location) extras.get("NEW_LOCATION");
            // Updating current location
            mCurrentLocation = NEW_LOCATION;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setUpTypeDropDown(R.id.harassment_type_dropDown, R.array.harassment_type_array, true);
        setUpTypeDropDown(R.id.harassment_subType_dropDown, R.array.sexism_subType_array, false);//default subtypes
        myData = new Data();
        LocationListener mLocationListener = new LocationUpdater();

        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationReceiver, new IntentFilter("LOCATION_UPDATE"));
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Toast.makeText(getApplicationContext(), "location manager is null!", Toast.LENGTH_LONG).show();
            return;
        }

        Looper looper = null;
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, looper);

        CheckBox myLocation = (CheckBox) findViewById(R.id.checkBox_current_location);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(((CheckBox) view).isChecked())) {
                    showLocationDialog();
                }
            }
        });

        Button sendButton = (Button) findViewById(R.id.button_post);
        sendButton.setTextColor(Color.WHITE);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Daniel", "sendButton");
                StringBuilder category = new StringBuilder();
                boolean physicalChecked = ((CheckBox) findViewById(R.id.checkBox_category_physical)).isChecked();
                boolean verbalChecked = ((CheckBox) findViewById(R.id.checkBox_category_verbal)).isChecked();
                if (physicalChecked && verbalChecked) {
                    category.append(Constants.PHYSICAL);
                    category.append(Util.SEPARATOR);
                    category.append(Constants.VERBAL);
                } else if (physicalChecked) {
                    category.append(Constants.PHYSICAL);
                } else if (verbalChecked) {
                    category.append(Constants.VERBAL);
                }
                myData.setCategory(category.toString());
                myData.setMessage(((EditText) findViewById(R.id.message_box)).getText().toString());

                Location location = getLocation();

                if (location == null) {
                    Toast.makeText(getApplicationContext(), "location is null!", Toast.LENGTH_LONG).show();
                    return;
                }

                myData.setLocation(Util.appendCoordinates(location.getLongitude(), location.getLongitude()));

                if (!validate()) {
                    showNonFilledDialog();
                    myData.clear();
                    return;
                }
                Toast.makeText(getApplicationContext(), "Report posted", Toast.LENGTH_SHORT).show();
                new SubmissionTask().execute(Constants.DATABASE_URL);
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

    public Location getLocation() {
        if (mCurrentLocation != null)
            return mCurrentLocation;

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void constructData() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Util.TIME_FORMAT);
        String dateTime = dateFormat.format(c.getTime());
        myData.setTimeStamp(dateTime);
    }

    private void showNonFilledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Please Fill All Required Fields");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * Message box is not required
     *
     * @return
     */
    private boolean validate() {
        if (myData.getType() == null || myData.getType().trim().equals("")) {
            return false;
        } else if (myData.getSub_Type() == null || myData.getSub_Type().trim().equals("")) {
            return false;
        } else if (myData.getCategory() == null || myData.getCategory().trim().equals("")) {
            return false;
        } else if (myData.getLocation() == null || myData.getLocation().trim().equals("")) {
            return false;
        } else if (myData.getMessage() == null || myData.getMessage().trim().equals("")) {
            return false;
        }
        return true;
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Location");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        builder.setView(input);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: set typed location text and replace checkbox
            }
        });
        builder.show();
    }

    private void setUpTypeDropDown(int viewId, int arrayId, boolean isMainType) {
        Spinner spinner = (Spinner) findViewById(viewId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (isMainType) {
            spinner.setOnItemSelectedListener(new MainTypeSelectionListener());
        } else {
            spinner.setOnItemSelectedListener(new SubTypeSelectionListener());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MainTypeSelectionListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String item = parent.getItemAtPosition(pos).toString();
            myData.setType(item);
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            if (item.equals("Racism")) {
                setUpTypeDropDown(R.id.harassment_subType_dropDown, R.array.racism_subType_array, false);//default subtypes
            } else { // selected sexism
                setUpTypeDropDown(R.id.harassment_subType_dropDown, R.array.sexism_subType_array, false);//default subtypes
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class SubTypeSelectionListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String item = parent.getItemAtPosition(pos).toString();
            myData.setSub_Type(item);
//            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private String post(String url, JSONObject jsonObj) {
        try {
            URL obj = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(jsonObj.toString());
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                Log.w("PostActivity", conn.toString());
                Log.w("PostActivity", "Some Error Happened Getting the Data! " + responseCode);
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Success";
    }

    private class SubmissionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            constructData();
            JSONObject json = new JSONObject();
            try {
                json.accumulate(Constants.TYPE, myData.getType());
                json.accumulate(Constants.SUB_TYPE, myData.getSub_Type());
                json.accumulate(Constants.MESSAGE, myData.getMessage());
                json.accumulate(Constants.CATEGORY, myData.getCategory());
                json.accumulate(Constants.LOCATION, myData.getLocation());
                json.accumulate(Constants.TIME_STAMP, myData.getTimeStamp());
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            return post(urls[0], json);
        }
    }
}

