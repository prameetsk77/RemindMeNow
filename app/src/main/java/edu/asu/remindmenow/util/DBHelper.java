package edu.asu.remindmenow.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import edu.asu.remindmenow.models.User;

/**
 * Created by priyama on 4/4/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyReminder.db";
    public static final String RM_REMINDER_TABLE_NAME = "rm_reminder";
    public static final String RM_TIME_TABLE_NAME = "rm_time";
    public static final String RM_USER_TABLE_NAME = "rm_user";
    public static final String RM_LOCATION_TABLE_NAME = "rm_location";
    public static final String RM_REMINDER_USER_REF_TABLE_NAME = "rm_reminder_user_ref";
    public static final String RM_REMINDER_LOC_REF_TABLE_NAME = "rm_reminder_loc_ref";

    public static final String RM_REMINDER_ID = "reminder_id";
    public static final String RM_REMINDER_TIME_ID = "reminder_time_id";
    public static final String RM_REMINDER_TYPE = "reminder_type";
    public static final String RM_REMINDER_TITLE = "reminder_title";
    public static final String RM_REMINDER_CREATED_DATE = "created_date";
    public static final String RM_REMINDER_CREATED_BY = "created_by";

    public static final String RM_TIME_ID ="time_id";
    public static final String RM_TIME_START_DATE = "start_date";
    public static final String RM_TIME_END_DATE = "end_date";
    public static final String RM_TIME_START_TIME = "start_time";
    public static final String RM_TIME_END_TIME = "end_time";

    public static final String RM_USER_ID = "user_id";
    public static final String RM_USER_NAME = "user_name";

    public static final String RM_LOC_ID = "loc_id";
    public static final String RM_LOC_LAT = "loc_lat";
    public static final String RM_LOC_LONG = "loc_long";
    public static final String RM_LOC_ADDRESS = "loc_address";

    public static final String RM_REMINDER_USER_ID = "reminder_user_id";
    public static final String RM_REMINDER_LOC_ID = "reminder_loc_id";




    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(
                "create table " + RM_TIME_TABLE_NAME +
                        " (" + RM_TIME_ID +" integer primary key, " +
                        RM_TIME_START_DATE + "text," +
                        RM_TIME_END_DATE + " text," +
                        RM_TIME_START_TIME + " text, " +
                        RM_TIME_END_TIME + " text)"
        );

        db.execSQL(
                "create table "+ RM_REMINDER_TABLE_NAME +
                        " (" + RM_REMINDER_ID + " integer primary key, " +
                        RM_REMINDER_TIME_ID +" integer, "+


                        RM_REMINDER_TYPE + " text," +
                        RM_REMINDER_TITLE + " text," +
                        RM_REMINDER_CREATED_DATE + " text, " +
                        RM_REMINDER_CREATED_BY + " text, " +
                        "FOREIGN KEY ("+RM_REMINDER_TIME_ID+") REFERENCES "+RM_TIME_TABLE_NAME+" ("+RM_TIME_ID+"))"
        );


        db.execSQL(
                "create table " + RM_USER_TABLE_NAME +
                        " (" + RM_USER_ID + " text primary key, " +
                        RM_USER_NAME + " text)"
        );

        db.execSQL(
                "create table " + RM_LOCATION_TABLE_NAME +
                        " (" + RM_LOC_ID + " integer primary key, " +
                        RM_LOC_LAT + " text," +
                        RM_LOC_LONG + " text," +
                        RM_LOC_ADDRESS + " text)"
        );

        db.execSQL(
                "create table " + RM_REMINDER_USER_REF_TABLE_NAME +
                        " (" + RM_REMINDER_USER_ID + " integer primary key, " +
                        RM_REMINDER_ID + " integer," +
                        RM_USER_ID + " integer," +
                        "FOREIGN KEY ("+RM_REMINDER_ID+") REFERENCES "+RM_REMINDER_TABLE_NAME+" ("+RM_REMINDER_ID+"),"+
                        "FOREIGN KEY ("+RM_USER_ID+") REFERENCES "+RM_USER_TABLE_NAME+" ("+RM_USER_ID+"))"

        );

        db.execSQL(
                "create table " + RM_REMINDER_LOC_REF_TABLE_NAME +
                        " (" + RM_REMINDER_LOC_ID +" integer primary key, " +
                        RM_REMINDER_ID + " integer," +
                        RM_LOC_ID + " integer,"+
                        "FOREIGN KEY ("+RM_REMINDER_ID+") REFERENCES "+RM_REMINDER_TABLE_NAME+" ("+RM_REMINDER_ID+"),"+
                        "FOREIGN KEY ("+RM_LOC_ID+") REFERENCES "+RM_LOCATION_TABLE_NAME+" ("+RM_LOC_ID+"))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


//
//    public int numberOfRows(){
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
//        return numRows;
//    }
//
//    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("phone", phone);
//        contentValues.put("email", email);
//        contentValues.put("street", street);
//        contentValues.put("place", place);
//        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
//        return true;
//    }
//
//    public Integer deleteContact (Integer id)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete("contacts",
//                "id = ? ",
//                new String[] { Integer.toString(id) });
//    }
//
//    public ArrayList<String> getAllCotacts() {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from contacts", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
//            res.moveToNext();
//        }
//        return array_list;
//    }
}

