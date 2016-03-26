package edu.asu.remindmenow;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserReminderActivity extends AppCompatActivity {


    EditText textView;
    EditText endTextView;
    AutoCompleteTextView addaFriend;
    List<String> friendDataList=new ArrayList<String>();

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
                                    friendDataList.add(friendData.getString("name"));
//                                  String friendId=friendData.get("id");

                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserReminderActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, friendDataList);
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


//        String[] COUNTRIES = new String[] {
//                "Belgium", "France", "Italy", "Germany", "Spain"
//        };
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
//
//        addaFriend.setAdapter(adapter);


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
                textView.setText(monthOfYear+"/"+dayOfMonth+"/"+year);
            }

        };

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("Inside onclick");
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
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endTextView.setText(monthOfYear+"/"+dayOfMonth+"/"+year);
            }

        };

        endTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println("Inside onclick");
                new DatePickerDialog(UserReminderActivity.this, endDate, myEndCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myEndCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

}
