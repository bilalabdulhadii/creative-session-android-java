package com.example.creativesession;

public class Settings {
    private static final int FOCUS_DURATION = 25, SHORT_BREAK_DURATION = 5, LONG_BREAK_DURATION = 15, INTERVAL_COUNT = 4, LAST_TASK = 0;
    private static final boolean AUTO_FOCUS_STATUS = false, AUTO_BREAK_STATUS = false;
    private String name, email, password;
    private int focus, shortBreak, longBreak, interval, lastTask;
    private boolean autoFocus, autoBreak;

    public Settings(Settings settings) {
        this.name = settings.name;
        this.email = settings.email;
        this.password = settings.password;
        this.focus = settings.focus;
        this.shortBreak = settings.shortBreak;
        this.longBreak = settings.longBreak;
        this.interval = settings.interval;
        this.autoFocus = settings.autoFocus;
        this.autoBreak = settings.autoBreak;
        this.lastTask = settings.lastTask;
    }
    public Settings() {

    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getFocus() { return focus; }
    public void setFocus(int focus) { this.focus = focus; }
    public int getShortBreak() { return shortBreak; }
    public void setShortBreak(int shortBreak) { this.shortBreak = shortBreak; }
    public int getLongBreak() { return longBreak; }
    public void setLongBreak(int longBreak) { this.longBreak = longBreak; }
    public int getInterval() { return interval; }
    public void setInterval(int interval) { this.interval = interval; }
    public boolean isAutoFocus() { return autoFocus; }
    public void setAutoFocus(boolean autoFocus) { this.autoFocus = autoFocus; }
    public boolean isAutoBreak() { return autoBreak; }
    public void setAutoBreak(boolean autoBreak) { this.autoBreak = autoBreak; }

    public int getLastTask() { return lastTask; }
    public void setLastTask(int lastTask) { this.lastTask = lastTask; }

    /*public int defaultFocusDuration() { return FOCUS_DURATION; }
    public int defaultShortBreakDuration() { return SHORT_BREAK_DURATION; }
    public int defaultLongBreakDuration() { return LONG_BREAK_DURATION; }
    public int defaultIntervalCount() { return INTERVAL_COUNT; }
    public boolean defaultAutoFocusStatus() { return AUTO_FOCUS_STATUS; }
    public boolean defaultAutoBreakStatus() { return AUTO_BREAK_STATUS; }*/

    public Settings defaultSettings() {
        Settings settings = new Settings();
        settings.focus = FOCUS_DURATION;
        settings.shortBreak = SHORT_BREAK_DURATION;
        settings.longBreak = LONG_BREAK_DURATION;
        settings.interval = INTERVAL_COUNT;
        settings.autoFocus = AUTO_FOCUS_STATUS;
        settings.autoBreak = AUTO_BREAK_STATUS;
        settings.lastTask = LAST_TASK;
        return settings;
    }
}
