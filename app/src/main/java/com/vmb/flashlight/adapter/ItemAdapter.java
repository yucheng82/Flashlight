package com.vmb.flashlight.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.vmb.flashlight.R;
import com.vmb.flashlight.adapter.holder.ItemHolder;
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        bindData((ItemHolder) viewHolder, position);
    }

    public void bindData(ItemHolder holder, int position) {

    }
}
