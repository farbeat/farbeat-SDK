package com.lifeteam.farbeat.demo.app;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.lifeteam.farbeat.FarBeat;
import com.lifeteam.farbeat.bridge.FarBeatManager;

public class NingApp extends Application {

    protected static NingApp mContext;
    public static String appId = "gw_a2ec4fd51793543d";
    public static String appSecret = "3987a05373c14336a2940c73bbb13783";
    public static long sdkSn = 79165821L;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        // 初始化通用SDK
        FarBeatManager fbMgr = FarBeatManager.getInstance();
        fbMgr.setDebugIsEnable(true);
        fbMgr.init(mContext, SDKDevice.devices());
        fbMgr.setGSdkSn(sdkSn);

        // 初始化计算SDK
        FarBeat.getInstance().init(mContext);
    }

    public static NingApp getAppContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(mContext);
    }
}