package com.vmb.flashlight.base;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.vmb.flashlight.Config;
import com.vmb.flashlight.util.AdmobUtil;
import com.vmb.flashlight.util.NetworkUtil;
import com.vmb.flashlight.util.PermissionUtils;
import com.vmb.flashlight.util.ToastUtil;

import flashlight.supper.flashlight.R;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayout());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(this, Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA, Manifest.permission.CAMERA);
            } else {
                initView();
                initData();
            }

        } else {
            initView();
            initData();
        }
    }

    protected abstract int getResLayout();

    protected abstract void initView();

    protected abstract void initData();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA:
                if (!PermissionUtils.permissionGranted(requestCode, Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA, grantResults)) {
                    ToastUtil.longToast(getApplicationContext(), getString(R.string.warning_request_permission));
                } else {
                    initView();
                    initData();
                }
                break;
        }
    }
}
