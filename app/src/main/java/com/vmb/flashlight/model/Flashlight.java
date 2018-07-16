package com.vmb.flashlight.model;

import android.hardware.Camera;

public class Flashlight {

    private static Flashlight flashlight;

    private Camera camera;
    private Camera.Parameters parameters;
    private boolean isFlashLightOn;

    public static Flashlight getInstance() {
        if (flashlight == null) {
            synchronized (Flashlight.class) {
                flashlight = new Flashlight(false);
            }
        }
        return flashlight;
    }

    public Flashlight(boolean isFlashLightOn) {
        this.isFlashLightOn = isFlashLightOn;
    }

    public void toggle(String value) {
        this.parameters.setFlashMode(value);
        this.camera.setParameters(this.parameters);
    }

    // Get
    public Camera getCamera() {
        return camera;
    }

    public Camera.Parameters getParameters() {
        return parameters;
    }

    public boolean isFlashLightOn() {
        return isFlashLightOn;
    }

    // Set
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setParameters(Camera.Parameters parameters) {
        this.parameters = parameters;
    }

    public void setFlashLightOn(boolean flashLightOn) {
        isFlashLightOn = flashLightOn;
    }
}
