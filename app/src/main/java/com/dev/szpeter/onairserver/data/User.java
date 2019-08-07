package com.dev.szpeter.onairserver.data;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
public class User {

    @Id
    public long id;

    @Getter(AccessLevel.PUBLIC) @Setter
    private String email;

    @Getter(AccessLevel.PUBLIC) @Setter
    private String userName;

    @Getter @Setter
    private String password;

    @Getter @Setter
    private Integer textColor;

    @Getter(AccessLevel.PUBLIC) @Setter
    private ToMany<TimeEntry> timeEntryList;

    @Getter
    private String token;

    public User() {
    }

    public User(String userName, String password, String email, Integer textColor) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.textColor = textColor;
    }

    public boolean checkPassword(String password){
        return this.password.equals(password);
    }

    public void setToken(String token) {
        this.token = token;
        //TODO: handle expiration of token
    }

    public void addNewTimeSlot(int duration) {
        timeEntryList.add(new TimeEntry(new Date(), new Date(new Date().getTime() + duration), duration));
    }

    public String getToken() {
        return token;
    }

    public void removeAllEntries() {
        timeEntryList.clear();
    }

    public void removeLastEntry() {
        timeEntryList.remove(timeEntryList.size() - 1);
    }
}
