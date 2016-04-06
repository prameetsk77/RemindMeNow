package edu.asu.remindmenow.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jithin Roy on 4/5/16.
 */
public class DBConnection {

    private static DBConnection mInstance;
    private Context mContext;
    private DBHelper mDbHelper;

    public static DBConnection getInstance() {
        if (mInstance == null) {
            mInstance = new DBConnection();
        }
        return mInstance;
    }

    public void setContext(Context ctx) {
        mContext = ctx;
        mDbHelper = new DBHelper(ctx);
    }


    public SQLiteDatabase openWritableDB() {
        return mDbHelper.getWritableDatabase();
    }

    public void closeDB(SQLiteDatabase db) {
        db.close();
    }
}
