package com.vmb.flashlight.ui;

import android.view.View;
import android.widget.ImageView;

import com.rey.material.widget.Switch;
import com.vmb.flashlight.base.BaseActivity;
import com.vmb.flashlight.model.Flashlight;
import com.vmb.flashlight.util.SharedPreferencesUtil;
import com.vmb.touchclick.listener.OnTouchClickListener;

import flashlight.supper.flashlight.R;

public class SettingActivity extends BaseActivity {

    ImageView img_back;
    ImageView img_about;
    Switch sw_sound;

    @Override
    protected int getResLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        img_back = findViewById(R.id.img_back);
        img_about = findViewById(R.id.img_about);
        sw_sound = findViewById(R.id.sw_sound);
    }

    @Override
    protected void initData() {
        img_back.setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getApplicationContext(), 1));

        img_about.setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, getApplicationContext(), 1));

        sw_sound.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                Flashlight.getInstance().setSound(checked);
                SharedPreferencesUtil.putPrefferBool(getApplicationContext(), "sound", checked);
            }
        });

        if (Flashlight.getInstance().isSound())
            sw_sound.setChecked(true);
        else
            sw_sound.setChecked(false);
    }
}
