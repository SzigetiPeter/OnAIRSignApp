package com.dev.szpeter.onairserver.data;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
public class TimeEntry {

    @Id
    public long id;

    public TimeEntry() {
    }

    public TimeEntry(String displayText, Date startTime, Date endTime, int duration) {
        this.displayText = displayText;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    @Getter(AccessLevel.PUBLIC) @Setter
    private String displayText;

    @Getter(AccessLevel.PUBLIC) @Setter
    private Date startTime;

    @Getter(AccessLevel.PUBLIC) @Setter
    private Date endTime;

    @Getter(AccessLevel.PUBLIC) @Setter
    private int duration;
}
