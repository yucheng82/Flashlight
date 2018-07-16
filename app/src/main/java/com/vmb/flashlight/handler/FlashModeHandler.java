package com.vmb.flashlight.handler;

import android.hardware.Camera;
import android.os.Handler;

import com.vmb.flashlight.model.Flashlight;

public class FlashModeHandler {

    private static FlashModeHandler flashModeHandler;

    private boolean isOn;
    private int offsetTime;
    private Handler handler;

    public static FlashModeHandler getInstance() {
        if (flashModeHandler == null) {
            synchronized (FlashModeHandler.class) {
                flashModeHandler = new FlashModeHandler();
            }
        }
        return flashModeHandler;
    }

    public FlashModeHandler() {
        this.isOn = true;
        this.offsetTime = 0;
        this.handler = new Handler();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Flashlight.getInstance().isFlashLightOn()) {
                if (isOn) {
                    // Turn off flashlight
                    Flashlight.getInstance().toggle(Camera.Parameters.FLASH_MODE_OFF);
                    isOn = false;
                } else {
                    // Turn on flashlight
                    Flashlight.getInstance().toggle(Camera.Parameters.FLASH_MODE_TORCH);
                    Flashlight.getInstance().getCamera().startPreview();
                    isOn = true;
                }

                handler.postDelayed(this, offsetTime);
            }
        }
    };

    public void setMode(int mode) {
        handler.removeCallbacks(runnable);
        switch (mode) {
            case 0:
                if (Flashlight.getInstance().isFlashLightOn() == false) {
                    // Turn on flashlight
                    Flashlight.getInstance().toggle(Camera.Parameters.FLASH_MODE_TORCH);
                    Flashlight.getInstance().getCamera().startPreview();
                    isOn = true;
                }
                break;

            case 1:
                offsetTime = 900;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 2:
                offsetTime = 800;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 3:
                offsetTime = 700;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 4:
                offsetTime = 600;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 5:
                offsetTime = 500;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 6:
                offsetTime = 400;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 7:
                offsetTime = 300;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 8:
                offsetTime = 200;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 9:
                offsetTime = 100;
                handler.postDelayed(runnable, offsetTime);
                break;

            default:
                break;
        }
    }
}
