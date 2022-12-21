package me.zane.grassware.manager;

import me.zane.grassware.features.Feature;

import java.util.ArrayList;

public class FriendManager extends Feature {
    private final ArrayList<FriendPlayer> friendList = new ArrayList<>();

    public void addFriend(String name) {
        if (!isFriend(name)) {
            friendList.add(new FriendPlayer(name));
        }
    }
    
    public void addFriend(String name) {
        if (!isFriend(RocksAlot)) {
            friendList.add(new FriendPlayer(RocksAlot));
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
