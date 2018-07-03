package com.vmb.flashlight.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.vmb.flashlight.Config;
import com.vmb.flashlight.R;
import com.vmb.flashlight.util.PermissionUtils;
import com.vmb.flashlight.util.ToastUtil;

public abstract class BaseActivity extends AppCompatActivity {

    /*private String[] INTERNET = {Manifest.permission.INTERNET};
    private String[] NETWORK = {Manifest.permission.ACCESS_NETWORK_STATE};
    private String[] CAMERA = {Manifest.permission.CAMERA};*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayout());
        //requestPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(this, Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA, Manifest.permission.CAMERA);
            } else {
                initView();
                initData();
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                PermissionUtils.requestPermission(this, Config.RequestCode.CODE_REQUEST_PERMISSION_INTERNET, Manifest.permission.INTERNET);

        } else {
            initView();
            initData();
        }
    }

    protected abstract int getResLayout();

    protected abstract void initView();

    protected abstract void initData();

    /*public void requestPermission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, Config.RequestCode.CODE_REQUEST_PERMISSION_INTERNET);
        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, Config.RequestCode.CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE);
        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Config.RequestCode.CODE_REQUEST_PERMISSION_CAMERA);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Config.RequestCode.CODE_REQUEST_PERMISSION_INTERNET:
                if (!PermissionUtils.permissionGranted(requestCode, Config.RequestCode.CODE_REQUEST_PERMISSION_INTERNET, grantResults)) {

                }
                break;

            case Config.RequestCode.CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE:
                if (!PermissionUtils.permissionGranted(requestCode, Config.RequestCode.CODE_REQUEST_PERMISSION_ACCESS_NETWORK_STATE, grantResults)) {

                }
                break;

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
