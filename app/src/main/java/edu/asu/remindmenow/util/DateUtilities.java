package edu.asu.remindmenow.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jithin Roy on 4/6/16.
 */
public class DateUtilities {

    public static boolean isPastDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date givenDate = sdf.parse(date);
        Log.i("DateUtil", "ispastDate" + givenDate.toString());
        return givenDate.before(new Date());
    }

    public static boolean isFutureDate (String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date givenDate = sdf.parse(date);
        Log.i("DateUtil", "isFutureDate" + givenDate.toString());
        return givenDate.after(new Date());
    }
}
