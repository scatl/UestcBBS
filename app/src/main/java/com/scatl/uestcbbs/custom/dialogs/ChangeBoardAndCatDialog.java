package com.scatl.uestcbbs.custom.dialogs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseDialogFragment;
import com.scatl.uestcbbs.base.BaseEvent;
import com.scatl.uestcbbs.utils.Constants;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ChangeBoardAndCatDialog extends BaseDialogFragment {

    TagFlowLayout tagFlowLayout;
    TextView title;

    private ArrayList<String> item;
    private String type;
    private int current_select;

    public static final String TYPE_CAT = "cat";
    public static final String TYPE_SUB_BOARD = "sub_board";

    @Override
    protected int setLayoutResourceId() {
        return R.layout.dialog_change_board_and_cat;
    }

    @Override
    protected void getBundle(Bundle bundle) {
        if (bundle != null) {
            type = bundle.getString(Constants.Key.TYPE);
            item = bundle.getStringArrayList(Constants.Key.DATA);
            current_select = bundle.getInt(Constants.Key.CURRENT_SELECT, Integer.MAX_VALUE);
        }
    }

    @Override
    protected void init() {

        tagFlowLayout = view.findViewById(R.id.dialog_change_board_and_cat_taglayout);
        title = view.findViewById(R.id.text14);

        if (TYPE_CAT.equals(type)) { title.setText("选择板块分类"); }
        if (TYPE_SUB_BOARD.equals(type)) { title.setText("选择子版块"); }

        TagAdapter<String> tagAdapter = new TagAdapter<String>(item) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = new TextView(mActivity);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(16);
                textView.setText(s);
                textView.setTextColor(current_select == position ?
                        Color.WHITE : getResources().getColor(R.color.colorPrimary));
                textView.setBackgroundResource(R.drawable.shape_item_dialog_board_cat);
                return textView;
            }
        };
        tagFlowLayout.setAdapter(tagAdapter);
        tagAdapter.setSelectedList(current_select);

        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (TYPE_CAT.equals(type)) {
                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.BOARD_CAT_CHANGE, position));
                }
                if (TYPE_SUB_BOARD.equals(type)) {
                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SUB_BOARD_CHANGE, position));
                }
                dismiss();
                return true;
            }
        });

    }


}
