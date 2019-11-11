package com.scatl.uestcbbs.custom.imagebrowser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.ielse.imagewatcher.ImageWatcher;


public class MyGlideLoader implements ImageWatcher.Loader {
    @Override
    public void load(Context context, Uri uri, final ImageWatcher.LoadCallback lc) {
        RequestOptions op = new RequestOptions()
                //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .format(DecodeFormat.PREFER_RGB_565);
        Glide.with(context).load(uri).apply(op)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        lc.onResourceReady(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        lc.onLoadFailed(errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        lc.onLoadStarted(placeholder);
                    }
                });
    }
}
