package com.scatl.uestcbbs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.OpenSourceActivity;
import com.scatl.uestcbbs.base.BasePreferenceFragment;

public class AboutFragment extends BasePreferenceFragment {
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("settings");
        addPreferencesFromResource(R.xml.perf_about);

        init();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.about_developer_mail))) {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:sca_tl@foxmail.com"));
            startActivity(data);
        }

        if (preference.getKey().equals(getString(R.string.about_open_source))) {
            Intent intent = new Intent(mActivity, OpenSourceActivity.class);
            startActivity(intent);
        }
        return super.onPreferenceTreeClick(preference);
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
    }


}
