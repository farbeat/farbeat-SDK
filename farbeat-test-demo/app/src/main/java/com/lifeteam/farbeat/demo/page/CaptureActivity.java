package com.lifeteam.farbeat.demo.page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;

import com.google.zxing.Result;
import com.lifeteam.farbeat.bridge.dialog.AskDialog;
import com.lifeteam.farbeat.bridge.listener.common.OnCancelListener;
import com.lifeteam.farbeat.bridge.listener.common.OnNegativeListener;
import com.lifeteam.farbeat.bridge.listener.common.OnPositiveListener;
import com.lifeteam.farbeat.bridge.util.ui.ToastUtil;
import com.lifeteam.farbeat.demo.R;
import com.lifeteam.farbeat.demo.util.Permission;
import com.lifeteam.farbeat.demo.zxing.CameraScan;
import com.lifeteam.farbeat.demo.zxing.DefaultCameraScan;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.helper.PermissionHelper;

public class CaptureActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private CameraScan cameraScan;
    private AppCompatActivity mActivity;
    public static final String CONTENT = "content";
    public static final int APP_ID = 110;
    public static final int APP_SECRET = 111;

    public static void startForResult(AppCompatActivity activity, int requestCode) {
        Intent intent = new Intent(activity, CaptureActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        mActivity = this;

        PreviewView capturePreview = findViewById(R.id.capture_preview);
        cameraScan = new DefaultCameraScan(mActivity, capturePreview);
        cameraScan.setPlayBeep(true);
        cameraScan.setOnScanResultCallback(new CameraScan.OnScanResultCallback() {
            @Override
            public boolean onScanResultCallback(Result result) {
                cameraScan.setAnalyzeImage(false);
                if (result != null) {
                    String text = result.getText();
                    if (!TextUtils.isEmpty(text)) {
                        Intent intent = new Intent();
                        if (!TextUtils.isEmpty(text)) {
                            intent.putExtra(CONTENT, text);
                        }
                        setResult(RESULT_OK, intent);
                    } else {
                        ToastUtil.show(mActivity, "??????????????????????????????");
                    }
                } else {
                    ToastUtil.show(mActivity, "??????????????????????????????");
                }
                finish();
                return true;
            }
        });

        methodRequires2Permission();
    }

    private void initCapture() {
        cameraScan.setAnalyzeImage(true);
    }

    @AfterPermissionGranted(Permission.R30)
    public void methodRequires2Permission() {
        if (EasyPermissions.hasPermissions(mActivity, Permission.PERMS)) {
            cameraScan.startCamera();
        } else {
            AskDialog askDialog = new AskDialog(mActivity, "????????????????????????????????????", "??????", "?????????");
            askDialog.setOnPositiveListener(new OnPositiveListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onPositive() {
                    PermissionHelper.newInstance(mActivity).directRequestPermissions(Permission.R30, Permission.PERMS);
                }
            });
            askDialog.setOnNegativeListener(new OnNegativeListener() {
                @Override
                public void onNegative() {
                    finish();
                }
            });
            askDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel() {
                    finish();
                }
            });
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NotNull List<String> perms) {
        ToastUtil.show(mActivity, "??????????????????????????????");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NotNull List<String> perms) {
        if (requestCode == Permission.R30 && perms.size() > 0) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        if (cameraScan != null) {
            cameraScan.stopCamera();
            cameraScan.release();
            cameraScan = null;
        }
        super.onDestroy();
    }
}