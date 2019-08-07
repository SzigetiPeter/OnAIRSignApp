package com.dev.szpeter.onairserver;

import android.app.Application;

public class ApplicationDBInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
    }
}
