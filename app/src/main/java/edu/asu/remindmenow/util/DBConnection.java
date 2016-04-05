package edu.asu.remindmenow.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import edu.asu.remindmenow.exception.ApplicationRuntimeException;
import edu.asu.remindmenow.models.Message;
import edu.asu.remindmenow.models.User;
import edu.asu.remindmenow.models.UserReminder;
import edu.asu.remindmenow.userManager.UserSession;

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

    //==============================================================================================
    // Reminder User
    //==============================================================================================

    public boolean insertUserReminder(UserReminder reminder) {
        SQLiteDatabase db = null;
        try {
            db = mDbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            //contentValues.put(DBHelper.RM_REMINDER_ID, null);
            //contentValues.put(DBHelper.RM_REMINDER_TIME_ID
            contentValues.put(DBHelper.RM_REMINDER_TYPE,"U");
            contentValues.put(DBHelper.RM_REMINDER_TITLE,reminder.getReminderTitle());
            contentValues.put(DBHelper.RM_REMINDER_CREATED_DATE,System.currentTimeMillis());
            contentValues.put(DBHelper.RM_REMINDER_CREATED_BY, UserSession.getInstance().getLoggedInUser().getId());

            db.insertOrThrow(DBHelper.RM_USER_TABLE_NAME, null, contentValues);
            db.close();

        } catch (Exception ex) {
            //log
            Log.i("DBConnection", "exceptopm ");
            String errorCode = ApplicationConstants.SYSTEM_FAILURE;
            Message message = new Message();
            message.setCode(errorCode);
            message.setDescription(ex.getMessage());
            throw new ApplicationRuntimeException(message);
        }
        return  true;
    }

    //==============================================================================================
    // Reminder Zone
    //==============================================================================================


    //==============================================================================================
    // Reminder Loc
    //==============================================================================================



    //==============================================================================================
    // User
    //==============================================================================================

    public boolean insertUser(User user)  {

        SQLiteDatabase db = null;
        try {
            db = mDbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_USER_ID, user.getId());
            contentValues.put(DBHelper.RM_USER_NAME, user.getName());
            db.insertOrThrow(DBHelper.RM_USER_TABLE_NAME, null, contentValues);
            db.close();
        } catch (Exception ex) {
            //log
            Log.i("DBConnection", "exceptopm ");
            String errorCode = ApplicationConstants.SYSTEM_FAILURE;
            Message message = new Message();
            message.setCode(errorCode);
            message.setDescription(ex.getMessage());
            throw new ApplicationRuntimeException(message);
        }

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

