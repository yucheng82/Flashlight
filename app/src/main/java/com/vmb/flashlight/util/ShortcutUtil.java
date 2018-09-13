package com.vmb.flashlight.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class ShortcutUtil {

    public static void addShortcut(final Context context, String name, Bitmap bitmap, String url) {
        if (context == null || bitmap == null) {
            Log.i("ShortcutUtil", "null");
            return;
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(url));

        final Intent extra = new Intent();
        extra.putExtra("android.intent.extra.shortcut.INTENT", intent);
        extra.putExtra("android.intent.extra.shortcut.NAME", name);
        extra.putExtra("android.intent.extra.shortcut.ICON", bitmap);
        extra.putExtra("duplicate", false);
        extra.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(extra);
    }
}
