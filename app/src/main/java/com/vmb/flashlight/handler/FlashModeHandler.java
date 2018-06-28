package com.vmb.flashlight.handler;

import android.hardware.Camera;
import android.os.Handler;

import com.vmb.flashlight.Static;

public class FlashModeHandler {

    private static boolean isOn = true;
    private static int offsetTime = 0;
    private static Handler handler = new Handler();

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Static.isFlashLightOn) {
                if (isOn) {
                    // Turn off flashlight
                    Static.parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    Static.camera.setParameters(Static.parameters);
                    isOn = false;
                } else {
                    // Turn on flashlight
                    Static.parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    Static.camera.setParameters(Static.parameters);
                    Static.camera.startPreview();
                    isOn = true;
                }

                handler.postDelayed(this, offsetTime);
            }
        }
    };

    public static void setMode(int mode) {
        handler.removeCallbacks(runnable);
        switch (mode) {
            case 0:
                if (Static.isFlashLightOn) {
                    // Turn on flashlight
                    Static.parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    Static.camera.setParameters(Static.parameters);
                    Static.camera.startPreview();
                    isOn = true;
                }
                break;

            case 1:
                offsetTime = 1000;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 2:
                offsetTime = 900;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 3:
                offsetTime = 800;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 4:
                offsetTime = 700;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 5:
                offsetTime = 600;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 6:
                offsetTime = 500;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 7:
                offsetTime = 400;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 8:
                offsetTime = 300;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 9:
                offsetTime = 200;
                handler.postDelayed(runnable, offsetTime);
                break;

            default:
                break;
        }
    }
}
