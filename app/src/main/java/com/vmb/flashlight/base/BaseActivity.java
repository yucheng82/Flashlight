package com.vmb.flashlight.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResLayout());
        initView();
        initData();
    }

    protected abstract int getResLayout();

    protected abstract void initView();

    protected abstract void initData();
}
