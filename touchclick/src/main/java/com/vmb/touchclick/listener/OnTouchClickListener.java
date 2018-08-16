package com.vmb.touchclick.listener;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.vmb.touchclick.R;


/**
 * Created by Manh Dang on 03/05/2018.
 */

public class OnTouchClickListener implements View.OnTouchListener {
    /**
     * min movement to detect as a click action.
     */
    private int minMove = 20;
    private float startX;
    private float startY;
    private OnClickListener mListener;
    private Context context;
    private int type;

    public OnTouchClickListener(OnClickListener mListener, Context context, int type) {
        this.mListener = mListener;
        this.context = context;
        this.type = type;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > minMove || differenceY > minMove) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.context == null)
            return false;

        switch (type) {
            case 1:
                Animation zoom_in = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
                Animation zoom_out = AnimationUtils.loadAnimation(context, R.anim.zoom_out);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        v.startAnimation(zoom_in);
                        return true;

                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float endY = event.getY();
                        v.startAnimation(zoom_out);
                        if (isAClick(startX, endX, startY, endY))
                            mListener.onClick(v);
                        break;
                }
                break;

            case 2:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        if(v instanceof ImageView)
                            ((ImageView) v).setImageResource(R.drawable.img_setting_touch);
                        return true;

                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float endY = event.getY();
                        if(v instanceof ImageView)
                            ((ImageView) v).setImageResource(R.drawable.img_setting_not_touch);
                        if (isAClick(startX, endX, startY, endY))
                            mListener.onClick(v);
                        break;
                }
                break;

            default:
                break;
        }
        return false;
    }

    public interface OnClickListener {
        void onClick(View v);
    }
}
