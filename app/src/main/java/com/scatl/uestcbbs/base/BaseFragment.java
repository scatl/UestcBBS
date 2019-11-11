package com.scatl.uestcbbs.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {

    protected View view;
    protected Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle(getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(setLayoutResourceId(), container, false);

        init();

        return view;
    }

    protected abstract int setLayoutResourceId();
    protected abstract void init();
    protected void getBundle(Bundle bundle) {}

}
