package com.scatl.uestcbbs.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.PostDraftAdapter;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.databases.PostDraftDataBase;
import com.scatl.uestcbbs.entities.PostDraftBean;
import com.scatl.uestcbbs.interfaces.OnRefresh;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.RefreshUtil;
import com.scatl.uestcbbs.utils.SharePrefUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class PostDraftActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private PostDraftAdapter postDraftAdapter;
    private SmartRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_draft);

        init();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void init() {
        recyclerView = findViewById(R.id.post_draft_rv);
        refreshLayout = findViewById(R.id.post_draft_refresh);
        refreshLayout.setEnableLoadMore(false);
        hint = findViewById(R.id.post_draft_hint);

        toolbar = findViewById(R.id.post_draft_toolbar);
        toolbar.setTitle("我的草稿");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postDraftAdapter = new PostDraftAdapter(this, R.layout.item_post_draft);
        postDraftAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(postDraftAdapter);
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_top);
        recyclerView.setLayoutAnimation(layoutAnimationController);

        showWarningDialog();
        setDraftData(true);
//        refreshLayout.autoRefresh(0, 300, 1, false);
        setOnRefreshListener();
        setOnDraftClickListener();
    }

    /**
     * author: sca_tl
     * description: 显示警告对话框
     */
    private void showWarningDialog() {
        if (SharePrefUtil.isHint(this, Constants.HintId.POST_DRAFT_WARNING_HINT)) {
            final AlertDialog dialog = new AlertDialog.Builder(PostDraftActivity.this)
                    .setTitle("请注意")
                    .setMessage(getString(R.string.post_draft_warning))
                    .setPositiveButton("我晓得了", null)
                    .setNegativeButton("不再提醒", null)
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface d) {
                    Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    n.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            SharePrefUtil.setHint(PostDraftActivity.this, Constants.HintId.POST_DRAFT_WARNING_HINT, false);
                        }
                    });
                }
            });
            dialog.show();
        }
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setDraftData(boolean refresh) {
        PostDraftDataBase postDraftDataBase = new PostDraftDataBase(this, PostDraftDataBase.DATA_BASE_NAME, null, PostDraftDataBase.DATA_BASE_VERSION);
        SQLiteDatabase database = postDraftDataBase.getWritableDatabase();

        List<PostDraftBean> postDraftBeanList = new ArrayList<>();

        Cursor cursor = database.rawQuery("select * from postdraft", new String[]{});
        if (cursor.moveToFirst()) {
            do {
                PostDraftBean postDraftBean = new PostDraftBean();
                postDraftBean.time = cursor.getLong(cursor.getColumnIndex("time"));
                postDraftBean.title = cursor.getString(cursor.getColumnIndex("title"));
                postDraftBean.content = cursor.getString(cursor.getColumnIndex("content"));
                postDraftBean.cat_name = cursor.getString(cursor.getColumnIndex("cat_name"));
                postDraftBean.board_name = cursor.getString(cursor.getColumnIndex("board_name"));
                postDraftBean.cat_id = cursor.getInt(cursor.getColumnIndex("cat_id"));
                postDraftBean.board_name = cursor.getString(cursor.getColumnIndex("board_name"));
                postDraftBean.board_id = cursor.getInt(cursor.getColumnIndex("board_id"));

                postDraftBeanList.add(postDraftBean);
            } while (cursor.moveToNext());

            hint.setVisibility(View.GONE);
        } else {
            hint.setVisibility(View.VISIBLE);
        }
        cursor.close();

        postDraftAdapter.addPostReplyData(postDraftBeanList, refresh);
        refreshLayout.finishRefresh();
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnDraftClickListener() {

        postDraftAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.post_draft_root_view:
                        Intent intent = new Intent(PostDraftActivity.this, CreatePostActivity.class);
                        intent.putExtra("board_id", postDraftAdapter.getData().get(position).board_id);
                        intent.putExtra("cat_id", postDraftAdapter.getData().get(position).cat_id);
                        intent.putExtra("board_name", postDraftAdapter.getData().get(position).board_name);
                        intent.putExtra("cat_name", postDraftAdapter.getData().get(position).cat_name);
                        intent.putExtra("title", postDraftAdapter.getData().get(position).title);
                        intent.putExtra("content", postDraftAdapter.getData().get(position).content);
                        intent.putExtra("time", postDraftAdapter.getData().get(position).time);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });

        postDraftAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                switch (view.getId()) {
                    case R.id.post_draft_root_view:
                        final AlertDialog dialog = new AlertDialog.Builder(PostDraftActivity.this)
                                .setTitle("删除草稿")
                                .setMessage("确认删除该草稿吗？删除后不可恢复！")
                                .setPositiveButton("取消", null)
                                .setNegativeButton("确认", null)
                                .create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface d) {
                                Button n = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                n.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        PostDraftDataBase.deleteEditorData(PostDraftActivity.this,
                                                postDraftAdapter.getData().get(position).time);
                                        postDraftAdapter.delete(position);
                                        //setDraftData(true);
                                    }
                                });
                            }
                        });
                        dialog.show();
                        break;

                    default:
                        break;
                }

                return true;
            }
        });
    }

    /**
     * author: sca_tl
     * description:
     */
    private void setOnRefreshListener(){
        RefreshUtil.setOnRefreshListener(this, refreshLayout, new OnRefresh() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                setDraftData(true);
            }

            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {

            }
        });
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
