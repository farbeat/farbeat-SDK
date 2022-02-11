package com.lifeteam.farbeat.demo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lifeteam.farbeat.bridge.engin.HealthDataSourceConfigList;
import com.lifeteam.farbeat.bridge.engin.ScanDevice;
import com.lifeteam.farbeat.bridge.util.data.DataUtil;
import com.lifeteam.farbeat.bridge.widget.recycler.OnClickListener;
import com.lifeteam.farbeat.bridge.widget.recycler.RecyclerAdapter;
import com.lifeteam.farbeat.bridge.widget.recycler.RecyclerHolder;
import com.lifeteam.farbeat.demo.R;
import com.lifeteam.farbeat.demo.page.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeviceAdapter extends RecyclerAdapter<ScanDevice> implements OnClickListener {

    public DeviceAdapter(Context context, List<ScanDevice> data) {
        super(context, data, R.layout.item_device);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void convert(@NotNull RecyclerHolder holder, @NotNull ScanDevice item) {
        TextView deviceName = holder.findViewById(R.id.device_name);
        TextView deviceMac = holder.findViewById(R.id.device_mac);
        String name = item.getName();
        if (!DataUtil.isEmpty(name)) {
            deviceName.setText(name);
        }
        String mac = item.getMac();
        if (!DataUtil.isEmpty(mac)) {
            deviceMac.setText("MAC：" + mac);
        }
        holder.setOnClickListener(this, R.id.device_root);
    }

    @Override
    public void onClick(View v, int position, RecyclerHolder holder) {
        ScanDevice item = getItem(position);
        if (item != null) {
            MainActivity.start(mContext, item.getSdkDevice());
        }
    }
}