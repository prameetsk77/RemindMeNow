package edu.asu.remindmenow.models;

/**
 * Created by Jithin Roy on 3/26/16.
 */
public class User {

    String name;
    String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.replaceAll("\\s","");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
