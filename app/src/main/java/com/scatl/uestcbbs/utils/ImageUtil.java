package com.scatl.uestcbbs.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.SparseArray;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.github.ielse.imagewatcher.ImageWatcher;
import com.github.ielse.imagewatcher.ImageWatcherHelper;
import com.scatl.uestcbbs.custom.imagebrowser.MyGlideLoader;
import com.scatl.uestcbbs.custom.imagebrowser.MyIndexProvider;

import java.util.ArrayList;
import java.util.List;

public class ImageUtil {
    /**
     * author: TanLei
     * description: 图片模糊
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurPhoto(Context context, Bitmap bitmap, float radius){
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation in = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation out = Allocation.createFromBitmap(renderScript, result);
        blur.setRadius(radius);
        blur.setInput(in);
        blur.forEach(out);
        out.copyTo(result);
        renderScript.destroy();
        return result;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * author: TanLei
     * description: 多图浏览
     */
    public static void showImages(Activity activity, ImageView imageView, List<String> urls, int selected) {
        ImageWatcherHelper imageWatcherHelper = ImageWatcherHelper
                .with(activity, new MyGlideLoader())
                .setIndexProvider(new MyIndexProvider())
                .setOnPictureLongPressListener(new ImageWatcher.OnPictureLongPressListener() {
                    @Override
                    public void onPictureLongPress(ImageView imageView, Uri uri, int i) {
                        //TODO 保存图片
                    }
                });
        final SparseArray<ImageView> mappingViews = new SparseArray<>();
        mappingViews.put(selected, (ImageView) imageView);
        imageWatcherHelper.show((ImageView) imageView, mappingViews, convert(urls));
    }

    public static List<Uri> convert(List<String> data) {
        List<Uri> list = new ArrayList<>();
        for (String d : data) list.add(Uri.parse(d));
        return list;
    }

    /**
     * author: sca_tl
     * description: 图片压缩
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap bitmapCompress(Bitmap bitmap){
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int bitmap_size = bitmap.getByteCount() / 1024 / 1024;

        if (bitmap_size < 5) {  //占用＜5MB，不压缩
            return bitmap;

        } else if (bitmap_size <= 7){
            if (height / width > 3) {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, width / 2, height / 2, true);
                new_bitmap.setHeight(new_bitmap.getHeight() / 2);
                return new_bitmap;
            } else {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, width / 2, height / 2, true);
                return new_bitmap;
            }

        } else if (bitmap_size <= 10) {
            if (height / width > 3) {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 2.2), (int)(height / 2.2), true);
                new_bitmap.setHeight(new_bitmap.getHeight() / 2);
                return new_bitmap;
            } else {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 2.2), (int)(height / 2.2), true);
                return new_bitmap;
            }

        } else {
            if (height / width > 3) {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 3), (int)(height / 3), true);
                new_bitmap.setHeight(new_bitmap.getHeight() / 2);
                return new_bitmap;
            } else {
                Bitmap new_bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width / 3), (int)(height / 3), true);
                return new_bitmap;
            }
        }

    }

}
