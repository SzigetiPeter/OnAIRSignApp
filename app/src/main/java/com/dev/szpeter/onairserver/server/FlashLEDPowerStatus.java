package com.dev.szpeter.onairserver.server;

import android.util.Log;

import com.dev.szpeter.onairserver.data.DeviceFlashStatus;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

public class FlashLEDPowerStatus extends RouterNanoHTTPD.GeneralHandler {

    private static final String TAG = FlashLEDPowerStatus.class.getName();

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
        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(),
                DeviceFlashStatus.getInstance().getStatus().name());
    }

    @Override
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams,
                                   NanoHTTPD.IHTTPSession session) {
       if(session.getParameters().get("flash").get(0).equals("on")) {
           DeviceFlashStatus.getInstance().flashLightOn();
        } else {
           DeviceFlashStatus.getInstance().flashLightOff();
        }

        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "{}");
    }

}
