package com.lifeteam.farbeat.demo.app;

import com.lifeteam.farbeat.bridge.util.constant.SdkDevice;

import java.util.ArrayList;
import java.util.List;

public class SDKDevice {

    public static List<Integer> devices() {
        List<Integer> devices = new ArrayList<>();
        devices.add(SdkDevice.GT2);
        devices.add(SdkDevice.TKH01);
        return devices;
    }
}