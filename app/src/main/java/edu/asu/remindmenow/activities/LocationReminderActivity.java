package edu.asu.remindmenow.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.models.LocationReminder;
import edu.asu.remindmenow.models.ZoneReminder;

public class LocationReminderActivity extends AppCompatActivity {


    EditText textView;
    EditText endTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_reminder);

        textView = (EditText)findViewById(R.id.dateTextView);
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textView.setText(monthOfYear+1+"/"+dayOfMonth+"/"+year);
            }

        };

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("Inside onclick");
                new DatePickerDialog(LocationReminderActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        endTextView = (EditText)findViewById(R.id.endDateTextView);
        final Calendar myEndCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endTextView.setText(monthOfYear+1+"/"+dayOfMonth+"/"+year);
            }

        };

        endTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("Inside onclick");
                new DatePickerDialog(LocationReminderActivity.this, endDate, myEndCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myEndCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    public boolean validateInput(LocationReminder locationReminder) {

        if ( locationReminder.getReminderTitle() == null ||
                locationReminder.getReminderTitle().equals("")) {
            Toast.makeText(this, "Please enter the title of the reminder.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ( locationReminder.getStartDate() == null ||
                locationReminder.getStartDate().equals("")) {
            Toast.makeText(this, "Please enter the start date of the reminder", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ( locationReminder.getEndDate() == null ||
                locationReminder.getEndDate().equals("")) {
            Toast.makeText(this, "Please enter the end date of the reminder..", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
