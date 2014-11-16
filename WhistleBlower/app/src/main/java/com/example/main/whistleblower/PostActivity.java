package com.example.main.whistleblower;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class PostActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setUpTypeDropDown(R.id.harassment_type_dropDown, R.array.harassment_type_array, true);
        setUpTypeDropDown(R.id.harassment_subType_dropDown, R.array.sexism_subType_array, false);//default subtypes

        CheckBox myLocation = (CheckBox) findViewById(R.id.checkBox_current_location);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(((CheckBox) view).isChecked())) {
                    showLocationDialog();
                }
            }
        });


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
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            if (item.equals("Racism")) {
                setUpTypeDropDown(R.id.harassment_subType_dropDown, R.array.racism_subType_array, false);//default subtypes
            } else { // selected sexism
                setUpTypeDropDown(R.id.harassment_subType_dropDown, R.array.sexism_subType_array, false);//default subtypes
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    public class SubTypeSelectionListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            String item = parent.getItemAtPosition(pos).toString();
//            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    private class SubmissionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            person = new Data();
            person.setName(Data.getText().toString());
            person.setCountry(etCountry.getText().toString());
            person.setTwitter(etTwitter.getText().toString());
            return post(urls[0], person);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Message Sent", Toast.LENGTH_LONG).show();
        }
    }
}