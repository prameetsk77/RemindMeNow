package edu.asu.remindmenow.models;

/**
 * Created by Priyama Biswas on 3/26/16.
 */
public class UserReminder extends Reminder{
    
    User friend;

    String startDate;
    String endDate;


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
}
