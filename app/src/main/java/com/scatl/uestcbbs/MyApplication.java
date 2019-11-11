package com.scatl.uestcbbs;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.tencent.bugly.crashreport.CrashReport;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (SharePrefUtil.isNightMode(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_ID, true);

    }

}
