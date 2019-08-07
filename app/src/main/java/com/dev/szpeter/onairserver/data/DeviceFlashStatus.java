package com.dev.szpeter.onairserver.data;

import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

public class DeviceFlashStatus {
    private static final String TAG = DeviceFlashStatus.class.getName();
    /**
     * status : on / off
     */

    public enum FlashStatus {
        ON, OFF
    }

    private static Camera camera = null;

    private CameraManager cameraManager = null;


    private FlashStatus status = FlashStatus.OFF;

    private static DeviceFlashStatus instance;

    public static DeviceFlashStatus getInstance() {
        if (instance == null) {
            instance = new DeviceFlashStatus();
        }
        return instance;
    }

    public FlashStatus getStatus() {
        return status;
    }

    public void setStatus(FlashStatus status) {
        this.status = status;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public boolean hasFlash() {
        if (camera == null) {
            return false;
        }

        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        return true;
    }

    public void flashLightOn() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                camera = Camera.open();
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
                status = FlashStatus.ON;
            } catch (Exception e) {
                Log.e(TAG, "Flashlight start failed: " + e);
            }
        }
    }


    public void flashLightOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                 cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (camera == null) {
                    return;
                }
                camera.stopPreview();
                camera.release();
                camera = null;
                status = FlashStatus.OFF;
            } catch (Exception e) {
                Log.e(TAG, "Flashlight stop failed: " + e);
            }
        }
    }
}
