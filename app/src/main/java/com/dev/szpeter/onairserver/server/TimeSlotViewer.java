package com.dev.szpeter.onairserver.server;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

public class TimeSlotViewer extends RouterNanoHTTPD.DefaultHandler {

    private static final String TAG = TimeSlotViewer.class.getName();

    @Override
    public String getText() {
        throw new IllegalStateException("this method should not be called");
    }

    @Override
    public String getMimeType() {
        throw new IllegalStateException("this method should not be called");
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        String baseUri = uriResource.getUri();
        String realUri = RouterNanoHTTPD.normalizeUri(session.getUri());
        for (int index = 0; index < Math.min(baseUri.length(), realUri.length()); index++) {
            if (baseUri.charAt(index) != realUri.charAt(index)) {
                realUri = RouterNanoHTTPD.normalizeUri(realUri.substring(index));
                break;
            }
        }
        AssetManager assetManager = uriResource.initParameter(AssetManager.class);
        InputStream inputStream;
        try {
            if(baseUri.contains("index.html")) {
                inputStream = assetManager.open("index.html");
            } else if (baseUri.contains("loader.js")){
                inputStream = assetManager.open("js/loader.js");
            } else {
                inputStream = assetManager.open("js/jquery-3.4.1.min.js");
            }
        } catch (IOException e) {
            Log.e(TAG, "Index retrieval error " + e);
            return new RouterNanoHTTPD.Error404UriHandler().get(uriResource, urlParams, session);
        }

        NanoHTTPD.Response response = NanoHTTPD.newChunkedResponse(getStatus(), NanoHTTPD.getMimeTypeForFile(realUri),
                new BufferedInputStream(inputStream));
        return response;


    }
}