package com.vmb.flashlight.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.rey.material.widget.Switch;
import com.vmb.flashlight.model.Flashlight;
import com.vmb.flashlight.util.SharedPreferencesUtil;
import com.vmb.touchclick.listener.OnTouchClickListener;

import flashlight.supper.flashlight.R;

public class SettingActivity extends Activity {

    ImageView img_back;
    ImageView img_about;
    Switch sw_sound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayout());

        initView();
        initData();
    }

    protected int getResLayout() {
        return R.layout.activity_setting;
    }

    protected void initView() {
        img_back = findViewById(R.id.img_back);
        img_about = findViewById(R.id.img_about);
        sw_sound = findViewById(R.id.sw_sound);
    }

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
