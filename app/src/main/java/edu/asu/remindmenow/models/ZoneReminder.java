package edu.asu.remindmenow.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Priyama Biswas on 3/26/16.
 */
public class ZoneReminder extends Reminder{
    String location;
    String startDate;
    String endDate;
    String startTime;
    String endTime;

    LatLng coordinates;

    public LatLng getCoordinates(){return coordinates;}

    public void setCoordinates(LatLng coordinates){this.coordinates = coordinates;


    }

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return getReminderTitle()+ getStartDate() + getEndDate() + getStartTime() + getEndTime() + getLocation() + getCoordinates().toString();
    }
}
