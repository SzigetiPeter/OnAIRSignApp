package com.dev.szpeter.onairserver.server;

import android.content.res.AssetManager;

import fi.iki.elonen.router.RouterNanoHTTPD;

public class RESTServer extends RouterNanoHTTPD {

    private static final int PORT = 9090;
    private AssetManager assetManager;

    public RESTServer(int port, AssetManager assets) {
        super(port);
        assetManager = assets;
        addMappings();
    }

    public void addMappings() {
        setNotImplementedHandler(NotImplementedHandler.class);
        setNotFoundHandler(Error404UriHandler.class);
        addRoute("/js/loader.js", TimeSlotViewer.class, assetManager);
        addRoute("/js/jquery-3.4.1.min.js", TimeSlotViewer.class, assetManager);
        addRoute("/", TimeSlotViewer.class, assetManager);
        addRoute("", TimeSlotViewer.class, assetManager);
        addRoute("/index.html",  TimeSlotViewer.class, assetManager);
        addRoute("/device/flash", FlashLEDPowerStatus.class);
        addRoute("/user/register", UserRegistrationHandler.class);
        addRoute("/user/login", UserLoginHandler.class);
        addRoute("/timeslot", TimeSlotHandler.class);
    }
}
