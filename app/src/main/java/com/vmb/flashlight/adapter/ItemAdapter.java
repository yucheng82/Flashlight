package com.vmb.flashlight.adapter;

import android.content.Context;

import com.vmb.flashlight.R;
import com.vmb.flashlight.base.BaseAdapter;

import java.util.List;

public class ItemAdapter extends BaseAdapter {

    public ItemAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    protected int getResLayout() {
        return R.layout.row_option;
    }
}
