package edu.asu.remindmenow.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import edu.asu.remindmenow.Geofence.GeofenceIntentService;
import edu.asu.remindmenow.R;
import edu.asu.remindmenow.models.UserReminder;
import edu.asu.remindmenow.models.ZoneReminder;

/**
 * Created by priyama on 3/21/2016.
 */
public class GeofenceReminderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    EditText textView;
    EditText endTextView;
    EditText timeTextView;
    EditText endTimeTextView;

    String TAG = "GeoFence";
    long endTimeMillis;
    LatLng coordinates;
    Handler handler = new Handler();
    GeofenceIntentService geofenceService;

    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_reminder);
        textView = (EditText) findViewById(R.id.dateTextView);
        final Calendar myCalendar = Calendar.getInstance();
        timeTextView = (EditText) findViewById(R.id.timeTextView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Search a Location");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {
            // TODO: Get info about the selected place.
            coordinates = place.getLatLng();
            Log.i(TAG, "Place: " + place.getName());
        }

        @Override
        public void onError(Status status) {
            // TODO: Handle the error.
            Log.i(TAG, "An error occurred: " + status);
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(GeofenceReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timeTextView.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textView.setText(monthOfYear+1 + "/" + dayOfMonth + "/" + year);
            }

        };

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("Inside onclick");
                new DatePickerDialog(GeofenceReminderActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        endTextView = (EditText) findViewById(R.id.endDateTextView);
        final Calendar myEndCalendar = Calendar.getInstance();

        endTimeTextView = (EditText) findViewById(R.id.endTimeTextView);

        endTimeTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(GeofenceReminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endTimeTextView.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endTimeMillis = myCalendar.getTimeInMillis();
                endTextView.setText(monthOfYear+1 + "/" + dayOfMonth + "/" + year);
            }

        };

        endTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("Inside onclick");
                new DatePickerDialog(GeofenceReminderActivity.this, endDate, myEndCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myEndCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        geofenceService = new GeofenceIntentService();
//        startService()
    }

    public void saveGeofenceClicked(View v){

        try {


            Location lastLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i(TAG, "Current loc - " + lastLoc.getLatitude() + " Current loc " + lastLoc.getLongitude());
        } catch (SecurityException e) {
            Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
        }


        ZoneReminder geofenceReminder = new ZoneReminder();
        geofenceReminder.setCoordinates(coordinates);
        geofenceReminder.setStartDate(textView.getText().toString());
        geofenceReminder.setEndDate(endTextView.getText().toString());
        geofenceReminder.setStartTime(timeTextView.getText().toString());
        geofenceReminder.setEndTime(endTimeTextView.getText().toString());
        System.out.println("geo " + geofenceReminder.getEndTime());
        geofenceService.addGeofence(coordinates,endTimeMillis , mGoogleApiClient, this);

    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    public boolean validateInput(ZoneReminder zoneReminder) {

        if (zoneReminder.getReminderTitle() == null ||
                zoneReminder.getReminderTitle().equals("")) {
            Toast.makeText(this, "Please enter the title of the reminder.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (zoneReminder.getStartDate() == null ||
                zoneReminder.getStartDate().equals("")) {
            Toast.makeText(this, "Please enter the start date of the reminder", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (zoneReminder.getEndDate() == null ||
                zoneReminder.getEndDate().equals("")) {
            Toast.makeText(this, "Please enter the end date of the reminder..", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
