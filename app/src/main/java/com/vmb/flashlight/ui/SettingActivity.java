package com.vmb.flashlight.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.rey.material.widget.Switch;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.base.BaseActivity;
import com.vmb.flashlight.model.Flashlight;
import com.vmb.flashlight.util.ShareUtils;
import com.vmb.flashlight.util.SharedPreferencesUtil;
import com.vmb.touchclick.listener.OnTouchClickListener;

import flashlight.supper.flashlight.R;

public class SettingActivity extends BaseActivity {

    private CallbackManager callbackManager;

    private ImageView img_back;
    private ImageView img_about;
    private ImageView img_share;
    private ImageView img_rate;
    private Switch sw_sound;

    protected int getResLayout() {
        return R.layout.activity_setting;
    }

    protected void initView() {
        img_back = findViewById(R.id.img_back);
        img_about = findViewById(R.id.img_about);
        img_share = findViewById(R.id.img_share);
        img_rate = findViewById(R.id.img_rate);
        sw_sound = findViewById(R.id.sw_sound);
    }

    protected void initData() {
        callbackManager = CallbackManager.Factory.create();

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

        img_share.setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareFB(SettingActivity.this, callbackManager);
            }
        }, getApplicationContext(), 1));

        img_rate.setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.rateApp(SettingActivity.this);
            }
        }, getApplicationContext(), 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.RequestCode.SHARE_FB) {
            if (callbackManager != null) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
