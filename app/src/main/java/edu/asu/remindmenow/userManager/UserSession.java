package edu.asu.remindmenow.userManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import edu.asu.remindmenow.activities.SettingsActivity;
import edu.asu.remindmenow.models.User;

/**
 * Created by Jithin Roy on 3/26/16.
 */
public class UserSession {

    private User loggedInUser;
    private Context context;
    private static UserSession ourInstance = new UserSession();



    public static UserSession getInstance() {
        return ourInstance;
    }

    private UserSession() {
        Log.i("Usersession", "constructor");
        loggedInUser = null;
    }

    public void setContext(Context ctx) {
        this.context = ctx;
        restoreSession();
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        saveSession();
    }

    private void saveSession() {
        String prefName = SettingsActivity.PREFS_NAME;

        SharedPreferences settings = this.context.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userId", this.loggedInUser.getId());
        editor.putString("userName", this.loggedInUser.getName());
        editor.commit();
    }

    private void restoreSession () {

        String prefName = SettingsActivity.PREFS_NAME;
        SharedPreferences settings = this.context.getSharedPreferences(prefName, 0);
        String userId = settings.getString("userId", null);
        String userName = settings.getString("userName", null);

        this.loggedInUser = new User();
        this.loggedInUser.setId(userId);
        this.loggedInUser.setName(userName);
    }



}
