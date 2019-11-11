package com.scatl.uestcbbs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.AboutActivity;
import com.scatl.uestcbbs.activities.SuggestionActivity;
import com.scatl.uestcbbs.base.BasePreferenceFragment;
import com.scatl.uestcbbs.custom.dialogs.UpdateDialog;
import com.scatl.uestcbbs.entities.UpdateBean;
import com.scatl.uestcbbs.interfaces.OnHttpRequest;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.FileUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scatl.uestcbbs.utils.TimeUtil;
import com.scatl.uestcbbs.utils.ToastUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import okhttp3.Call;

public class SettingsFragment extends BasePreferenceFragment {

    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.pref_settings);

        init();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.clear_cache))) {
            clearCacheDialog();
        }

        if (preference.getKey().equals(getString(R.string.home_style))) {
            changeHomeStyleDialog();
        }

        if (preference.getKey().equals(getString(R.string.app_update))) {
            checkUpdate(true);
        }

        if (preference.getKey().equals(getString(R.string.app_suggestion))) {
            Intent intent = new Intent(mActivity, SuggestionActivity.class);
            startActivity(intent);
        }

        if (preference.getKey().equals(getString(R.string.app_about))) {
            Intent intent = new Intent(mActivity, AboutActivity.class);
            startActivity(intent);
        }


        return super.onPreferenceTreeClick(preference);
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        findPreference(getString(R.string.app_update)).setSummary("当前版本：" + CommonUtil.getVersionName(mActivity));
        checkUpdate(false);
    }

    /**
     * author: sca_tl
     * description: 更改主页样式
     */
    private void changeHomeStyleDialog() {
        final int current = SharePrefUtil.getHomeStyle(mActivity);
        String[] items = new String[]{"综合样式", "分类样式"};
        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setTitle("首页样式")
                .setSingleChoiceItems(items, current, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (current != i) {
                            SharePrefUtil.setHomeStyle(mActivity, i);
                        }
                        dialogInterface.dismiss();
                        ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), "更改成功，快去体验吧");
                    }
                }).create();
        dialog.show();
    }


    /**
     * author: sca_tl
     * description: 检查更新
     */
    private void checkUpdate(final boolean click) {
        HttpRequestUtil.get(Constants.Api.UPDATE_URL, new OnHttpRequest() {
            @Override
            public void onRequestError(Call call, Exception e, int id) {
                if (click) ToastUtil.showToast(mActivity, "检查更新失败：" + e.getMessage());
            }

            @Override
            public void onRequestInProgress(float progress, long total, int id) { }

            @Override
            public void onRequestSuccess(String response, int id) {
                if (JSON.isValidObject(response)) {
                    UpdateBean updateBean = JSON.parseObject(response, new TypeReference<UpdateBean>(){});
                    if (updateBean.versionCode > CommonUtil.getVersionCode(mActivity)) {
                        if (click) {
                            UpdateDialog updateDialog = new UpdateDialog();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.Key.DATA, updateBean);
                            updateDialog.setArguments(bundle);
                            updateDialog.show(getChildFragmentManager(), TimeUtil.getStringMs());
                        } else {
                            findPreference(getString(R.string.app_update))
                                    .setSummary("检测到新版本：" + updateBean.versionName);
                        }

                    } else {
                        findPreference(getString(R.string.app_update))
                                .setSummary("已是最新版本：" + CommonUtil.getVersionName(mActivity));
                        if (click) ToastUtil.showToast(mActivity, "已经是最新版本，不要点啦");
                    }
                }

            }
        });
    }

    /**
     * author: sca_tl
     * description: 清理缓存
     * todo 还有其他的缓存
     */
    private void clearCacheDialog() {
        String s = FileUtil.formatDirectorySize(FileUtil.getDirectorySize(mActivity.getCacheDir())
                + FileUtil.getDirectorySize(mActivity.getExternalFilesDir(Constants.AppFilePath.IMG_PATH))
                + FileUtil.getDirectorySize(mActivity.getExternalFilesDir(Constants.AppFilePath.TEMP_PATH)));

        final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setTitle("清理缓存")
                .setMessage(getResources().getString(R.string.clear_cache_disp, s))
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button p = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FileUtil.deleteDir(mActivity.getCacheDir(), false);
                        FileUtil.deleteDir(mActivity.getExternalFilesDir(Constants.AppFilePath.IMG_PATH), false);
                        FileUtil.deleteDir(mActivity.getExternalFilesDir(Constants.AppFilePath.TEMP_PATH), false);
                        dialog.dismiss();
                        ToastUtil.showSnackBar(mActivity.getWindow().getDecorView(), "清除缓存成功");
                    }
                });
            }
        });
        dialog.show();
    }


}
