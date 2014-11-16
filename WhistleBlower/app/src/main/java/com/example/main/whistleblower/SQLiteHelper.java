package com.example.main.whistleblower;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * An extension of the SQLiteOpenHelper helper class to manage SQLite database
 * creation and data management. Implemented as a Singleton.
 * <p/>
 * Created by daniel on 11/15/14.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int NUM_OF_MESSAGES_RETURNED = 30;
    private static final String TABLE_MESSAGES = "msgs";                // Table
    private static final String COLUMN_CONTENT = "msg_content";         // Message content
    private static final String COLUMN_TS = "msg_ts";                   // Timestamp
    private static final String COLUMN_CAT = "category";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_SUBTYPE = "sub_type";
    private static final String COLUMN_LOCATION = "location";           // GPS coordinate data
    private static final String DATABASE_NAME = "messages.db";
    private static final int DATABASE_VERSION = 3;

    /**
     * Representations of the columns in the SQL database
     */
    private static String[] ALL_COLUMNS = {
            COLUMN_CONTENT, COLUMN_TS, COLUMN_CAT, COLUMN_TYPE, COLUMN_SUBTYPE, COLUMN_LOCATION};

    /**
     * String to create SQL database
     */
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_MESSAGES + "(" +
                    COLUMN_CONTENT + " TEXT NOT NULL, " +
                    COLUMN_TS + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_CAT + " TEXT NOT NULL, " +
                    COLUMN_TYPE + " TEXT NOT NULL, " +
                    COLUMN_SUBTYPE + " TEXT NOT NULL, " +
                    COLUMN_LOCATION + " TEXT NOT NULL" +
                    ");";


    private static SQLiteHelper myInstance;


    /**
     * Returns the sole instance of {@link #SQLiteHelper}. If the instance is
     * null, creates a new instance of {@link #SQLiteHelper}, sets it as
     * the sole instance, and then returns the instance.
     */
    public static SQLiteHelper getInstance() {
        if (myInstance == null) {
            myInstance = new SQLiteHelper(MainActivity.getMyActivity());
        }
        return myInstance;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    /**
     * Inserts new row into database. Can be called directly from an existing
     * instance without additional setup. Database updates automatically.
     *
     * @param msgContent message body
     * @param msgTS      message timestamp
     */
    public synchronized void insertEntry(String msgContent, String msgTS, String cat,
                                         String type, String subtype, String loc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CONTENT, msgContent);
        values.put(SQLiteHelper.COLUMN_TS, Util.convertDataTimeToUserTime(msgTS));
        values.put(SQLiteHelper.COLUMN_CAT, cat);
        values.put(SQLiteHelper.COLUMN_TYPE, type);
        values.put(SQLiteHelper.COLUMN_SUBTYPE, subtype);
        values.put(SQLiteHelper.COLUMN_LOCATION, loc);
        db.insert(SQLiteHelper.TABLE_MESSAGES, null, values);
    }


    /**
     * Gets the most recently received messages (up to 30) and posts them to
     * the Handler of {@link MainActivity} sequentially in order of time
     * received. Runs in background thread.
     */
    public synchronized void getRecentMessages() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.d("Daniel", "Syncing with main activity");
                List<Data> messages = new ArrayList<Data>();
                Cursor cursor = getWritableDatabase().query(
                        SQLiteHelper.TABLE_MESSAGES,
                        SQLiteHelper.ALL_COLUMNS,
                        null, null, null, null,
                        SQLiteHelper.COLUMN_TS + " DESC");
                int numEntries = NUM_OF_MESSAGES_RETURNED;
                cursor.moveToFirst();

                // Adding each message into the list, as a DATA object type
                while (!cursor.isAfterLast() && numEntries != 0) {
                    String msg = cursor.getString(0);
                    String ts = cursor.getString(1);
                    String cat = cursor.getString(2);
                    String type = cursor.getString(3);
                    String subtype = cursor.getString(4);
                    String loc = cursor.getString(5);
                    Data message = new Data(msg, ts, cat, type, subtype, loc);
                    messages.add(message);
                    cursor.moveToNext();
                    numEntries--;
                }

                // Sending each message to the main thread
                for (Data d : messages) {
                    MainActivity main = (MainActivity) MainActivity.getMyActivity();
                    Handler handler = main.getHandler();
                    Message m = Message.obtain(handler, 0, d);
                    m.sendToTarget();
                }

                // Sending each message to the main thread
                for (Data d : messages) {
                    FeedsActivity main = (FeedsActivity) FeedsActivity.getMyActivity();
                    if (main != null) {
                        Handler handler = main.getHandler();
                        Message m = Message.obtain(handler, 0, d);
                        m.sendToTarget();
                    }
                }
                return null;
            }
        }.execute(null, null, null);
    }
}