package edu.asu.remindmenow.activities;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.util.Calendar;

import edu.asu.remindmenow.Location.Location_GeofenceIntentService;
import edu.asu.remindmenow.R;
import edu.asu.remindmenow.models.LocationReminder;
import edu.asu.remindmenow.util.ApplicationConstants;
import edu.asu.remindmenow.util.DBConnection;
import edu.asu.remindmenow.util.DatabaseManager;
import edu.asu.remindmenow.util.DateUtilities;

public class LocationReminderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    EditText startDateTextView;
    EditText endTextView;

    EditText title;

    String TAG = "Location Reminder";
    String locationName;
    long endTimeMillis;
    LatLng coordinates;
    Handler handler = new Handler();
    Location_GeofenceIntentService locationService;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_reminder);


        startDateTextView = (EditText)findViewById(R.id.dateTextView);
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDateTextView.setText(monthOfYear+1+"/"+dayOfMonth+"/"+year);
            }

        };

        startDateTextView.setOnClickListener(new View.OnClickListener() {

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

                endTimeMillis = myCalendar.getTimeInMillis();
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

        title = (EditText) findViewById(R.id.Title);

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
                locationName = place.getName().toString();
                coordinates = place.getLatLng();
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        locationService = new Location_GeofenceIntentService();
    }

//    public boolean validateInput(LocationReminder locationReminder) {
//
//        if ( locationReminder.getReminderTitle() == null ||
//                locationReminder.getReminderTitle().equals("")) {
//            Toast.makeText(this, "Please enter the title of the reminder.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if ( locationReminder.getStartDate() == null ||
//                locationReminder.getStartDate().equals("")) {
//            Toast.makeText(this, "Please enter the start date of the reminder", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if ( locationReminder.getEndDate() == null ||
//                locationReminder.getEndDate().equals("")) {
//            Toast.makeText(this, "Please enter the end date of the reminder..", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        return true;
//    }

    public void saveGeofenceClicked(View v){

        try {
            Location lastLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i(TAG, "Current loc - " + lastLoc.getLatitude() + " Current loc " + lastLoc.getLongitude());
        } catch (SecurityException e) {
            Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
        }

        LocationReminder locationReminder = new LocationReminder();
        locationReminder.setCoordinates(coordinates);
        locationReminder.setReminderTitle(title.getText().toString());
        locationReminder.setLocation(locationName);
        locationReminder.setStartDate(startDateTextView.getText().toString());
        locationReminder.setEndDate(endTextView.getText().toString());
        locationReminder.setReqID(locationReminder.toString());


        if (validateInput(locationReminder)) {
            locationService.addGeofence(locationReminder, endTimeMillis, mGoogleApiClient, this);
            SQLiteDatabase db = DBConnection.getInstance().openWritableDB();
            DatabaseManager dbManager = new DatabaseManager();
            long id = dbManager.insertLocationReminder(db, locationReminder);
            //Log
            Log.e(TAG, "Location Reminder ID: " + id + " added.");
            DBConnection.getInstance().closeDB(db);
            Toast.makeText(LocationReminderActivity.this, "Reminder saved", Toast.LENGTH_SHORT).show();
            finish();
        }

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


    public boolean validateInput(LocationReminder locationReminder) {

        if (locationReminder.getReminderTitle() == null ||
                locationReminder.getReminderTitle().equals("")) {
            Toast.makeText(LocationReminderActivity.this, ApplicationConstants.NO_TITLE, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (locationReminder.getStartDate() == null ||
                locationReminder.getStartDate().equals("")) {
            Toast.makeText(LocationReminderActivity.this, ApplicationConstants.NO_START_DATE, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (locationReminder.getEndDate() == null ||
                locationReminder.getEndDate().equals("")) {
            Toast.makeText(LocationReminderActivity.this, ApplicationConstants.NO_END_DATE, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (locationReminder.getLocation() == null ||
                locationReminder.getLocation().equals("")) {
            Toast.makeText(LocationReminderActivity.this, ApplicationConstants.NO_LOCATION, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Date range validation
        try {
            if (DateUtilities.isPastDate(locationReminder.getEndDate()) == true) {
                Toast.makeText(LocationReminderActivity.this,ApplicationConstants.FUTURE_DATE, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (DateUtilities.isDateInOrder(locationReminder.getStartDate(), locationReminder.getEndDate()) == false) {
                Toast.makeText(LocationReminderActivity.this, ApplicationConstants.VALID_DATE, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return true;
    }
}
