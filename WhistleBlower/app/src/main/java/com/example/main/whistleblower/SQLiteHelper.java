package com.example.main.whistleblower;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;

import com.example.main.whistleblower.MainActivity;


/**
 * An extension of the SQLiteOpenHelper helper class to manage SQLite database
 * creation and data management. Implemented as a Singleton.
 *
 * Created by daniel on 11/15/14.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int NUM_OF_MESSAGES_RETURNED = 30;
    private static final String TABLE_MESSAGES = "msgs";                // Table
    private static final String COLUMN_ID = "msg_id";                   // ID
    private static final String COLUMN_CONTENT = "msg_content";         // Message content
    private static final String COLUMN_TS = "msg_ts";                   // Timestamp
    private static final String COLUMN_CAT = "category";
    private static final String COLUMN_SUBTYPE = "sub_type";
    private static final String COLUMN_TYPE =  "type";
    private static final String COLUMN_LOCATION = "location";           // GPS coordinate data
    private static final String DATABASE_NAME = "messages.db";
    private static final int DATABASE_VERSION = 1;
    private static String[] ALL_COLUMNS = { COLUMN_TS, COLUMN_ID, COLUMN_CONTENT };
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MESSAGES + "(" + COLUMN_TS + " text primary key, "
            + COLUMN_ID + " text not null," + COLUMN_CONTENT
            + " text not null);";
    private static SQLiteHelper myInstance;


    /**
     * Returns the sole instance of {@link #SQLiteHelper}. If the instance is
     * null, creates a new instance of {@link #SQLiteHelper}, sets it as
     * the sole instance, and then returns the instance.
     */
    public static SQLiteHelper getInstance(Context context) {
        if (myInstance == null) {
            myInstance = new SQLiteHelper(context);
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
     * @param msgID message id
     * @param msgTS message timestamp
     */
    public synchronized void insertEntry(String msgContent, String msgID, String msgTS, String cat,
                                         String type, String subtype, String loc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CONTENT, msgContent);
        values.put(SQLiteHelper.COLUMN_ID, msgID);
        values.put(SQLiteHelper.COLUMN_TS, msgTS);
        values.put(SQLiteHelper.COLUMN_CAT, cat);
        values.put(SQLiteHelper.COLUMN_TYPE, type);
        values.put(SQLiteHelper.COLUMN_SUBTYPE, subtype);
        values.put(SQLiteHelper.COLUMN_LOCATION, loc);
        db.insert(SQLiteHelper.TABLE_MESSAGES, null, values);
    }


    /**
     * Gets the most recently received messages (up to 30) and posts them to
     * the Handler of {@link #MainActivity} sequentially in order of time
     * received. Runs in background thread.
     */
    public synchronized void getRecentMessages() {
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                List<Data> messages = new ArrayList<Data>();
                Cursor cursor = getWritableDatabase().query(
                        SQLiteHelper.TABLE_MESSAGES,
                        SQLiteHelper.ALL_COLUMNS, null, null, null, null,
                        SQLiteHelper.COLUMN_TS + " DESC");
                int numEntries = NUM_OF_MESSAGES_RETURNED;
                cursor.moveToFirst();

                // Adding each message into the list, as a DATA object type
                while (!cursor.isAfterLast() && numEntries != 0) {
                    long ts = cursor.getLong(0);
                    String id = cursor.getString(1);
                    String content = cursor.getString(2);
                    Data message = new Data();
                    messages.add(message);
                    cursor.moveToNext();
                    numEntries--;
                }
                for (Data i : messages) {
                    MainActivity main = (MainActivity) MainActivity.getMyActivity();
                    Handler handler = main.getHandler();
                    Message m = Message.obtain(handler, 0, i);
                    m.sendToTarget();
                }
                return null;
            }
        }.execute(null, null, null);
    }
}