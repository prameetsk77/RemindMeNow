package edu.asu.remindmenow.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import edu.asu.remindmenow.exception.ApplicationRuntimeException;
import edu.asu.remindmenow.models.LocationReminder;
import edu.asu.remindmenow.models.Message;
import edu.asu.remindmenow.models.Reminder;
import edu.asu.remindmenow.models.Time;
import edu.asu.remindmenow.models.User;
import edu.asu.remindmenow.models.UserReminder;
import edu.asu.remindmenow.models.ZoneReminder;
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
            reminder.setReminderType(cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_TYPE)));

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

    public void deleteUserReminder(SQLiteDatabase db, long reminderId) {
        UserReminder userReminder = getReminder(db, reminderId);

        // TODO: delete from time table
        // Delete the entry from time table
        // deleteTime(db,0);

        // Remove from user - reminder ref table
        deleteRem_User_Ref(db, reminderId);

        String table = DBHelper.RM_REMINDER_TABLE_NAME;
        String whereClause = "_id" + "=?";
        String[] whereArgs = new String[] { String.valueOf(reminderId) };
        db.delete(table, whereClause, whereArgs);

    }

    public void deleteRem_User_Ref(SQLiteDatabase db, long reminderId) {
        String table = DBHelper.RM_REMINDER_USER_REF_TABLE_NAME;
        String whereClause = DBHelper.RM_REMINDER_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(reminderId) };
        db.delete(table, whereClause, whereArgs);
    }

    //==============================================================================================
    // Reminder Zone
    //==============================================================================================

    public List<ZoneReminder> getAllZoneReminders(SQLiteDatabase db) {

        ArrayList<ZoneReminder> zoneReminderList = new ArrayList<ZoneReminder>();

        Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_TABLE_NAME +
                " WHERE " + DBHelper.RM_REMINDER_TYPE +" = \"Z\"", null );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i(TAG, "Reminder - " + cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_CREATED_DATE)));
            long reminderID = cursor.getLong(cursor.getColumnIndex(DBHelper.RM_REMINDER_ID));
            ZoneReminder reminder = getZoneReminderFromRemID(db, reminderID);

            if(reminder == null)
                Log.e(TAG, "reminder == nul in getAllZoneReminders");
            else
                zoneReminderList.add(reminder);
            cursor.moveToNext();
        }

        return zoneReminderList;
    }

        public long insertZoneReminder(SQLiteDatabase db, ZoneReminder reminder) {

        try {

            ContentValues contentValues = new ContentValues();

            // Create time entry
            long timeId = insertTime(db,reminder.getStartDate(), reminder.getEndDate(),
                    reminder.getStartTime(), reminder.getEndTime());

            // Create reminder entry
            contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_REMINDER_TYPE,"Z");
            contentValues.put(DBHelper.RM_REMINDER_TIME_ID,timeId);
            contentValues.put(DBHelper.RM_REMINDER_TITLE,reminder.getReminderTitle());
            contentValues.put(DBHelper.RM_REMINDER_CREATED_DATE,System.currentTimeMillis());
            contentValues.put(DBHelper.RM_REMINDER_CREATED_BY, UserSession.getInstance().getLoggedInUser().getId());
            long reminderId = db.insertOrThrow(DBHelper.RM_REMINDER_TABLE_NAME, null, contentValues);

            // Create reminder-user ref entry
            contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_LOC_LAT,reminder.getCoordinates().latitude);
            contentValues.put(DBHelper.RM_LOC_LONG,reminder.getCoordinates().longitude);
            contentValues.put(DBHelper.RM_LOC_ADDRESS,reminder.getLocation());
            contentValues.put(DBHelper.RM_LOC_REQ_ID,"");
            long locId = db.insertOrThrow(DBHelper.RM_LOCATION_TABLE_NAME, null, contentValues);

            contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_REMINDER_ID,reminderId);
            contentValues.put(DBHelper.RM_LOC_ID,locId);
            db.insertOrThrow(DBHelper.RM_REMINDER_LOC_REF_TABLE_NAME, null, contentValues);

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


    public ZoneReminder getZoneReminderFromRemID(SQLiteDatabase db, long reminderId) {

        try {

            Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_TABLE_NAME+
                    " WHERE " + DBHelper.RM_REMINDER_ID +" = \""+reminderId+"\"", null );

            if (cursor.moveToFirst()) {

                ZoneReminder reminder = new ZoneReminder();
                reminder.setReminderTitle(cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_TITLE)));

                long timeId = cursor.getLong(cursor.getColumnIndex(DBHelper.RM_REMINDER_TIME_ID));
                Time time = getTime(db, timeId);
                reminder.setStartTime(time.getStartTime());
                reminder.setEndTime(time.getEndTime());
                reminder.setStartDate(time.getStartDate());
                reminder.setEndDate(time.getEndDate());
                reminder.setReminderTitle(cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_TITLE)));
                setZoneReminderAddress(db, reminderId, reminder);
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

    private void setZoneReminderAddress(SQLiteDatabase db, long reminderId, ZoneReminder reminder){
        try {

            Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_LOC_REF_TABLE_NAME+
                    " WHERE " + DBHelper.RM_REMINDER_ID +" = \""+reminderId+"\"", null );

            if (cursor.moveToFirst()) {
                long loc_id = (cursor.getLong(cursor.getColumnIndex(DBHelper.RM_LOC_ID)));

                cursor =  db.rawQuery( "select * from " + DBHelper.RM_LOCATION_TABLE_NAME +
                        " WHERE " + DBHelper.RM_LOC_ID +" = \""+loc_id+"\"", null );
                if (cursor.moveToFirst()) {
                    double latitude = (cursor.getDouble(cursor.getColumnIndex(DBHelper.RM_LOC_LAT)));
                    double longitude = (cursor.getDouble(cursor.getColumnIndex(DBHelper.RM_LOC_LONG)));
                    String reqId = (cursor.getString(cursor.getColumnIndex(DBHelper.RM_LOC_REQ_ID)));
                    LatLng coordinates = new LatLng(latitude,longitude);
                    reminder.setCoordinates(coordinates);
                    reminder.setLocation((cursor.getString(cursor.getColumnIndex(DBHelper.RM_LOC_ADDRESS))));
                }
            }
            return;

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
    // Reminder Loc
    //==============================================================================================

    public List<LocationReminder> getAllLocationReminders(SQLiteDatabase db) {

        ArrayList<LocationReminder> locationReminderList = new ArrayList<LocationReminder>();

        Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_TABLE_NAME +
                " WHERE " + DBHelper.RM_REMINDER_TYPE +" = \"Z\"", null );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i(TAG, "Reminder - " + cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_CREATED_DATE)));
            long reminderID = cursor.getLong(cursor.getColumnIndex(DBHelper.RM_REMINDER_ID));
            LocationReminder reminder = getLocationReminderFromRemID(db, reminderID);

            if(reminder == null)
                Log.e(TAG, "reminder == nul in getAllZoneReminders");
            else
                locationReminderList.add(reminder);
            cursor.moveToNext();
        }

        return locationReminderList;
    }

    public long insertLocationReminder(SQLiteDatabase db, LocationReminder reminder) {

        try {
            ContentValues contentValues = new ContentValues();

            // Create time entry
            long timeId = insertTime(db,reminder.getStartDate(), reminder.getEndDate(),
                    null, null);

            // Create reminder entry
            contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_REMINDER_TYPE,"L");
            contentValues.put(DBHelper.RM_REMINDER_TIME_ID,timeId);
            contentValues.put(DBHelper.RM_REMINDER_TITLE,reminder.getReminderTitle());
            contentValues.put(DBHelper.RM_REMINDER_CREATED_DATE,System.currentTimeMillis());
            contentValues.put(DBHelper.RM_REMINDER_CREATED_BY, UserSession.getInstance().getLoggedInUser().getId());
            long reminderId = db.insertOrThrow(DBHelper.RM_REMINDER_TABLE_NAME, null, contentValues);

            // Creare reminder-user ref entry
            contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_LOC_LAT,reminder.getCoordinates().latitude);
            contentValues.put(DBHelper.RM_LOC_LONG,reminder.getCoordinates().longitude);
            contentValues.put(DBHelper.RM_LOC_ADDRESS,reminder.getLocation());
            long locId = db.insertOrThrow(DBHelper.RM_LOCATION_TABLE_NAME, null, contentValues);

            contentValues = new ContentValues();
            contentValues.put(DBHelper.RM_REMINDER_ID,reminderId);
            contentValues.put(DBHelper.RM_LOC_ID,locId);
            db.insertOrThrow(DBHelper.RM_REMINDER_LOC_REF_TABLE_NAME, null, contentValues);

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


    public LocationReminder getLocationReminderFromRemID(SQLiteDatabase db, long reminderId) {

        try {

            Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_TABLE_NAME+
                    " WHERE " + DBHelper.RM_REMINDER_ID +" = \""+reminderId+"\"", null );

            if (cursor.moveToFirst()) {

                LocationReminder reminder = new LocationReminder();
                reminder.setReminderTitle(cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_TITLE)));

                long timeId = cursor.getLong(cursor.getColumnIndex(DBHelper.RM_REMINDER_TIME_ID));
                Time time = getTime(db, timeId);
                reminder.setStartDate(time.getStartDate());
                reminder.setEndDate(time.getEndDate());
                reminder.setReminderTitle(cursor.getString(cursor.getColumnIndex(DBHelper.RM_REMINDER_TITLE)));
                setLocationReminderAddress(db, reminderId, reminder);
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

    private void setLocationReminderAddress(SQLiteDatabase db, long reminderId, LocationReminder reminder){
        try {

            Cursor cursor =  db.rawQuery( "select * from " + DBHelper.RM_REMINDER_LOC_REF_TABLE_NAME+
                    " WHERE " + DBHelper.RM_REMINDER_ID +" = \""+reminderId+"\"", null );

            if (cursor.moveToFirst()) {
                long loc_id = (cursor.getLong(cursor.getColumnIndex(DBHelper.RM_LOC_ID)));

                cursor =  db.rawQuery( "select * from " + DBHelper.RM_LOCATION_TABLE_NAME +
                        " WHERE " + DBHelper.RM_LOC_ID +" = \""+loc_id+"\"", null );
                if (cursor.moveToFirst()) {
                    double latitude = (cursor.getDouble(cursor.getColumnIndex(DBHelper.RM_LOC_LAT)));
                    double longitude = (cursor.getDouble(cursor.getColumnIndex(DBHelper.RM_LOC_LONG)));
                    LatLng coordinates = new LatLng(latitude,longitude);
                    reminder.setCoordinates(coordinates);
                    reminder.setLocation((cursor.getString(cursor.getColumnIndex(DBHelper.RM_LOC_ADDRESS))));
                }
            }
            return;

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

    public void deleteTime (SQLiteDatabase db, long timeId) {
        String table = DBHelper.RM_TIME_TABLE_NAME;
        String whereClause = "_id" + "=?";
        String[] whereArgs = new String[] { String.valueOf(timeId) };
        db.delete(table, whereClause, whereArgs);
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

