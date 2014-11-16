package com.example.main.whistleblower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Collections;
import java.util.List;


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

    @Override
    public void onClick(View view) {

    }

    public static Activity getMyActivity(){
        return myActivity;
    }

    /**
     * Handler used for updating the ListView
     * @return
     */
    public android.os.Handler getHandler(){
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

}
