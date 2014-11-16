package com.example.main.whistleblower;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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


public class MainActivity extends Activity implements View.OnClickListener {

    private static MainActivity myActivity;
    private static List<Data> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myActivity = this;
//        getFragmentManager().beginTransaction()
//                .add(R.id.container, new MapFragment()).commit();
        initFetchDataTask();
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

    @Override
    public void onClick(View view) {

    }

    public static Activity getMyActivity() {
        return myActivity;
    }

    /**
     * Handler used for updating the ListView
     *
     * @return
     */
    public android.os.Handler getHandler() {
        return new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message m) {
                Data d = (Data) m.obj;
                if (dataList.size() >= 20) {
                    dataList.remove(19);
                }
                dataList.add(d);
                Collections.sort(dataList);
//                listAdapter.notifyDataSetChanged();
            }
        };
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
                Log.w("MainActivity", response.toString());

                JsonElement jsonElement = new JsonParser().parse(response.toString());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String type = entry.getValue().getAsJsonObject().get(Constants.TYPE).toString().replace("\"", "");
                    String sub_type = entry.getValue().getAsJsonObject().get(Constants.SUB_TYPE).toString().replace("\"", "");
                    String message = entry.getValue().getAsJsonObject().get(Constants.MESSAGE).toString().replace("\"", "");
                    String category = entry.getValue().getAsJsonObject().get(Constants.CATEGORY).toString().replace("\"", "");
                    String timestamp = entry.getValue().getAsJsonObject().get(Constants.TIME_STAMP).toString().replace("\"", "");
                    String location = entry.getValue().getAsJsonObject().get(Constants.LOCATION).toString().replace("\"", "");
                    SQLiteHelper.getInstance().insertEntry(message, null, timestamp, category, type, sub_type, location);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Success";
        }
    }

}
