package com.dev.szpeter.onairserver;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dev.szpeter.onairserver.data.DeviceFlashStatus;
import com.dev.szpeter.onairserver.server.RESTServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private PowerManager pm;
    private PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RESTServer server = new RESTServer(9090, getAssets());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "on.air:service.wakeup");
        wl.acquire();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DeviceFlashStatus.getInstance().setCameraManager((CameraManager) getSystemService(Context.CAMERA_SERVICE));
        }
        registerService(9090);

    }

    @Override
    protected void onDestroy() {
        wl.release();
        super.onDestroy();
    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName("ONAir");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(port);

        NsdManager nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "onRegistrationFailed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "onUnregistrationFailed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "onServiceRegistered");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "onServiceUnregistered");
            }
        });
    }
}
