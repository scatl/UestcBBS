package com.scatl.uestcbbs.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }


    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        coordinatorLayout = findViewById(R.id.settings_coorlayout);
        toolbar = findViewById(R.id.settings_toolbar);
        toolbar.setTitle("应用设置");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_framelayout, settingsFragment)
                .commit();
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }


}
