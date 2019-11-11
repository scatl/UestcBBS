package com.scatl.uestcbbs.adapters;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.entities.PostDetailBean;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/25 11:25
 */
public class PollAdapter extends BaseQuickAdapter<PostDetailBean.PostPollBasicBean.PostPollContentBean, BaseViewHolder> {

    private Context context;
    private int total;
    private int poll_status;
    private List<Integer> ids = new ArrayList<>();

    public PollAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addPollData(List<PostDetailBean.PostPollBasicBean.PostPollContentBean> data, int total, int poll_status) {
        setNewData(data);
        this.total = total;
        this.poll_status = poll_status;
    }

    public List<Integer> getPollItemIds() {
        return ids;
    }

    @Override
    protected void convert(BaseViewHolder helper, final PostDetailBean.PostPollBasicBean.PostPollContentBean item) {
        CheckBox checkBox = helper.getView(R.id.item_poll_checkbox);
        ProgressBar progressBar = helper.getView(R.id.item_poll_progress);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (! ids.contains(item.poll_item_id)) {
                        ids.add(item.poll_item_id);
                    }
                } else {
                    if (ids.contains(item.poll_item_id)) {
                        ids.remove(Integer.valueOf(item.poll_item_id));
                    }
                }
            }
        });


        checkBox.setEnabled(poll_status == 2);
        checkBox.setText(context.getResources().getString(R.string.vote_item_voted_num,
                item.name, item.total_num, item.percent));

        progressBar.setMax(total);
        progressBar.setProgress(item.total_num);

    }
}
