package com.scatl.uestcbbs.custom.imagebrowser;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.github.ielse.imagewatcher.ImageWatcher;
import com.scatl.uestcbbs.R;

import java.util.List;

public class MyIndexProvider implements ImageWatcher.IndexProvider {
    private boolean initLayout;
    private MyIndicatorView myIndicatorView;

    @Override
    public View initialView(Context context) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        myIndicatorView = new MyIndicatorView(context);
        myIndicatorView.setLayoutParams(lp);

        DisplayMetrics d = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(d);
        int size = (int) (20 * d.density + 0.5);
        lp.setMargins(0, 0, 0, size);

        initLayout = false;
        return myIndicatorView;
    }

    @Override
    public void onPageChanged(ImageWatcher imageWatcher, int position, List<Uri> dataList) {
        if (!initLayout) {
            initLayout = true;
            myIndicatorView.reset(dataList.size(), position, R.drawable.shape_indicator_unselected, R.drawable.shape_indicator_selected);
        } else {
            myIndicatorView.select(position, R.drawable.shape_indicator_unselected, R.drawable.shape_indicator_selected);
        }
    }
}
