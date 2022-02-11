package com.lifeteam.farbeat.demo.page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lifeteam.farbeat.bridge.Adaptor;
import com.lifeteam.farbeat.bridge.base.BaseActivity;
import com.lifeteam.farbeat.bridge.engin.ScanDevice;
import com.lifeteam.farbeat.bridge.listener.device.OnConnectDeviceListener;
import com.lifeteam.farbeat.bridge.util.data.DataChange;
import com.lifeteam.farbeat.bridge.util.data.DataUtil;
import com.lifeteam.farbeat.bridge.util.data.StringUtils;
import com.lifeteam.farbeat.bridge.util.log.LogUtil;
import com.lifeteam.farbeat.bridge.util.ui.ToastUtil;
import com.lifeteam.farbeat.demo.R;
import com.lifeteam.farbeat.demo.adapter.DeviceAdapter;
import com.lifeteam.farbeat.demo.app.SDKDevice;
import com.lifeteam.farbeat.demo.util.Permission;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.helper.PermissionHelper;

public class DeviceActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private RecyclerView deviceRecycler;
    private DeviceAdapter deviceAdapter;
    private ScanDevice scanDevice;
    private final String DEVICE_NAME = "device_name";
    private final String DEVICE_MAC = "device_mac";
    private final int onStartConnect = 90001;
    private final int onConnected = 90002;
    private final int onDisconnected = 90003;
    private final int onConnectFailed = 90004;

    @Override
    public int getContentResId() {
        return R.layout.activity_device;
    }

    @Override
    public void init() {
        TextView topBarOther = findViewById(R.id.top_bar_other);
        topBarOther.setText("添加设备");
        topBarOther.setVisibility(View.VISIBLE);
        deviceRecycler = findViewById(R.id.device_recycler);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        deviceAdapter = new DeviceAdapter(mActivity, null);
        deviceRecycler.setAdapter(deviceAdapter);

        Adaptor.setOnConnectDeviceListener(onConnectDeviceListener);

        methodRequires2Permission();
    }

    @Override
    public void onClick(@NotNull View v) {
        int id = v.getId();
        if (id == R.id.top_bar_other) {
            Adaptor.getSourceDeviceConnected(mActivity, SDKDevice.devices());
        }
    }

    private final OnConnectDeviceListener onConnectDeviceListener = new OnConnectDeviceListener() {
        @Override
        public void onStartConnect(ScanDevice device) {
            scanDevice = device;
            handler.sendEmptyMessage(onStartConnect);
        }

        @Override
        public void onConnected(String name, String mac) {
            Message msg = new Message();
            msg.what = onConnected;
            Bundle bundle = new Bundle();
            bundle.putString(DEVICE_NAME, name);
            bundle.putString(DEVICE_MAC, mac);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onDisconnected(String name, String mac) {
            Message msg = new Message();
            msg.what = onDisconnected;
            Bundle bundle = new Bundle();
            bundle.putString(DEVICE_NAME, name);
            bundle.putString(DEVICE_MAC, mac);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onConnectFailed(String name, String mac) {
            handler.sendEmptyMessage(onConnectFailed);
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch(msg.what) {
                case onStartConnect:
                    if (scanDevice != null) {
                        ToastUtil.showProgressDialog(mActivity, "设备【" + scanDevice.getName() + "】开始连接...");
                    }
                    break;
                case onConnected:
                    String connectedName = msg.getData().getString(DEVICE_NAME);
                    String connectedMac = msg.getData().getString(DEVICE_MAC);
                    if (scanDevice != null) {
                        String deviceMac = scanDevice.getMac();
                        if (!DataUtil.isEmpty(deviceMac) && deviceMac.equals(connectedMac)) {
                            boolean isAdd = true;
                            List<ScanDevice> data = deviceAdapter.getData();
                            for (ScanDevice device : data){
                                if (connectedMac.equals(device.getMac())) {
                                    isAdd = false;
                                }
                            }
                            if (isAdd) {
                                deviceAdapter.addItem(scanDevice);
                                deviceRecycler.setVisibility(View.VISIBLE);
                                ToastUtil.show(mActivity, "设备【" + connectedName + "】已连接！");
                            }
                        }
                    }
                    scanDevice = null;
                    ToastUtil.dismissProgressDialog();
                    break;
                case onDisconnected:
                    String disconnectedName = msg.getData().getString(DEVICE_NAME);
                    String disconnectedMac = msg.getData().getString(DEVICE_MAC);
                    List<ScanDevice> data = deviceAdapter.getData();
                    for (ScanDevice device : data){
                        if (disconnectedMac.equals(device.getMac())) {
                            deviceAdapter.removeItem(device);
                            ToastUtil.show(mActivity, "设备【" + disconnectedName + "】已断开！");
                        }
                    }
                    if (deviceAdapter.getData().size() == 0) {
                        deviceRecycler.setVisibility(View.GONE);
                    }
                    ToastUtil.dismissProgressDialog();
                    break;
                case onConnectFailed:
                    ToastUtil.dismissProgressDialog();
                    break;
            }
        }
    };

    @SuppressLint("RestrictedApi")
    @AfterPermissionGranted(Permission.R30)
    private void methodRequires2Permission() {
        if (!EasyPermissions.hasPermissions(mActivity, Permission.PERMS)) {
            PermissionHelper.newInstance(mActivity).directRequestPermissions(Permission.R30, Permission.PERMS);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        } else return keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN;
    }
}