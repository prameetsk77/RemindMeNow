package edu.asu.remindmenow.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by priyama on 4/5/2016.
 */
public class LocationReminder extends Reminder{

    String location;
    String startDate;
    String endDate;
    String ReqID;

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

    public void setReqID(String ReqID){
        this.ReqID = ReqID;    }

    public String getReqID(){return ReqID;}
    @Override
    public String toString() {
        return (getReminderTitle()+ getStartDate() + getEndDate() + getLocation() + getCoordinates()).toString();
    }
}
