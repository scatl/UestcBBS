package com.scatl.uestcbbs.adapters.homepage1;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.adapters.SimplePostAdapter;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.entities.DailyPicBean;
import com.scatl.uestcbbs.entities.HomePage2MultipleItem;
import com.scatl.uestcbbs.entities.PostListBean;

import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/19 12:36
 */
public class HomePageAdapter1 extends BaseMultiItemQuickAdapter<HomePage2MultipleItem, BaseViewHolder> {

    private Context context;
    private SimplePostAdapter simplePostAdapter;
    private HotPostAdapter1 hotPostAdapter1;

    private DailyPicBean dailyPicBean = new DailyPicBean();

    private OnItemClickListener onItemClickListener;
    private OnItemChildClickListener onItemChildClickListener;

    private onItemClickListener mOnItemClickListener;

    public static final int TYPE_DAILY_PIC = 0;
    public static final int TYPE_HOT_POST = 1;
    public static final int TYPE_SIMPLE_POST = 2;

    public HomePageAdapter1(Context context, List<HomePage2MultipleItem> data) {
        super(data);
        this.context = context;

        addItemType(TYPE_DAILY_PIC, R.layout.item_home_page_dailypic);
        addItemType(TYPE_HOT_POST, R.layout.item_home_page_hot_post);
        addItemType(TYPE_SIMPLE_POST, R.layout.item_home_page_latest_post);

        init();
    }


    /**
     * author: sca_tl
     * description: 初始化
     */
    private void init() {
        hotPostAdapter1 = new HotPostAdapter1(context, R.layout.item_item_home_page_hot_post);
        hotPostAdapter1.setHasStableIds(true);
        simplePostAdapter = new SimplePostAdapter(context, R.layout.item_post);
        simplePostAdapter.setType(SimplePostAdapter.TYPE_REPLY_DATE);
        simplePostAdapter.setHasStableIds(true);

        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mOnItemClickListener.onItemClick(view, position);
            }
        };

        onItemChildClickListener = new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mOnItemClickListener.onItemChildClick(view, position);
            }
        };

        hotPostAdapter1.setOnItemClickListener(onItemClickListener);
        hotPostAdapter1.setOnItemChildClickListener(onItemChildClickListener);
        simplePostAdapter.setOnItemClickListener(onItemClickListener);
        simplePostAdapter.setOnItemChildClickListener(onItemChildClickListener);

    }

    /**
     * author: sca_tl
     * description: 添加数据
     */
    public void addHotPostData(List<PostListBean> data, boolean refresh) {
        hotPostAdapter1.addPostData(data, refresh);
    }

    public void addSimplePostData(List<PostListBean> data, boolean refresh) {
        simplePostAdapter.addPostData(data, refresh);
    }

    public void addDailyPicData(DailyPicBean dailyPicBean) {
        this.dailyPicBean = dailyPicBean;
        notifyItemChanged(TYPE_DAILY_PIC);
    }

    public List<PostListBean> getSimplePostData() {
        return simplePostAdapter.getData();
    }

    public List<PostListBean> getHotPostData() {
        return hotPostAdapter1.getData();
    }

    public DailyPicBean getDailyPicBean() {
        return dailyPicBean;
    }


    @Override
    protected void convert(BaseViewHolder helper, HomePage2MultipleItem item) {

        switch (helper.getItemViewType()) {

            case TYPE_DAILY_PIC:
                Glide.with(context)
                        .load(dailyPicBean.remote_url)
                        .transition(new DrawableTransitionOptions().crossFade(450))
                        .into((ImageView) helper.getView(R.id.home_page_dailypic_img));
                helper.setText(R.id.home_page_dailypic_copyright, dailyPicBean.copy_right)
                        .addOnClickListener(R.id.daily_pic_fullscreen);

                break;

            case TYPE_HOT_POST:
                RecyclerView hot_post_rv = helper.getView(R.id.main_page_hot_post_rv);
                LinearLayoutManager linearLayoutManager = new MyLinearLayoutManger(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                hot_post_rv.setLayoutManager(linearLayoutManager);
                hot_post_rv.setNestedScrollingEnabled(false);
                hot_post_rv.setFocusableInTouchMode(false);
                hot_post_rv.setFocusable(false);
                hot_post_rv.setAdapter(hotPostAdapter1);

                break;

            case TYPE_SIMPLE_POST:
                RecyclerView simple_post_rv = helper.getView(R.id.home_page_simple_post_rv);
                simple_post_rv.setLayoutManager(new MyLinearLayoutManger(context));
                simple_post_rv.setNestedScrollingEnabled(false);
                simple_post_rv.setFocusableInTouchMode(false);
                simple_post_rv.setFocusable(false);
                simple_post_rv.setAdapter(simplePostAdapter);

                break;

            default:
                break;

        }
    }


    public interface onItemClickListener {
        void onItemClick(View view, int position);
        void onItemChildClick(View view, int position);
    }

    public void setOnItemClickListener (onItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
