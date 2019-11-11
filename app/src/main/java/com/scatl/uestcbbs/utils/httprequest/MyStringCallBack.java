package com.scatl.uestcbbs.utils.httprequest;

import com.alibaba.fastjson.JSONObject;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 13:51
 */
public abstract class MyStringCallBack extends Callback<String> {
    @Override
    public boolean validateReponse(Response response, int id) {
        return true;
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {

        if(response.code() >= 200 && response.code() < 300){
            if (response.body() == null )
                return "the response is null";
            return response.body().string();
        } else {
            int code = response.code();
            String s = response.body() == null ? "null" : response.body().string();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", code);
            jsonObject.put("response", s);
            throw new Exception(jsonObject.toJSONString());
        }

    }

}
