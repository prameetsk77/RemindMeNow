package edu.asu.remindmenow.userManager;

import edu.asu.remindmenow.models.User;

/**
 * Created by Jithin Roy on 3/26/16.
 */
public class UserSession {

    private User loggedInUser;

    private static UserSession ourInstance = new UserSession();

    public static UserSession getInstance() {
        return ourInstance;
    }

    private UserSession() {
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }


}