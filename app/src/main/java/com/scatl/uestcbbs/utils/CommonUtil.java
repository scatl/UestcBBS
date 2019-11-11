package com.scatl.uestcbbs.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 10:44
 */
public class CommonUtil {
    public static int getAttrColor(Context context, int resId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, typedValue, true);
        return context.getResources().getColor(typedValue.resourceId);
    }

    /**
     * author: sca_tl
     * description: 改变svg图片颜色
     */
    public static void setVectorColor(Context context, ImageView imageView, int drawable, int color) {
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(context.getResources(), drawable, context.getTheme());
        vectorDrawableCompat.setTint(context.getResources().getColor(color));
        imageView.setImageDrawable(vectorDrawableCompat);
    }


    public static String getAppHashValue() {
        String timeString = String.valueOf(System.currentTimeMillis());
        String authKey = "appbyme_key";
        String authString = timeString.substring(0, 5) + authKey;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashKey = md.digest(authString.getBytes());
        return new BigInteger(1, hashKey).toString(16).substring(8, 16);//16进制转换字符串
    }

    /**
     * author: TanLei
     * description: 打开浏览器
     */
    public static void openBrowser(Context context, String url) {
        try{
            Intent intent= new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.getMessage();
        }

    }

    /**
     * author: TanLei
     * description: 复制文本到剪切板
     */
    public static void clipToClipBoard(Context context, String s){
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("1", s));
        ClipData.Item item= clipboardManager.getPrimaryClip().getItemAt(0);
        ToastUtil.showToast(context, item.getText().toString().equals(s) ? "复制成功" : "复制失败，请重试");
    }

    public static boolean isNetWorkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSoftKeyboard(final Context context, final View view) {
        if (view != null) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) { imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT); }
                }
            }, 0);
        }
    }

    /**
     * author: sca_tl
     * description: 分享
     */
    public static void share(Context context, String title, String content) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent = Intent.createChooser(shareIntent, title);
        context.startActivity(shareIntent);
    }

    /**
     * author: sca_tl
     * description: 获取版本号和版本名
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * author: sca_tl
     * description: 数组里是否有某个元素
     */
    public static boolean isArrayContainsValue(Object[] arr, Object targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * author: sca_tl
     * description: 键盘高度
     */
    public static int keyBoardHeight(Activity activity) {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        return screenHeight - r.bottom;
    }

    /**
     * author: sca_tl
     * description: 屏幕dp宽度
     */
    public static int screenDpHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return px2dip(context, dm.widthPixels);

    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
