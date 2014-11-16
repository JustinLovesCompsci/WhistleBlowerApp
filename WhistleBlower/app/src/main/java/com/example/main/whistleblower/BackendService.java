package com.example.main.whistleblower;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class BackendService extends IntentService {
    private static final String ACTION_FETCH_DATA = "com.example.main.whistleblower.action.FETCH";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackendService.class);
        intent.setAction(ACTION_FETCH_DATA);
        context.startService(intent);
    }

    public BackendService() {
        super("BackendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_DATA.equals(action)) {
                handleActionFetch();
            }
        }
    }

    private void handleActionFetch() {
        try {
            URL obj = new URL(Constants.DATABASE_URL);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                Log.w("MainActivity", "Unable to fetch data");
                return;
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
            SQLiteHelper.getInstance().getRecentMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
