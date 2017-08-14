package com.example.kwy2868.finalproject.Model;

/**
 * Created by kwy2868 on 2017-08-14.
 */

public class AlarmEvent {
    private String title;
    private String description;

    public AlarmEvent(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

