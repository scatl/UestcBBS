package com.scatl.uestcbbs.custom.dialogs;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.entities.UpdateBean;
import com.scatl.uestcbbs.interfaces.OnHttpFileRequest;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.FileUtil;
import com.scatl.uestcbbs.utils.httprequest.HttpRequestUtil;

import java.io.File;
import java.text.DecimalFormat;

import okhttp3.Call;

public class UpdateDialog extends BaseDialogFragment implements View.OnClickListener {

    private TextView title, content;
    private ProgressBar progressBar;
    private UpdateBean updateBean;
    private Button download;
    private TextView cancel, progress_text;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.dialog_update;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            updateBean = (UpdateBean) bundle.getSerializable(Constants.Key.DATA);
        }
    }

    @Override
    protected void init() {
        title = view.findViewById(R.id.dialog_update_title);
        progress_text = view.findViewById(R.id.dialog_update_progress_text);
        content = view.findViewById(R.id.dialog_update_content);
        progressBar = view.findViewById(R.id.dialog_update_progressbar);
        download = view.findViewById(R.id.dialog_update_download);
        download.setOnClickListener(this);
        cancel = view.findViewById(R.id.dialog_update_cancel);
        cancel.setOnClickListener(this);

        title.setText(updateBean.title);
        content.setText(Html.fromHtml(updateBean.updateContent));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_update_download:
                download.setText("下载中...");
                download.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                startDownload();
                break;

            case R.id.dialog_update_cancel:
                dismiss();
                break;

            default:
                break;
        }
    }

    /**
     * author: sca_tl
     * description: 下载安装包
     */
    private void startDownload() {
        String dir = mActivity.getExternalFilesDir(Constants.AppFilePath.TEMP_PATH).getAbsolutePath();
        String apkName = updateBean.apkUrl.substring(updateBean.apkUrl.lastIndexOf("/"));
        final File apkFile = new File(dir + apkName);

        if(apkFile.exists() && apkFile.isFile() && FileUtil.getFileMD5(apkFile).equalsIgnoreCase(updateBean.MD5)){
            FileUtil.installApk(mActivity, apkFile);
            download.setText("立即安装");
            download.setClickable(true);
            progressBar.setVisibility(View.GONE);
            progress_text.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
        } else {

            HttpRequestUtil.getFile(updateBean.apkUrl, dir, apkName, new OnHttpFileRequest() {
                @Override
                public void onRequestError(Call call, Exception e, int id) {
                    download.setClickable(true);
                    download.setText("立即下载");
                }

                @Override
                public void onRequestInProgress(float progress, long total, int id) {
                    progressBar.setProgress((int)(100 * progress));
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    String s = decimalFormat.format(progress*100);
                    float m = (total*progress)/1024/1024;
                    double q = (total*1.00)/1024/1024;
                    String l = decimalFormat.format(m);
                    String w = decimalFormat.format(q);
                    progress_text.setText(String.valueOf("已下载:"+l+"MB"+"("+s+"%)/"+w+"MB"));
                }

                @Override
                public void onRequestSuccess(File response, int id) {
                    if(apkFile.exists() && apkFile.isFile()){
                        FileUtil.installApk(mActivity, apkFile);
                        download.setText("立即安装");
                        download.setClickable(true);
                        progressBar.setVisibility(View.VISIBLE);
                        cancel.setVisibility(View.GONE);
                    }
                }
            });


        }

    }
}
