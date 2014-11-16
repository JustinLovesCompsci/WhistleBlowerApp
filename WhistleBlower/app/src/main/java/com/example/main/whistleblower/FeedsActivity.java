package com.example.main.whistleblower;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.main.whistleblower.models.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedsActivity extends Activity {

    private ListView mListView;
    private ListAdapter mAdapter;
    private List<Data> dataList;
    private static FeedsActivity myActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = new ArrayList<Data>();
        myActivity = this;
        setContentView(R.layout.activity_feeds);
        mAdapter = new ListAdapter(this, dataList);
        mListView = (ListView) findViewById(R.id.msg_list);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds, menu);
        return true;
    }

    public static Activity getMyActivity() {
        return myActivity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
