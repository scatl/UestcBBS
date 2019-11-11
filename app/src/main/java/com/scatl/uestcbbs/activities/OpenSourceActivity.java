package com.scatl.uestcbbs.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.OpenSourceAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.OpenSourceBean;
import com.scatl.uestcbbs.utils.CommonUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class OpenSourceActivity extends BaseActivity {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private OpenSourceAdapter openSourceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source);
        init();
    }

    private void init() {
        coordinatorLayout = findViewById(R.id.open_source_coorlauout);
        toolbar = findViewById(R.id.open_source_toolbar);
        toolbar.setTitle("开源项目");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.open_source_rv);
        openSourceAdapter = new OpenSourceAdapter( R.layout.item_open_source);
        openSourceAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new MyLinearLayoutManger(this));
        recyclerView.setAdapter(openSourceAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        setData();
        setOnItemClick();
    }

    private void setOnItemClick() {
        openSourceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.item_open_source_cardview) {
                    CommonUtil.openBrowser(OpenSourceActivity.this, openSourceAdapter.getData().get(position).link);
                }
            }
        });
    }

    private void setData() {
        String data = "[]";
        InputStream is;
        try {
            is = getAssets().open("open_source_projects.json");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toString();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSONObject.parseObject(data);
        List<OpenSourceBean> listBeans = JSON.parseObject(jsonObject.getJSONArray("data").toString(),
                    new TypeReference<List<OpenSourceBean>>(){});
        openSourceAdapter.addData(listBeans);


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
