package edu.asu.remindmenow.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.asu.remindmenow.exception.ApplicationRuntimeException;
import edu.asu.remindmenow.models.Message;
import edu.asu.remindmenow.models.Reminder;
import edu.asu.remindmenow.models.Time;
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
        List<Reminder> reminderList=new ArrayList<Reminder>();
        while (!cursor.isAfterLast()) {
            Reminder reminder=new Reminder();
            reminder.setReminderTitle(cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_TITLE)));
//            String reminderType = cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_TYPE));
//            if (reminderType.equals("U")) {
//
//            }
            reminderList.add(reminder);
            cursor.moveToNext();
        }
        return reminderList;
    }

    //==============================================================================================
    // Reminder User
    //==============================================================================================

    public List<UserReminder> getAllUserReminders(SQLiteDatabase db) {

        Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_TABLE_NAME +
                " WHERE " + DBHelper.RM_REMINDER_TYPE +" = \"U\"", null );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i(TAG, "REminder - " + cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_CREATED_DATE)));
            cursor.moveToNext();
        }

        return null;
    }

    public long isUserPresentInReminder(SQLiteDatabase db, String userId) {

        Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_USER_REF_TABLE_NAME+
                " WHERE " + DBHelper.RM_USER_ID +" = \""+userId+"\"", null );

        Log.i(TAG, "select * from " + DBHelper.RM_REMINDER_USER_REF_TABLE_NAME+
                " WHERE " + DBHelper.RM_USER_ID +" = \""+userId+"\"");

        Log.i(TAG, "count "+ cursor.getCount());
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndex(DBHelper.RM_REMINDER_ID));
            Log.i(TAG, "DB found user " + id);
            return id;
        }
        return -1;
    }


    public long insertUserReminder(SQLiteDatabase db, UserReminder reminder) {

        try {

            ContentValues contentValues = new ContentValues();

            // Create time entry
            contentValues.put(DBHelper.RM_TIME_START_DATE,reminder.getStartDate());
            contentValues.put(DBHelper.RM_TIME_END_DATE, reminder.getEndDate());
            long timeId = insertTime(db,reminder.getStartDate(), reminder.getEndDate(), null, null);

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


    public UserReminder getReminder(SQLiteDatabase db, long reminderId) {

        try {

            Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_TABLE_NAME+
                    " WHERE " + DBHelper.RM_REMINDER_ID +" = \""+reminderId+"\"", null );

            if (cursor.moveToFirst()) {

                UserReminder reminder = new UserReminder();
                reminder.setReminderTitle(cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_TITLE)));

                long timeId = cursor.getLong(cursor.getColumnIndex(DBHelper.RM_REMINDER_TIME_ID));
                Time time = getTime(db, timeId);
                reminder.setStartDate(time.getStartDate());
                reminder.setEndDate(time.getEndDate());

                return reminder;

            }
            return null;

        } catch (Exception ex) {
            ex.printStackTrace();
            String errorCode = ApplicationConstants.SYSTEM_FAILURE;
            Message message = new Message();
            message.setCode(errorCode);
            message.setDescription(ex.getMessage());
            throw new ApplicationRuntimeException(message);
        }

    }

    public void deleteUserReminder(long reminderId) {
//        String table = "beaconTable";
//        String whereClause = "_id" + "=?";
//        String[] whereArgs = new String[] { String.valueOf(row) };
//        db.delete(table, whereClause, whereArgs);
    }

    //==============================================================================================
    // Reminder Zone
    //==============================================================================================



    //==============================================================================================
    // Reminder Loc
    //==============================================================================================

    //==============================================================================================
    // Time
    //==============================================================================================

    public long insertTime(SQLiteDatabase db, String startDate, String endDate, String startTime, String endTime) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.RM_TIME_START_DATE,startDate);
        contentValues.put(DBHelper.RM_TIME_END_DATE, endDate);
        if (startTime != null) {
            contentValues.put(DBHelper.RM_TIME_START_TIME, startTime);
        }
        if (endTime != null) {
            contentValues.put(DBHelper.RM_TIME_END_TIME, endTime);
        }
        long timeId = db.insertOrThrow(DBHelper.RM_TIME_TABLE_NAME, null, contentValues);
        return timeId;
    }


    public Time getTime(SQLiteDatabase db, long timeId) {
        try {

            Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_TIME_TABLE_NAME+
                    " WHERE " + DBHelper.RM_TIME_ID +" = \""+timeId+"\"", null );

            if (cursor.moveToFirst()) {

                Time time = new Time();
                time.setTimeId(timeId);
                time.setStartDate(cursor.getString(cursor.getColumnIndex(DBHelper.RM_TIME_START_DATE)));
                time.setEndDate(cursor.getString(cursor.getColumnIndex(DBHelper.RM_TIME_END_DATE)));
                time.setStartTime(cursor.getString(cursor.getColumnIndex(DBHelper.RM_TIME_START_TIME)));
                time.setEndTime(cursor.getString(cursor.getColumnIndex(DBHelper.RM_TIME_END_TIME)));
                return  time;

            }
            return null;

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
            long userId = Long.parseLong(user.getId());
            long id = db.insertOrThrow(DBHelper.RM_USER_TABLE_NAME, null, contentValues);
            return userId;

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
            Log.i(TAG, "Found user inside getUser");
            User user = new User();
            user.setId(res.getString(res.getColumnIndex(DBHelper.RM_USER_ID)));
            user.setName(res.getString(res.getColumnIndex(DBHelper.RM_USER_NAME)));
            return user;
        }
        return null;
    }


}

