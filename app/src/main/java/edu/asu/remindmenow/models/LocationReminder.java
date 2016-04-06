package edu.asu.remindmenow.models;

/**
 * Created by priyama on 4/5/2016.
 */
public class LocationReminder extends Reminder{

    String location;
    String startDate;
    String endDate;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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
}
