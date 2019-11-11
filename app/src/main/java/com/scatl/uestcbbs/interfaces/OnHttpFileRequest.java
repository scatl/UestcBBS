package com.scatl.uestcbbs.interfaces;

import java.io.File;

import okhttp3.Call;

public interface OnHttpFileRequest {
    void onRequestError(Call call, Exception e, int id);
    void onRequestInProgress(float progress, long total, int id);
    void onRequestSuccess(File response, int id);
}
