package com.scatl.uestcbbs.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.fragments.AboutFragment;
import com.scatl.uestcbbs.utils.CommonUtil;

public class AboutActivity extends BaseActivity {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        coordinatorLayout = findViewById(R.id.about_coorlayout);
        toolbar = findViewById(R.id.about_toolbar);
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView version = findViewById(R.id.about_app_version);
        version.setText(CommonUtil.getVersionName(this));

        AboutFragment aboutFragment = new AboutFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.about_framelayout, aboutFragment)
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
