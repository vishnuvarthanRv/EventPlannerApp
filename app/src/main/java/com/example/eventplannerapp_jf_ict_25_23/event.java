package com.example.eventplannerapp_jf_ict_25_23;

public class event {

    private String id;
    private String name;
    private String description;
    private long dateTime;
    private int reminderMinutes;

    // Constructor
    public event(
            String id,
            String name,
            String description,
            long dateTime,
            int reminderMinutes) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.reminderMinutes = reminderMinutes;
    }

    // Get ID
    public String getId() {
        return id;
    }

    // Set ID
    public void setId(String id) {
        this.id = id;
    }

    // Get Name
    public String getName() {
        return name;
    }

    // Set Name
    public void setName(String name) {
        this.name = name;
    }

    // Get Description
    public String getDescription() {
        return description;
    }

    // Set Description
    public void setDescription(String description) {
        this.description = description;
    }

    // Get Date and Time
    public long getDateTime() {
        return dateTime;
    }

    // Set Date and Time
    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    // Get Reminder Minutes
    public int getReminderMinutes() {
        return reminderMinutes;
    }

    // Set Reminder Minutes
    public void setReminderMinutes(int reminderMinutes) {
        this.reminderMinutes = reminderMinutes;
    }

}