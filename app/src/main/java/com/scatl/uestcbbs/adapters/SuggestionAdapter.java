package com.scatl.uestcbbs.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entities.SuggestionBean;
import com.scatl.uestcbbs.utils.TimeUtil;

import java.util.List;

public class SuggestionAdapter extends BaseQuickAdapter<SuggestionBean, BaseViewHolder> {

    private Context mContext;

    public SuggestionAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.mContext = context;
    }

    public void addSuggestionData(List<SuggestionBean> data, boolean refresh) {
        if (refresh) {setNewData(data);} else {addData(data);}
    }

    @Override
    protected void convert(BaseViewHolder helper, SuggestionBean item) {
        helper.setText(R.id.item_suggestion_content, "内容：" + item.content)
                .setText(R.id.item_suggestion_time, "时间：" + TimeUtil.getFormatDate(item.time, "yyyy年MM月dd日 HH:mm"))
                .setText(R.id.item_suggestion_status, "状态：" + item.status_desp);
        if (TextUtils.isEmpty(item.reply)) {
            helper.getView(R.id.item_suggestion_reply).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.item_suggestion_reply).setVisibility(View.VISIBLE);
            helper.setText(R.id.item_suggestion_reply, "回复：" + item.reply);
        }
    }
}
