package com.vmb.flashlight.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {

    public static void shortToast(Context context, String text) {
        if(context == null)
            return;

        Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String text) {
        if(context == null)
            return;

        Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void shortSnackbar(Activity activity, String text) {
        if(activity == null)
            return;

        Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).show();
    }

    public static void longSnackbar(Activity activity, String text) {
        if(activity == null)
            return;

        Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
    }

    public static void customShortSnackbar(Activity activity, String text, String click) {
        if(activity == null)
            return;

        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT).setAction(click, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View view = snackbar.getView();
        view.setBackgroundColor(Color.BLACK);
        ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) view.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);
    }

    public static void customLongSnackbar(Activity activity, String text, String click) {
        if(activity == null)
            return;

        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).setAction(click, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View view = snackbar.getView();
        view.setBackgroundColor(Color.BLACK);
        ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) view.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.GREEN);
    }
}
