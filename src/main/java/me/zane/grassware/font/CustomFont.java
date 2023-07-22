package me.zane.grassware.font;

public class CustomFont {
    private final int displayList;
    private long lastUsage;

    public CustomFont(int displayList, long lastUsage) {
        this.displayList = displayList;
        this.lastUsage = lastUsage;
    }

    public int getDisplayList() {
        return this.displayList;
    }

    public long getLastUsage() {
        return this.lastUsage;
    }

    public void setLastUsage(long lastUsage) {
        this.lastUsage = lastUsage;
    }
}