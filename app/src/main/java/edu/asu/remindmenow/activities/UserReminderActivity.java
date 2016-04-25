package edu.asu.remindmenow.activities;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.models.User;
import edu.asu.remindmenow.models.UserFriendList;
import edu.asu.remindmenow.models.UserReminder;
import edu.asu.remindmenow.util.ApplicationConstants;
import edu.asu.remindmenow.util.DBConnection;
import edu.asu.remindmenow.util.DatabaseManager;
import edu.asu.remindmenow.util.DateUtilities;

public class UserReminderActivity extends AppCompatActivity {


    EditText titleTextView;
    EditText startTextView;
    EditText endTextView;

    AutoCompleteTextView addaFriend;

    UserFriendList userFriendList =new UserFriendList();
    private static String TAG = "UserReminderActivity";

    protected  void fetchFriendList(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            Log.i("user reminder", "in on completed for add fb friend " + response.getJSONArray());
                            JSONArray friendList=response.getJSONObject().getJSONArray("data");


                            for (int i=0; i<friendList.length();i++){
                                    JSONObject friendData=friendList.getJSONObject(i);

                                Log.i("FB User Reminder", friendData.getString("id"));
                                userFriendList.setFriend(friendData.getString("name"),friendData.getString("id"));

                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserReminderActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, userFriendList.getFriendName());
                            addaFriend.setAdapter(adapter);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }
        ).executeAsync();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reminder);
        fetchFriendList();
        addaFriend=(AutoCompleteTextView)findViewById(R.id.addaFriendTV);


        startTextView = (EditText)findViewById(R.id.dateTextView);
        endTextView = (EditText)findViewById(R.id.endDateTextView);
        titleTextView = (EditText)findViewById(R.id.titleEditText);
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                myCalendar.
                startTextView.setText(monthOfYear + 1 + "/" + dayOfMonth + "/" + year);
            }

        };

        startTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(UserReminderActivity.this, date, myCalendar
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

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endTextView.setText(monthOfYear + 1+"/"+dayOfMonth +"/"+year);
            }

        };

        endTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(UserReminderActivity.this, endDate, myEndCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myEndCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    public void saveUserClicked(View v) {

        UserReminder userReminder=new UserReminder();
        userReminder.setStartDate(startTextView.getText().toString());
        userReminder.setEndDate(endTextView.getText().toString());
        userReminder.setReminderTitle(titleTextView.getText().toString());
        String fbName=addaFriend.getText().toString();

        Log.i(TAG, "Start date = " + userReminder.getStartDate());
        Log.i(TAG, "End date = " + userReminder.getEndDate());



        User friend = new User();
        friend.setName(fbName);

        if (friend.getName() != null && friend.getName().equals("") == false) {
            int i=0;
            for (; i<userFriendList.getFriendName().size() ; i++) {

                if (userFriendList.getFriendName().get(i).equals(fbName)) {
                    break;
                }
            }
            if (i == userFriendList.getFriendId().size()) {

            } else {
                friend.setId(userFriendList.getFriendId().get(i));
                Log.i(TAG, "User id = " + friend.getId());
                userReminder.setFriend(friend);
            }

        }


        if (validateInput(userReminder)) {
            SQLiteDatabase db = DBConnection.getInstance().openWritableDB();
            DatabaseManager dbManager = new DatabaseManager();
            long id = dbManager.insertUserReminder(db, userReminder);
            //Log
            DBConnection.getInstance().closeDB(db);
            Toast.makeText(UserReminderActivity.this, "Reminder saved", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public boolean validateInput(UserReminder userReminder) {

        if (userReminder.getReminderTitle() == null ||
                userReminder.getReminderTitle().equals("")) {
            Toast.makeText(UserReminderActivity.this, ApplicationConstants.NO_TITLE, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userReminder.getStartDate() == null ||
                userReminder.getStartDate().equals("")) {
            Toast.makeText(UserReminderActivity.this,ApplicationConstants.NO_START_DATE, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userReminder.getEndDate() == null ||
                userReminder.getEndDate().equals("")) {
            Toast.makeText(UserReminderActivity.this, ApplicationConstants.NO_END_DATE, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userReminder.getFriend() == null ||
                userReminder.getFriend().getName().equals("")) {
            Toast.makeText(UserReminderActivity.this, ApplicationConstants.NO_FRIEND, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Date range validation
        try {
            if (DateUtilities.isPastDate(userReminder.getEndDate()) == true) {
                Toast.makeText(UserReminderActivity.this, ApplicationConstants.FUTURE_DATE, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (DateUtilities.isDateInOrder(userReminder.getStartDate(), userReminder.getEndDate()) == false) {
                Toast.makeText(UserReminderActivity.this, ApplicationConstants.VALID_DATE, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return true;
    }

}
