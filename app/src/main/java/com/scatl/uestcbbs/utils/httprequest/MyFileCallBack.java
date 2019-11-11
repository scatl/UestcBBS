package com.scatl.uestcbbs.utils.httprequest;

import com.zhy.http.okhttp.callback.Callback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

public abstract class MyFileCallBack extends Callback<File> {

    @Override
    public File parseNetworkResponse(Response response, int id) throws Exception {
        return null;
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(File response, int id) {

    }
}
