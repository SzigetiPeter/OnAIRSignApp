package com.dev.szpeter.onairserver.server;

import android.util.Log;

import com.dev.szpeter.onairserver.ObjectBox;
import com.dev.szpeter.onairserver.data.User;
import com.dev.szpeter.onairserver.data.User_;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import io.objectbox.Box;

public class UserLoginHandler extends RouterNanoHTTPD.GeneralHandler {

    private static final String TAG = UserLoginHandler.class.getName();
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

        String email = session.getHeaders().get("email");
        String password = session.getHeaders().get("password");

        List<User> users = userBox.query().equal(User_.email, email).build().find();
        if(!users.isEmpty()) {
            User currentUser = users.get(0);
            if(currentUser.checkPassword(password)){
                NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(),
                        "{\"user-id\":\"" + currentUser.id + "\"}");
                String token = UUID.randomUUID().toString();
                currentUser.setToken(token);
                response.addHeader("Auth-Token", token);
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "{\"user-id\":\"" + currentUser.id + "\"}");
            } else {
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "{\"error\":\"Wrong password!\"");
            }
        } else {
            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "{\"error\":\"User does not exist!\"");
        }


    }
}
