package me.zane.grassware.manager;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.features.Feature;

import java.util.ArrayList;

public class FriendManager extends Feature {
    private final ArrayList<FriendPlayer> friendList = new ArrayList<>();

    public void addFriend(String name) {
        if (!isFriend(name)) {
            friendList.add(new FriendPlayer(name));
        }
    }

    public void removeFriend(String name) {
        friendList.removeIf(player -> player.getName().equals(name));
    }

    public ArrayList<FriendPlayer> getFriendList() {
        return friendList;
    }

    public boolean isFriend(String name) {
        return friendList.stream().anyMatch(player -> player.getName().equals(name));
    }

    public void saveFriends() {
        for (FriendPlayer friend : friendList) {
            register(friend.getName(), "");
        }
    }

    public void onLoad() {
        friendList.clear();
    }

    public static class FriendPlayer {
        private final String name;

        public FriendPlayer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
