package com.scatl.uestcbbs.interfaces;

import okhttp3.Call;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 13:40
 */
public interface OnHttpRequest {
    void onRequestError(Call call, Exception e, int id);
    void onRequestInProgress(float progress, long total, int id);
    void onRequestSuccess(String response, int id);
}
