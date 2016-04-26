package edu.asu.remindmenow.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.asu.remindmenow.models.Time;

/**
 * Created by Jithin Roy on 4/6/16.
 */
public class DateUtilities {

    public static boolean isPastDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date givenDate = sdf.parse(date);
        Log.i("DateUtil", "ispastDate" + givenDate.toString());
        Calendar c= Calendar.getInstance();
        Log.i("DateUtil", "Current date " + c.getTime());
        return givenDate.before(c.getTime());
    }

    public static boolean isPastDate(String date, String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        Date givenDate = sdf.parse(date+" "+time);
        Log.i("DateUtil", "ispastDate" + givenDate.toString());
        Calendar c= Calendar.getInstance();
        Log.i("DateUtil", "Current date " + c.getTime());
        return givenDate.before(c.getTime());
    }

    public static boolean isFutureDate (String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date givenDate = sdf.parse(date);
        Log.i("DateUtil", "isFutureDate" + givenDate.toString());
        Calendar c = Calendar.getInstance();
        Log.i("DateUtil", "Current date " + c.getTime());
        return givenDate.after(c.getTime());
    }

    public static boolean isDateInOrder (String startDate, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date startDateObj = sdf.parse(startDate);
        Date endDateObj = sdf.parse(endDate);

        Log.i("DateUtil", "isDateInOrder " + startDateObj.toString() + " " + endDateObj.toString());

        return endDateObj.after(startDateObj) || startDate.equals(endDate);
    }

    public static boolean isDateInOrder (String startDate, String endDate, String startTime, String endTime)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date startDateObj = sdf.parse(startDate+" "+startTime);
        Date endDateObj = sdf.parse(endDate +" "+endTime);

        Log.i("DateUtil", "isDateInOrder " + startDateObj.toString() + " " + endDateObj.toString());

        return endDateObj.after(startDateObj) || startDate.equals(endDate);
    }
}
