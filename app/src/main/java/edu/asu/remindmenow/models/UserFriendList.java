package edu.asu.remindmenow.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Priyama Biswas on 3/26/16.
 */
public class UserFriendList {
    List<String> friendName = new ArrayList<>();
    List<String> friendId = new ArrayList<>();

    public List<String> getFriendName() {
        return friendName;
    }

    public void setFriendName(List<String> friendName) {
        this.friendName = friendName;
    }

    public List<String> getFriendId() {
        return friendId;
    }

    public void setFriendId(List<String> friendId) {
        this.friendId = friendId;
    }

    public void setFriend(String name, String id) {
        this.friendId.add(id);
        this.friendName.add(name);
    }
}
