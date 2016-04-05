package edu.asu.remindmenow.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import edu.asu.remindmenow.models.User;

/**
 * Created by priyama on 4/4/2016.
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



    public boolean insertContact  (User user)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.RM_USER_ID, user.getId());
        contentValues.put(DBHelper.RM_USER_NAME, user.getName());
        db.insert(DBHelper.RM_USER_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }
    //
    public User getData(String id){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from rm_user where " +DBHelper.RM_USER_ID +" = "+id+"", null );
        if (res.moveToFirst()) {
            User user = new User();
            user.setId(res.getString(res.getColumnIndex(DBHelper.RM_USER_ID)));
        }
        return null;
    }


}

