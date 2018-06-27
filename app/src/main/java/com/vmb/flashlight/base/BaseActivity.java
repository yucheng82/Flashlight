package com.vmb.flashlight.base;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.vmb.flashlight.Config;
import com.vmb.flashlight.util.PermissionUtils;

public abstract class BaseActivity extends AppCompatActivity {

    private String[] INTERNET = {Manifest.permission.INTERNET};
    private String[] NETWORK = {Manifest.permission.ACCESS_NETWORK_STATE};
    private String[] CAMERA = {Manifest.permission.CAMERA};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayout());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(INTERNET, Config.RequestCode.CODE_REQUEST_PERMISSION_INTERNET);
            requestPermissions(NETWORK, Config.RequestCode.CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE);
            requestPermissions(CAMERA, Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA);
        }

        initView();
        initData();
    }

    protected abstract int getResLayout();

    protected abstract void initView();

    protected abstract void initData();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Config.RequestCode.CODE_REQUEST_PERMISSION_INTERNET:
                if (!PermissionUtils.permissionGranted(requestCode, Config.RequestCode.CODE_REQUEST_PERMISSION_INTERNET, grantResults)) {
                    PermissionUtils.requestPermission(this, Config.RequestCode.CODE_REQUEST_PERMISSION_INTERNET, Manifest.permission.INTERNET);
                }
                break;

            case Config.RequestCode.CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE:
                if (!PermissionUtils.permissionGranted(requestCode, Config.RequestCode.CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE, grantResults)) {
                    PermissionUtils.requestPermission(this, Config.RequestCode.CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_NETWORK_STATE);
                }
                break;

            case Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA:
                if (!PermissionUtils.permissionGranted(requestCode, Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA, grantResults)) {
                    PermissionUtils.requestPermission(this, Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA, Manifest.permission.CAMERA);
                }
                break;
        }
    }
}
