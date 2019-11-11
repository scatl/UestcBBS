package com.scatl.uestcbbs.utils;

import android.annotation.SuppressLint;

import androidx.fragment.app.FragmentActivity;

import com.scatl.uestcbbs.interfaces.OnPermission;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class PermissionUtil {
    @SuppressLint("CheckResult")
    public static void requestPermission(final FragmentActivity fragmentActivity, final OnPermission onPermission, final String... permissions) {
        new RxPermissions(fragmentActivity)
                .requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {
                            onPermission.onGranted();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            onPermission.onRefused();
                        } else {//选中不再询问
                            onPermission.onRefusedWithNoMoreRequest();
                        }
                    }
                });
    }

}
