package com.scatl.uestcbbs.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.scatl.uestcbbs.R;

public abstract class BaseDialogFragment extends DialogFragment {
    protected View view;
    protected Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(setLayoutResourceId(), container, false);

        getBundle(getArguments());
        init();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置动画、位置、宽度等属性（必须放在onStart方法中）
        Window window = null;
        if (getDialog() != null) window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;
                window.setAttributes(attr);
                //设置背景，加入这句使界面水平填满屏幕
                window.setBackgroundDrawableResource(R.drawable.shape_comment_dialog);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }
    }

    protected abstract int setLayoutResourceId();
    protected abstract void init();
    protected void getBundle(Bundle bundle) {}

}
