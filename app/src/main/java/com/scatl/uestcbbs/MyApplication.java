package com.scatl.uestcbbs;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.scatl.uestcbbs.utils.SharePrefUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (SharePrefUtil.isNightMode(getApplicationContext())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //todo
        //CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_ID, true);

    }

}
