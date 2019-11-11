package com.scatl.uestcbbs.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.custom.posteditor.ContentEditor;
import com.scatl.uestcbbs.entities.PostDraftBean;

import java.util.List;

public class PostDraftAdapter extends BaseQuickAdapter<PostDraftBean, BaseViewHolder> {

    private Context context;

    public PostDraftAdapter(Context context, int layoutResId) {
        super(layoutResId);
        this.context = context;
    }

    public void addPostReplyData(List<PostDraftBean> list, boolean isRefresh){
        if( isRefresh) { setNewData(list);} else { addData(list); }
    }

    public void delete(int position) {
        getData().remove(position);
        notifyItemRemoved(position);
        //notifyDataSetChanged();
        // notifyItemRangeChanged(position, getData().size() - position);
    }

    @Override
    protected void convert(BaseViewHolder helper, PostDraftBean item) {
        helper.setText(R.id.item_post_draft_title, item.title);

        String content_summary = "";
        String image_summary = "";
        JSONArray jsonArray = JSONObject.parseArray(item.content);
        if (jsonArray != null && jsonArray.size() > 0) {
            boolean content_found = false;
            boolean image_found = false;
            for (int i = 0; i < jsonArray.size(); i ++) {
                int type = jsonArray.getJSONObject(i).getIntValue("content_type");
                String content = jsonArray.getJSONObject(i).getString("content");
                if (!content_found && type == ContentEditor.CONTENT_TYPE_TEXT && !TextUtils.isEmpty(content)) {
                    content_found = true;
                    content_summary = content;
                }
                if (!image_found && type == ContentEditor.CONTENT_TYPE_IMAGE) {
                    image_found = true;
                    image_summary = content;
                }

                if (content_found && image_found) break;
            }
        }

        helper.setText(R.id.item_post_draft_content, content_summary);
        if (TextUtils.isEmpty(image_summary)) {
            helper.getView(R.id.item_post_draft_img).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.item_post_draft_img).setVisibility(View.VISIBLE);
            Glide.with(context).load(image_summary).into((ImageView) helper.getView(R.id.item_post_draft_img));
        }

    }
}
