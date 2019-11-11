package com.scatl.uestcbbs.custom.emoticon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.scatl.uestcbbs.DeviceConfig;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.utils.CommonUtil;
import com.scatl.uestcbbs.utils.Constants;
import com.scatl.uestcbbs.utils.SharePrefUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmoticonPanelLayout extends RelativeLayout {

    private LayoutInflater inflater;
    private LinearLayout root_layout;

    private ViewPager viewPager;
    private MagicIndicator indicator;
    private TextView hint;

    private View emotion_btn;
    private View focus_view;
    private View parent_view;

    public EmoticonPanelLayout(Context context) {
        super(context);
        //init();
    }

    public EmoticonPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public EmoticonPanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init();
    }


    public EmoticonPanelLayout init() {
        //setVisibility(VISIBLE);
        inflater = LayoutInflater.from(getContext());
        RelativeLayout root_view = (RelativeLayout) inflater.inflate(R.layout.view_emoticon_panel_root, new RelativeLayout(getContext()));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, DeviceConfig.KEYBOARD_HEIGHT);
        //layoutParams.bottomMargin = CommonUtil.dip2px(getContext(), 10);
        root_view.setLayoutParams(layoutParams);
        viewPager = root_view.findViewById(R.id.view_emoticon_panel_pager);
        indicator = root_view.findViewById(R.id.view_emoticon_panel_indicator);
        hint = root_view.findViewById(R.id.view_emoticon_panel_hint);
        //RelativeLayout trans = root_view.findViewById(R.id.view_emoticon_panel_trans_view);
        //RelativeLayout panel_rl = root_view.fb
        initEmoticonPanel();
        addView(root_view);

        return this;
    }

    public void initEmotionPanel() {

    }

    @SuppressLint("ClickableViewAccessibility")
    public EmoticonPanelLayout bindEditText(final View focusView) {
        this.focus_view = focusView;
        focus_view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setVisibility(GONE);
                CommonUtil.showSoftKeyboard(getContext(), focusView);
                return false;
            }
        });

        return this;
    }

    public EmoticonPanelLayout bindEmotionBtn(View button) {
        this.emotion_btn = button;
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPanelShown()) {
                    CommonUtil.showSoftKeyboard(getContext(), focus_view);
                    setVisibility(GONE);
                } else {
                    CommonUtil.hideSoftKeyboard(getContext(), focus_view);
                    setVisibility(VISIBLE);
                }
            }
        });
        return this;
    }

    public EmoticonPanelLayout bindParentView(View view) {
        this.parent_view = view;
        return this;
    }

    private boolean isPanelShown() {
        return getVisibility() == VISIBLE;
    }


    /**
     * author: sca_tl
     * description: 指示器
     */
    private void initEmoticonPanel() {

        final List<View> gridViewList = new ArrayList<>();
        final List<String> title_img_path = new ArrayList<>();

        //Set<String> strings = SharePrefUtil.getDownloadedEmoticon(getContext());
        List<String> list = new ArrayList<>(SharePrefUtil.getDownloadedEmoticon(getContext()));
        if (list.size() == 0) {
            hint.setText(getResources().getString(R.string.download_emoticon));
        } else {
            for (int j = 0; j < list.size(); j ++) {
                List<String> img_path = new ArrayList<>();
                File file = new File(getContext().getExternalFilesDir(Constants.AppFilePath.EMOTICON_PATH)
                        .getAbsolutePath() + "/" + list.get(j));
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i ++) {
                        img_path.add(files[i].getAbsolutePath());
                        Log.e("UUUUUUUUUUU", img_path.get(i));
                    }


                    GridView gridView = new GridView(getContext());
                    gridView.setVerticalSpacing(CommonUtil.dip2px(getContext(), 20));
                    gridView.setHorizontalSpacing(CommonUtil.dip2px(getContext(), 20));
                    gridView.setNumColumns(CommonUtil.screenDpHeight(getContext()) / 60);
                    gridView.setAdapter(new EmoticonGridViewAdapter(getContext(), img_path));
                    gridView.setVerticalScrollBarEnabled(false);
                    gridViewList.add(gridView);
                    title_img_path.add(img_path.get(0));
                }



            }

        }

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new EmoticonPagerAdapter(gridViewList));
        viewPager.setCurrentItem(0);

        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() { return title_img_path.size(); }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                MyPagerTitleView myPagerTitleView = new MyPagerTitleView(context);
                myPagerTitleView.setImageSource(title_img_path.get(i));
                myPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(i);
                    }
                });
                return myPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {return null;}
        });

        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, viewPager);
    }

}
