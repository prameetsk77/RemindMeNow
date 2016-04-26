package edu.asu.remindmenow.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.models.Reminder;
import edu.asu.remindmenow.util.DBConnection;

import edu.asu.remindmenow.util.DatabaseManager;

/**
 * Created by priyama on 4/6/2016.
 */
public class ReminderListViewLoader extends BaseActivity {

    private  ListView mListView;
    private  ListViewAdapter mAdapter;

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

        setContentView(R.layout.activity_reminder_list);

        mListView = (ListView)findViewById(R.id.reminderListView);
        mAdapter = new ListViewAdapter(this, getReminderList());
        mListView.setAdapter(mAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("ReminderListViewLoader", "On item click");
//                Reminder rm = (Reminder)mAdapter.getItem(position);
//                SQLiteDatabase db = DBConnection.getInstance().openWritableDB();
//                DatabaseManager dbManager = new DatabaseManager();
//                dbManager.deleteReminder(db,rm.getId());
//                DBConnection.getInstance().closeDB(db);

            }
        });

    }

    private class ListViewAdapter extends BaseAdapter {

        Context context;
        List<Reminder> reminderList = null;
        private  LayoutInflater inflater = null;

        public ListViewAdapter(Context context, List<Reminder> data) {
            this.context = context;
            this.reminderList = data;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return reminderList.size();
        }

        @Override
        public Object getItem(int position) {
            return reminderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View vi = convertView;
            Reminder reminder = (Reminder)getItem(position);
            if (vi == null)
                vi = inflater.inflate(R.layout.reminder_list_row, null);
            TextView text = (TextView) vi.findViewById(R.id.header);
            text.setText(reminder.getReminderTitle());
            return vi;
        }
    }
}
