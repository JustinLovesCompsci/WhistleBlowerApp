package com.example.main.whistleblower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends Activity {

    private Data myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setUpTypeDropDown(R.id.harassment_type_dropDown, R.array.harassment_type_array, true);
        setUpTypeDropDown(R.id.harassment_subType_dropDown, R.array.sexism_subType_array, false);//default subtypes
        myData = new Data();

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
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                constructData();
                if (!validate()) {
                    showNonFilledDialog();
                }
                new SubmissionTask().execute("");//TODO:url
            }
        });
    }

    private void constructData() {
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
//            data.setLocation();//TODO: set location
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
        myData.setTimeStamp(dateFormat.format(c.getTime()));
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

    private boolean validate() {
        if (myData.getType() == null || myData.getType().trim().equals("")) {
            return false;
        } else if (myData.getSub_Type() == null || myData.getSub_Type().trim().equals("")) {
            return false;
        } else if (myData.getTimeStamp() == null || myData.getTimeStamp().trim().equals("")) {
            return false;
        } else if (myData.getCategory() == null || myData.getCategory().trim().equals("")) {
            return false;
        } else if (myData.getLocation() == null || myData.getLocation().trim().equals("")) {
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

    public String post(String url, JSONObject json) {
        return null;//TODO
    }

    private class SubmissionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            JSONObject json = new JSONObject();
            try {
                json.accumulate(Constants.TYPE, myData.getType());
                json.accumulate(Constants.SUB_TYPE, myData.getSub_Type());
                json.accumulate(Constants.MESSAGE, myData.getMessage());
                json.accumulate(Constants.CATEGORY, myData.getCategory());
                json.accumulate(Constants.LOCATION, myData.getLocation());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return post(urls[0], json);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.w("PostActivity", "Message Sent");
        }
    }
}