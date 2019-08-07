package com.dev.szpeter.onairserver.server;

import android.util.Log;

import com.dev.szpeter.onairserver.ObjectBox;
import com.dev.szpeter.onairserver.data.TimeEntry;
import com.dev.szpeter.onairserver.data.User;
import com.dev.szpeter.onairserver.data.User_;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import io.objectbox.Box;

public class TimeSlotHandler extends RouterNanoHTTPD.GeneralHandler {

    private static final String TAG = TimeSlotHandler.class.getName();
    private Box<User> userBox = ObjectBox.get().boxFor(User.class);

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams,
                                  NanoHTTPD.IHTTPSession session) {
        Log.e(TAG, "The get function has been called with: " + urlParams.toString());

        JsonArray table = new JsonArray();

        Date today = new Date();
        today.setHours(0);
        today.setMinutes(1);

        for (User user: userBox.getAll()) {
            List<TimeEntry> removeUsers = new ArrayList<>();
            for (TimeEntry entry: user.getTimeEntryList()) {

                if(entry.getEndTime().before(today)) {
                    removeUsers.add(entry);

                } else {
                    JsonArray row = new JsonArray();
                    row.add(user.getUserName());
                    row.add(entry.getDisplayText());

                    row.add(entry.getStartTime().getTime());
                    row.add(entry.getEndTime().getTime());
                    table.add(row);
                }
            }
            user.getTimeEntryList().removeAll(removeUsers);
            userBox.put(user);
        }

        Log.e(TAG, "The table to be sent: " + table.size() + table.toString());
        if(table.size()!=0){
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), table.toString());
        }else {
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "{\"error\":\"No time entries exist.!\"}");
        }

    }

    @Override
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams,
                                   NanoHTTPD.IHTTPSession session) {

        String email = session.getHeaders().get("email");
        String duration = session.getHeaders().get("duration");

        List<User> users = userBox.query().equal(User_.email, email).build().find();

        if(!users.isEmpty()) {
            User currentUser = users.get(0);
            //if(currentUser.hasToken() && currentUser.getToken().equals(token)) {
            Integer durationInteger = Integer.valueOf(duration);
            Log.e(TAG, "Duration: " + duration + " current user: " + currentUser.getUserName());
            if(durationInteger > 0) {
                currentUser.addNewTimeSlot(durationInteger);
                userBox.put(currentUser);
                //TODO: handle flash start stop
            } else if(durationInteger == -1){
                currentUser.removeLastEntry();
                userBox.put(currentUser);
            } else if(durationInteger == -20){
                currentUser.removeAllEntries();
                userBox.put(currentUser);
            }
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "{\"message\":\"Time slot saved\"}");
            //}
        } else {
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "{\"error\":\"User does not exist!\"}");
        }


    }
}

