package com.android.example.wordlistsql.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.example.wordlistsql.WordItem;

public class WordListOpenHelper extends SQLiteOpenHelper {
    // It's a good idea to always define a log tag like this.
    private static final String TAG = WordListOpenHelper.class.getSimpleName();
    // has to be 1 first time or app will crash
    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    private static final int DATABASE_VERSION = 1;
    private static final String WORD_LIST_TABLE = "word_entries";
    private static final String DATABASE_NAME = "wordlist";
    // Column names...
    public static final String KEY_ID = "_id";

    public static final String KEY_WORD = "word";
    private static final String WORD_LIST_TABLE_CREATE =
            "CREATE TABLE " + WORD_LIST_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
// id will auto-increment if no value passed
                    KEY_WORD + " TEXT );";

    // ... and a string array of columns.
    private static final String[] COLUMNS = { KEY_ID, KEY_WORD };
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_LIST_TABLE_CREATE);
        fillDatabaseWithData(db);
    }

    private void fillDatabaseWithData(SQLiteDatabase db){
        String[] words = {"Android", "Adapter", "ListView", "AsyncTask",
                "Android Studio", "SQLiteDatabase", "SQLOpenHelper",
                "Data model", "ViewHolder","Android Performance",
                "OnClickListener"};
        // Create a container for the data.
        ContentValues values = new ContentValues();
        for (int i=0; i < words.length; i++) {
// Put column/value pairs into the container.
// put() overrides existing values.
            values.put(KEY_WORD, words[i]);
            db.insert(WORD_LIST_TABLE, null, values);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WordListOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
        onCreate(db);
    }
    public int update(int id, String word){
        int mNumberOfRowsUpdated = -1;
        if (mWritableDB == null) {
            mWritableDB = getWritableDatabase();
        }
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_WORD, word);
            mNumberOfRowsUpdated = mReadableDB.update(WORD_LIST_TABLE,
                    values, // new values to insert
// selection criteria for row (the _id column)
                    KEY_ID + " = ?",
//selection args; value of id
                    new String[]{String.valueOf(id)});
        }catch (Exception e){
            e.printStackTrace();
        }

        return mNumberOfRowsUpdated;
    }

    public int delete(int id) {
        int deleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
                deleted = mWritableDB.delete(WORD_LIST_TABLE,
                        KEY_ID + " = ? ", new String[]{String.valueOf(id)});
            }
        } catch (Exception e) {
            Log.d (TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }
    public long insert(String word){
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word);
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(WORD_LIST_TABLE, null, values);

        }
        catch (Exception e)
        {
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        return newId;
    }

    public WordListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    public long count(){
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, WORD_LIST_TABLE);
    }
    @SuppressLint("Range")
    public WordItem query(int position) {
            String query = "SELECT * FROM " + WORD_LIST_TABLE +
                    " ORDER BY " + KEY_WORD + " ASC " +
                    "LIMIT " + position + ",1";
            Cursor cursor = null;
            WordItem entry = new WordItem();
            try {
                if (mReadableDB == null) {
                    mReadableDB = getReadableDatabase();
                }
                cursor = mReadableDB.rawQuery(query, null);
                cursor.moveToFirst();
                entry.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                entry.setWord(cursor.getString(cursor.getColumnIndex(KEY_WORD)));

            } catch (Exception e) {
                Log.d(TAG, "EXCEPTION! " + e);
            }
        cursor.close();
        return entry;

    }


}






