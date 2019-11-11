package com.scatl.uestcbbs.utils.httprequest;

import com.scatl.uestcbbs.interfaces.OnHttpFileRequest;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 13:38
 */
public class HttpRequestUtil {

    public static void post(String url, Map<String, String> params, final OnHttpRequest onHttpRequest) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .writeTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        onHttpRequest.onRequestError(call, e, id);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        onHttpRequest.onRequestInProgress(progress, total, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        onHttpRequest.onRequestSuccess(response, id);
                    }
                });

    }

    public static void postString(String url, String string, final OnHttpRequest onHttpRequest){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
        OkHttpUtils.postString()
                .url(url)
                .content(string)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        onHttpRequest.onRequestError(call, e, id);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        onHttpRequest.onRequestInProgress(progress, total, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        onHttpRequest.onRequestSuccess(response, id);
                    }
                });
    }

    public static void get(String url, final OnHttpRequest onHttpRequest){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .writeTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        onHttpRequest.onRequestError(call, e, id);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        onHttpRequest.onRequestInProgress(progress, total, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        onHttpRequest.onRequestSuccess(response, id);
                    }
                });
    }

    //下载文件
    public static void getFile(String url, String path, String name, final OnHttpFileRequest onHttpFileRequest) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .writeTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(path, name) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        onHttpFileRequest.onRequestError(call, e, id);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        onHttpFileRequest.onRequestInProgress(progress, total, id);
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        onHttpFileRequest.onRequestSuccess(response, id);
                    }
                });
    }

    public static void getFile(String url, String path, String name) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .writeTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(path, name) {
                    @Override
                    public void onError(Call call, Exception e, int id) { }

                    @Override
                    public void inProgress(float progress, long total, int id) { }

                    @Override
                    public void onResponse(File response, int id) { }
                });
    }


    //表单形式上传文件
    public static void postFormFile(String url, List<File> files, Map<String, String> params, final OnHttpRequest onHttpRequest){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .writeTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        PostFormBuilder postFormBuilder = OkHttpUtils.post();
        for (int i = 0; i < files.size(); i ++) {
            postFormBuilder.addFile("uploadFile[]", files.get(i).getName(), files.get(i));
        }

        postFormBuilder
                .url(url)
                .params(params)
                .addHeader("content-type","multipart/form-data")
                .build()
                .execute(new MyStringCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        onHttpRequest.onRequestError(call, e, id);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        onHttpRequest.onRequestInProgress(progress, total, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        onHttpRequest.onRequestSuccess(response, id);
                    }
                });

    }


}
