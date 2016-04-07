package edu.asu.remindmenow.activities;

import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.models.Reminder;
import edu.asu.remindmenow.util.DBConnection;
import edu.asu.remindmenow.util.DBHelper;
import edu.asu.remindmenow.util.DatabaseManager;

/**
 * Created by priyama on 4/6/2016.
 */
public class ReminderListViewLoader extends ListActivity{

    public List<Reminder> getReminderList(){
        SQLiteDatabase db = DBConnection.getInstance().openWritableDB();
        DatabaseManager dbManager = new DatabaseManager();
        List<Reminder> reminderList = dbManager.getAllReminders(db);
        DBConnection.getInstance().closeDB(db);
        return reminderList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String[] reminderArray=getReminderList().toArray()
        String[] reminderArray = new String[getReminderList().size()];
        for (int i=0; i<reminderArray.length; i++){
            reminderArray[i]=getReminderList().get(i).getReminderTitle();
        }
        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_reminder_list,reminderArray));

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
