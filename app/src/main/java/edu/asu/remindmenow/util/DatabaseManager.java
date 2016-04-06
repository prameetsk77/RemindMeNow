package edu.asu.remindmenow.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import edu.asu.remindmenow.exception.ApplicationRuntimeException;
import edu.asu.remindmenow.models.Message;
import edu.asu.remindmenow.models.Reminder;
import edu.asu.remindmenow.models.User;
import edu.asu.remindmenow.models.UserReminder;
import edu.asu.remindmenow.userManager.UserSession;

/**
 * Created by priyama on 4/4/2016.
 */
public class DatabaseManager {

    private static String TAG = "DATABASEMANAGER_TAG";

    //==============================================================================================
    // All Reminders
    //==============================================================================================

    public List<Reminder> getAllReminders(SQLiteDatabase db) {

        Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_TABLE_NAME, null );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i(TAG, "REminder - " + cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_CREATED_DATE)));
            cursor.moveToNext();
        }


        return null;
    }

    //==============================================================================================
    // Reminder User
    //==============================================================================================

    public long insertUserReminder(SQLiteDatabase db, UserReminder reminder) {

        try {

            ContentValues contentValues = new ContentValues();

            // Create time entry
            contentValues.put(DBHelper.RM_TIME_START_DATE,reminder.getStartDate());
            contentValues.put(DBHelper.RM_TIME_END_DATE, reminder.getEndDate());
            long timeId = db.insertOrThrow(DBHelper.RM_TIME_TABLE_NAME, null, contentValues);

            // Create reminder entry
            contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_REMINDER_TYPE,"U");
            contentValues.put(DBHelper.RM_REMINDER_TIME_ID,timeId);
            contentValues.put(DBHelper.RM_REMINDER_TITLE,reminder.getReminderTitle());
            contentValues.put(DBHelper.RM_REMINDER_CREATED_DATE,System.currentTimeMillis());
            contentValues.put(DBHelper.RM_REMINDER_CREATED_BY, UserSession.getInstance().getLoggedInUser().getId());
            long reminderId = db.insertOrThrow(DBHelper.RM_REMINDER_TABLE_NAME, null, contentValues);

            // Create user entry
            long friendId = insertUser(db, reminder.getFriend());

            // Creare reminder-user ref entry
            contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_USER_ID,friendId);
            contentValues.put(DBHelper.RM_REMINDER_ID,reminderId);
            db.insertOrThrow(DBHelper.RM_REMINDER_USER_REF_TABLE_NAME, null, contentValues);
            db.close();
            return reminderId;

        } catch (Exception ex) {
            ex.printStackTrace();
            String errorCode = ApplicationConstants.SYSTEM_FAILURE;
            Message message = new Message();
            message.setCode(errorCode);
            message.setDescription(ex.getMessage());
            throw new ApplicationRuntimeException(message);
        }

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

    public long insertUser(SQLiteDatabase db, User user)  {

        if (getUser(db,user.getId()) != null) {
            long userId = Long.parseLong(user.getId());
            return userId;
        }

        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_USER_ID, user.getId());
            contentValues.put(DBHelper.RM_USER_NAME, user.getName());
            long id = db.insertOrThrow(DBHelper.RM_USER_TABLE_NAME, null, contentValues);
            return id;

        } catch (Exception ex) {

            ex.printStackTrace();
            String errorCode = ApplicationConstants.SYSTEM_FAILURE;
            Message message = new Message();
            message.setCode(errorCode);
            message.setDescription(ex.getMessage());
            throw new ApplicationRuntimeException(message);
        }
    }
    //
    public User getUser(SQLiteDatabase db, String id){

        Cursor res =  db.rawQuery( "select * from rm_user where " +DBHelper.RM_USER_ID +" = "+id+"", null );
        if (res.moveToFirst()) {
            User user = new User();
            user.setId(res.getString(res.getColumnIndex(DBHelper.RM_USER_ID)));
            user.setName(res.getString(res.getColumnIndex(DBHelper.RM_USER_NAME)));
            return user;
        }
        return null;
    }


}

