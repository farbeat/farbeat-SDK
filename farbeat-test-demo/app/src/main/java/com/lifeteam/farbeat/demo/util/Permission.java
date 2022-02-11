package com.lifeteam.farbeat.demo.util;

import android.Manifest;

public class Permission {

    public static final int R30 = 30;

    public static final String[] PERMS = {
            Manifest.permission.READ_PHONE_STATE,

            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
}