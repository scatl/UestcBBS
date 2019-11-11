package com.scatl.uestcbbs.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entities.DailyPicBean;
import com.scatl.uestcbbs.entities.LoginBean;

import java.util.HashSet;
import java.util.Set;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 13:00
 */
public class SharePrefUtil {

    /**
     * author: sca_tl
     * description: 登陆，注销
     */
    public static void setLogin(Context context, boolean login, LoginBean loginBean){
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("login", login);
        editor.putString("name", loginBean.user_name);
        editor.putInt("uid", loginBean.uid);
        editor.putString("avatar", loginBean.avatar);
        editor.putString("token", loginBean.token);
        editor.putString("secret", loginBean.secret);
        editor.apply();
    }

    /**
     * author: sca_tl
     * description: 是否登陆
     */
    public static boolean isLogin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("login", false);
    }

    /**
     * author: TanLei
     * description:
     */
    public static void setDailyPicInfo(Context context, DailyPicBean dailyPicBean) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("dailypic", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("copy_right", dailyPicBean.copy_right);
        editor.putString("hsh", dailyPicBean.hsh);
        editor.putString("remote_url", dailyPicBean.remote_url);
        editor.apply();
    }

    public static DailyPicBean getDailyPicInfo(Context context) {
        DailyPicBean dailyPicBean = new DailyPicBean();
        SharedPreferences sharedPreferences = context.getSharedPreferences("dailypic", Context.MODE_PRIVATE);
        dailyPicBean.hsh = sharedPreferences.getString("hsh", "");
        dailyPicBean.copy_right = sharedPreferences.getString("copy_right", "");
        dailyPicBean.remote_url = sharedPreferences.getString("remote_url", "");
        return dailyPicBean;
    }

    /**
     * author: sca_tl
     * description:
     */
    public static String getAccessToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    public static int getId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("uid", Integer.MAX_VALUE);
    }

    public static String getAvatar(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString("avatar", "");
    }

    public static String getName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", "");
    }

    public static String getAccessSecret(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
        return sharedPreferences.getString("secret", "");
    }


    /**
     * author: sca_tl
     * description: 夜间模式
     */
    public static void setNightMode(Context context, boolean night){
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("night", night);
        editor.apply();
    }

    public static boolean isNightMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("night", false);
    }

    /**
     * author: sca_tl
     * description:
     */
    public static void setHomeStyle(Context context, int i) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getResources().getString(R.string.home_style), i);
        editor.apply();
    }


    public static int getHomeStyle(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(context.getResources().getString(R.string.home_style), 0);
    }

    /**
     * author: sca_tl
     * description:
     */
    public static void setHint(Context context, int hint_id, boolean hint) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hint", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hint_id_" + hint_id, hint);
        editor.apply();
    }

    public static boolean isHint(Context context, int hint_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hint", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("hint_id_" + hint_id, true);
    }

    /**
     * author: sca_tl
     * description:
     */
    public static void setDownloadedEmoticon(Context context, String s, boolean add) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("emoticon", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> strings = SharePrefUtil.getDownloadedEmoticon(context);
        if (add) strings.add(s);
        else strings.remove(s);
        editor.putStringSet("downloaded", strings);
        editor.apply();
    }

    public static Set<String> getDownloadedEmoticon(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("emoticon", Context.MODE_PRIVATE);
        return new HashSet<>(sharedPreferences.getStringSet("downloaded", new HashSet<String>()));
    }

}
