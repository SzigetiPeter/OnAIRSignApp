package com.dev.szpeter.onairserver.server;

import android.graphics.Color;
import android.util.Log;

import com.dev.szpeter.onairserver.ObjectBox;
import com.dev.szpeter.onairserver.data.User;
import com.dev.szpeter.onairserver.data.User_;

import java.util.List;
import java.util.Map;
import java.util.Random;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import io.objectbox.Box;

public class UserRegistrationHandler extends RouterNanoHTTPD.GeneralHandler{

    private static final String TAG = UserRegistrationHandler.class.getName();
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
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams,
                                   NanoHTTPD.IHTTPSession session) {

        String username = session.getHeaders().get("username");
        String password = session.getHeaders().get("password");
        String email = session.getHeaders().get("email");

        List<User> usersMail = userBox.query().equal(User_.email, email).build().find();
        List<User> usersUserName = userBox.query().equal(User_.userName, username).build().find();

        if(!usersMail.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(),
                    "{\"user-id\":\"" + usersMail.get(0).id + "\"," +
                    "\"user-mail\":\"" + usersUserName.get(0).getEmail() +
                    "\" , \"status\":\"user already exists\"}");
        } else if(!usersUserName.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(),
                    "{\"user-id\":\"" + usersUserName.get(0).id + "\"," +
                    "\"user-name\":\"" + usersUserName.get(0).getUserName() +
                    "\" , \"status\":\"user already exists\"}");
        } else {
            Random rnd = new Random();
            int textColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            User newUser = new User(username, password, email, textColor);
            userBox.put(newUser);
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "{\"user-id\":\"" + newUser.id + "\"}");
        }


    }
}
