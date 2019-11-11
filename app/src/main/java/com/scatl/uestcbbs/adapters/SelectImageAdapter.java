package com.scatl.uestcbbs.adapters;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/17 15:35
 */
public class SelectImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private Context context;

    public SelectImageAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void delete(int position) {
        getData().remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
       // notifyItemRangeChanged(position, getData().size() - position);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        Glide.with(context).load(item).into((ImageView) helper.getView(R.id.item_dialog_comment_img));
        helper.addOnClickListener(R.id.item_dialog_comment_delete_img);
    }
}
