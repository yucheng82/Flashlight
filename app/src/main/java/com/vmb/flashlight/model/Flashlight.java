package com.vmb.flashlight.model;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;

import flashlight.supper.flashlight.R;

public class Flashlight {

    private static Flashlight flashlight;

    private Camera camera;
    private Camera.Parameters parameters;
    private boolean isFlashLightOn = false;
    private boolean sound = true;

    public static Flashlight getInstance() {
        if (flashlight == null) {
            synchronized (Flashlight.class) {
                flashlight = new Flashlight();
            }
        }
        return flashlight;
    }

    public void setInstance(Flashlight flashlight) {
        this.flashlight = flashlight;
    }

    public void toggle(String value) {
        if (this.parameters == null || this.camera == null)
            return;

        try {
            this.parameters.setFlashMode(value);
            this.camera.setParameters(this.parameters);
        } catch (Exception e) {
            return;
        }
    }

    public void playToggleSound(Context context) {
        if (context == null || this.sound == false)
            return;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.sound_toggle);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });
        mp.start();
    }

    public void playMoveSound(Context context) {
        if (context == null || this.sound == false)
            return;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.adjustment_move);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });
        mp.start();
    }

    public void playEndSound(Context context) {
        if (context == null || this.sound == false)
            return;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.adjustment_end);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
            }
        });
        mp.start();
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

    public boolean isSound() {
        return sound;
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

    public void setSound(boolean sound) {
        this.sound = sound;
    }
}
