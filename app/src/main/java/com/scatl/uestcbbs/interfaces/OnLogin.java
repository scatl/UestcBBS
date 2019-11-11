package com.scatl.uestcbbs.interfaces;

/**
 * author: sca_tl
 * description:
 * date: 2019/07/06 15:24
 */
public interface OnLogin {
    void onLoginSuccess(String response);
    void onLoginError(String response);
}
