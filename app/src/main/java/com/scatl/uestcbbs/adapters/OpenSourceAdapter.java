package com.scatl.uestcbbs.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entities.OpenSourceBean;

public class OpenSourceAdapter extends BaseQuickAdapter<OpenSourceBean, BaseViewHolder> {

    public OpenSourceAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, OpenSourceBean item) {
        helper.setText(R.id.item_open_source_name, item.name)
                .setText(R.id.item_open_source_author, item.author)
                .setText(R.id.item_open_source_desc, item.description);
    }
}
