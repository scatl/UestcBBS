package com.scatl.uestcbbs.adapters;

import android.content.Context;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.activities.EmoticonActivity;
import com.scatl.uestcbbs.custom.imageview.RoundedImageView;
import com.scatl.uestcbbs.entities.EmoticonBean;
import com.scatl.uestcbbs.utils.SharePrefUtil;

import java.util.Set;

public class EmoticonAdapter extends BaseQuickAdapter<EmoticonBean, BaseViewHolder> {

    private Context context;


    public EmoticonAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, EmoticonBean item) {
        helper.setText(R.id.item_emoticon_name, item.name)
                .setText(R.id.item_emoticon_size, item.desp)
                .addOnClickListener(R.id.item_emoticon_btn);
        Glide.with(context).load(item.sample).into((RoundedImageView) helper.getView(R.id.item_emoticon_img));

        Set<String> strings = SharePrefUtil.getDownloadedEmoticon(context);
        if (strings.contains(item.code)) {
            helper.getView(R.id.item_emoticon_btn).setTag(EmoticonActivity.BTN_DOWNLOADED);
            ((Button)helper.getView(R.id.item_emoticon_btn)).setText("删除");
        } else {
            helper.getView(R.id.item_emoticon_btn).setTag(EmoticonActivity.BTN_DOWNLOAD);
            ((Button)helper.getView(R.id.item_emoticon_btn)).setText("下载");
        }
    }
}
